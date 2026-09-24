package com.studyroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudyRoomApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRoomApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Study Room Server đang chạy!");
        System.out.println("  Truy cập: http://localhost:8080");
        System.out.println("===========================================");
    }
}

