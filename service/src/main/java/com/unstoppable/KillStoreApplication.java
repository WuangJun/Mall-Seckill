package com.unstoppable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class KillStoreApplication {

    public static void main(String[] args) {
        log.info("--------------------service项目启动成功-------------------------");
        SpringApplication.run(KillStoreApplication.class, args);
    }

}
