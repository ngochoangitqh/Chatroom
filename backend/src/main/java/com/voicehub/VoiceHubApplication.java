package com.voicehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VoiceHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoiceHubApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  VoiceHub Server đang chạy!");
        System.out.println("  Truy cập: http://localhost:8080");
        System.out.println("===========================================");
    }
}
