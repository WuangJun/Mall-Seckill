# 商城秒杀系统

#### 介绍
高并发条件下完成在线商城秒杀业务

#### 基本功能
1.  秒杀活动列表的展示
2.  秒杀活动详情的获取
3.  用户登录认证服务
4.  高并发秒杀、抢购业务
5.  秒杀成功订单的生成
6.  邮件通知服务

#### 技术栈

1.  SpringBoot
2.  SpringMVC
3.  Mybatis
4.  Redis
5.  Zookeeper
6.  RabbitMQ
7.  MySQL

#### 技术要点

1.  用SpringBoot作为整个系统的框架，简化开发
2.  Redis作为缓存中间件，用于实现数据的缓存与分布式锁的实现
3.  Zookeeper作为注册中心
4.  RabbotMQ作为消息中间件，用于业务模块异步通信与接口限流

#### 参与贡献

1.  项目框架的搭建
2.  后端接口代码的开发
3.  核心业务的设计
