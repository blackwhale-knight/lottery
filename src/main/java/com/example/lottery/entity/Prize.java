package com.example.lottery.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prize {
    private String id;
    private String name;
    private double probability;
    private int stock;
}
