package com.example.homie.Models;

public enum TransactionType {
    EXPENSE,
    INCOME;
    public static TransactionType fromString(String text) {
        if (text != null) {
            for (TransactionType type : TransactionType.values()) {
                if (text.equalsIgnoreCase(type.name())) {
                    return type;
                }
            }
        }
        return null;
    }


}
