package com.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregationResultDTO {
    private int totalProcessed;
    private Map<String, Integer> bySourceType;
    private String message;
}