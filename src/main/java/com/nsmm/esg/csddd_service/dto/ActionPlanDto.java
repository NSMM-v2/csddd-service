package com.nsmm.esg.csddd_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanDto {
    private String issue;          // 문제 요약 (e.g. "중대 위반: 노동권 침해")
    private String priority;       // "긴급" | "높음" | "보통"
    private String recommendation; // 권장 조치 내용
}