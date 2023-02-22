package com.unstoppable.manager;

import ch.qos.logback.classic.turbo.TurboFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unstoppable.common.dto.ItemDTO;
import com.unstoppable.common.enums.SysConstant;
import com.unstoppable.entity.ItemKill;
import com.unstoppable.entity.ItemKillSuccess;
import com.unstoppable.service.ItemKillService;
import com.unstoppable.service.ItemKillSuccessService;
import com.unstoppable.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Author:WJ
 * Date:2023/2/12 11:01
 * Description:<>
 */
@Slf4j
@Service
public class KillManager {

    private SnowFlake snowFlake = new SnowFlake(2, 3);

    @Autowired
    private ItemKillSuccessService itemKillSuccessService;
    @Autowired
    private ItemManager itemManager;

    @Transactional(rollbackFor = Exception.class)
    public Boolean killItem(Integer killId, Integer userId) throws Exception {

        // 查询该用户是否已抢购该商品
        LambdaQueryWrapper<ItemKillSuccess> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemKillSuccess::getKillId, killId).eq(ItemKillSuccess::getUserId, userId);
        ItemKillSuccess itemKillSuccess = itemKillSuccessService.getOne(queryWrapper);
        if (itemKillSuccess != null) {
            log.info("you had been bought the item");
            throw new Exception("you had been bought the item");
        }

        //判断是否可以被秒杀
        ItemDTO itemDTO = itemManager.selectById(killId);
        if (itemDTO==null||itemDTO.getCanKill()!=1){
            log.info("the item cannot be gotten");
            throw new Exception("the item cannot be gotten");
        }
        //库存减1
        boolean flag = itemManager.updateById(killId);
        log.info("huigunle");

        if (!flag){
            log.info("kill failure");
            return false;
        }
        commonRecordKillSuccessInfo(itemDTO,userId);
        log.info("kill success");
        return true;
    }

    private void commonRecordKillSuccessInfo(ItemDTO itemDTO, Integer userId) {

        ItemKillSuccess entity = new ItemKillSuccess();
        String orderNo = String.valueOf(snowFlake.nextId());

        entity.setCode(orderNo); // 雪花算法
        entity.setItemId(itemDTO.getItemId());
        entity.setKillId(itemDTO.getId());
        entity.setUserId(userId);
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());

        try {
            itemKillSuccessService.save(entity);
            log.info("save orderInfo success...");
            log.info("user"+userId+" add success "+entity.getCode());
            // TODO: 2023/2/13 rabbitmq发送邮件
        } catch (Exception e) {
            log.info("save orderInfo failure");
            e.printStackTrace();
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * Redis的分布式锁优化
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {

        // 生成redis锁
        final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
        final int value = new Random().nextInt(1000);
        Boolean cacheRes = redisTemplate.opsForValue().setIfAbsent(key, value);

        if (cacheRes){
            log.info("the current lock is generated");
            redisTemplate.expire(key,30, TimeUnit.SECONDS);
            try {
                // 查询该用户是否已抢购该商品
                LambdaQueryWrapper<ItemKillSuccess> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ItemKillSuccess::getKillId, killId).eq(ItemKillSuccess::getUserId, userId);
                ItemKillSuccess itemKillSuccess = itemKillSuccessService.getOne(queryWrapper);
                if (itemKillSuccess != null) {
                    log.info("user"+userId+value+"you had been bought the item");
                    throw new Exception("you had been bought the item");
                }else {
                    log.info("user"+userId+" "+value+"no had dingdan");
                }

                //判断是否可以被秒杀
                ItemDTO itemDTO = itemManager.selectById(killId);
                if (itemDTO==null||itemDTO.getCanKill()!=1){
                    log.info("the item cannot be gotten");
                    throw new Exception("the item cannot be gotten");
                }
                //库存减1
                boolean flag = itemManager.updateById(killId);
                if (!flag){
                    log.info("kill failure");
                    return false;
                }
                commonRecordKillSuccessInfo(itemDTO,userId);

                log.info("kill success,userId{}",userId);
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (value==(int)redisTemplate.opsForValue().get(key)){
                    log.info("the current lock is deleted");
                    redisTemplate.delete(key);
                }
            }
        }else {
            throw new Exception("the currentUser is killing");
        }
        return false;
    }


    @Autowired
    private RedissonClient redissonClient;
    /**
     * Redisson的分布式锁优化
     * @param killId
     * @param userId
     * @return
             * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {

        // 生成redisson锁
        final String lockKey = new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean cacheRes = lock.tryLock(30, 10, TimeUnit.SECONDS);
            if (!cacheRes) {
                throw new Exception("redissonLock is invalid");
            }
            LambdaQueryWrapper<ItemKillSuccess> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ItemKillSuccess::getKillId, killId).eq(ItemKillSuccess::getUserId, userId);
            ItemKillSuccess itemKillSuccess = itemKillSuccessService.getOne(queryWrapper);
            if (itemKillSuccess != null) {
                log.info("you had been bought the item");
                throw new Exception("you had been bought the item");
            }
            //判断是否可以被秒杀
            ItemDTO itemDTO = itemManager.selectById(killId);
            if (itemDTO==null||itemDTO.getCanKill()!=1){
                log.info("the item cannot be gotten");
                throw new Exception("the item cannot be gotten");
            }
            //库存减1
            boolean flag = itemManager.updateById(killId);
            if (!flag){
                log.info("kill failure");
                return false;
            }
            commonRecordKillSuccessInfo(itemDTO,userId);
            log.info("kill success......................");
            return true;
        }finally {
            lock.unlock();
        }

    }

    @Autowired
    private CuratorFramework curatorFramework;
    /**
     * ZooKeeper的分布式锁优化
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {

        // 生成zookeeper锁
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/kill/zkLock/" + killId + userId + "-lock");

        try {
            int a = 4/0;
            log.info("no error");
            boolean cacheRes = mutex.acquire(10L,TimeUnit.SECONDS);
            if (!cacheRes) {
                throw new Exception("zookeeperLock is invalid");
            }
            LambdaQueryWrapper<ItemKillSuccess> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ItemKillSuccess::getKillId, killId).eq(ItemKillSuccess::getUserId, userId);
            ItemKillSuccess itemKillSuccess = itemKillSuccessService.getOne(queryWrapper);
            if (itemKillSuccess != null) {
                log.info("you had been bought the item");
                throw new Exception("you had been bought the item");
            }
            //判断是否可以被秒杀
            ItemDTO itemDTO = itemManager.selectById(killId);
            if (itemDTO==null||itemDTO.getCanKill()!=1){
                log.info("the item cannot be gotten");
                throw new Exception("the item cannot be gotten");
            }
            //库存减1
            boolean flag = itemManager.updateById(killId);
            if (!flag){
                log.info("kill failure");
                return false;
            }
            commonRecordKillSuccessInfo(itemDTO,userId);
            log.info("kill success");
            return true;
        }catch (Exception e){
            log.info(e.getMessage()+"................................");
            throw new Exception("zookeeperLock is invalid");
        } finally {
            // if (mutex!=null){
            //     mutex.release();
            // }
        }
    }
}
