package ru.ollyeys.todoapp.utils;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = JDBCUtils.getConnection();
        if (connection != null) {
            System.out.println("Success!");
        } else {
            System.out.println("Not success");
        }

    }
}
