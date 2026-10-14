package com.example.hydra.config;

import java.net.URI;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FilebaseConfig {
    @Bean
    public S3Client create(@Value("${filebase.access-key}") String accessKey,
                           @Value("${filebase.secret-key}") String secretKey) {

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey,secretKey);

        return S3Client.builder()
            .endpointOverride(URI.create("https://s3.filebase.io"))
            .region(Region.of("auto"))
            .credentialsProvider(StaticCredentialsProvider.create(creds))
            .build();
    }

    @Bean
    public S3Presigner presigner(@Value("${filebase.access-key}") String accessKey,
                                 @Value("${filebase.secret-key}") String secretKey) {
        
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey,secretKey);  
        
        return S3Presigner.builder()
            .endpointOverride(URI.create("https://s3.filebase.io"))
            .region(Region.of("auto"))
            .credentialsProvider(StaticCredentialsProvider.create(creds))
            .build();
    }

}