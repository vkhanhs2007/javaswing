package com.cafe.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DiscountUtils {
    
    public static double getDiscountedPrice(double price, double discountPercent, 
                                           String timeStart, String timeEnd) {
        if (timeStart == null || timeEnd == null) {
            return price;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        try {
            LocalTime start = LocalTime.parse(timeStart);
            LocalTime end = LocalTime.parse(timeEnd);
            
            if (currentTime.isAfter(start) && currentTime.isBefore(end)) {
                return price * (1 - discountPercent / 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return price;
    }
    
    public static double calculatePoints(double amount) {
        return (int)(amount / 1000);
    }
}
