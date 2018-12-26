package com.unsc.shard.shadow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(value = {"classpath:dubbo/dubbo.xml"})
public class Shadow {

    public static void main(String[] args) {
        SpringApplication.run(Shadow.class, args);
    }

}

