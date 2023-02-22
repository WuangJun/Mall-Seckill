package com.unstoppable.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author:WJ
 * Date:2023/2/14 18:08
 * Description:<>
 */
@Configuration
public class ZooKeeperConfig {

    @Bean
    public CuratorFramework curatorFramework(){
        CuratorFramework curatorFramework= CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .namespace("kill")
                //重试策略
                .retryPolicy(new RetryNTimes(5,1000))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
