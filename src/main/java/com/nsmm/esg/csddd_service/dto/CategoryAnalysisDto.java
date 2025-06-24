package com.nsmm.esg.csddd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysisDto {
    private String category;      // 영역명
    private int score;            // 정규화 점수 (0~100)
    private String status;        // "우수" | "보통" | "개선 필요"
    private String color;         // UI 색상용 (green/yellow/red 등)
}