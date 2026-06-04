package com.example.service;



public class EmailService {
    // Simulated email – prints to console (can be replaced with real SMTP)
    public static void sendNotification(String to, String subject, String body) {
        System.out.println("\n========== EMAIL NOTIFICATION (SIMULATED) ==========");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("====================================================\n");
    }
}