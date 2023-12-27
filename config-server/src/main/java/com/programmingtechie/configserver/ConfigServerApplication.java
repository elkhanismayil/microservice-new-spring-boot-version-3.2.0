package com.programmingtechie.configserver;

//import com.programmingtechie.configserver.config.VaultConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
@RequiredArgsConstructor
@Slf4j
//@EnableConfigurationProperties(VaultConfiguration.class)
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

    @Value("${spring-boot-value.property}")
    private String demo;

    @Bean
    CommandLineRunner runner() {
        return args -> log.info("====> Demo: {}", demo);
    }

}
