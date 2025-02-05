package com.example.project.utils;

import com.example.project.entities.Book;

import java.security.SecureRandom;

public class CreateService {
    public static String randomID(int length) {

        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        String characters = UPPER + LOWER + DIGITS;
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String getIDBooksFromBooks(Book[] books) {
        if (books == null || books.length == 0) {
            return ""; // Trả về chuỗi rỗng nếu mảng books là null hoặc không có phần tử
        }

        StringBuilder result = new StringBuilder(); // Sử dụng StringBuilder để hiệu quả khi thực hiện các phép toán chuỗi

        // Duyệt qua mảng books
        for (int i = 0; i < books.length; i++) {
            result.append(books[i].id); // Thêm id của mỗi Book vào chuỗi result
            if (i < books.length - 1) {
                result.append(","); // Thêm dấu phẩy giữa các id, trừ id cuối cùng
            }
        }

        return result.toString();
    }

}
