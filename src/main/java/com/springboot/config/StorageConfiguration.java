package com.springboot.config;

import com.springboot.question.service.FileSystemStorageService;
import com.springboot.question.service.StorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {
//    private static final String REGION = "ap-northeast-2";
    @Bean
    public StorageService fileSystemStorageService(){
        return new FileSystemStorageService();
    }
}
