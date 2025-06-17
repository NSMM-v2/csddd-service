package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import lombok.Builder;
import lombok.Getter;

/**
 * 🚨 자가진단 위반 항목 DTO
 * - 위험 응답만 따로 필터링해서 전달할 때 사용
 * - 사용처 예: /result/violations API 응답
 */
@Getter
@Builder
public class ViolationDto {

    // 📌 문항 ID (예: "2.3", "3.1" 등)
    private String questionId;

    // ✅ 응답 선택 (YES, NO, PARTIAL)
    private AnswerChoice answer;

    // ⚠️ 중대 위반 여부 (true: 중대 위반)
    private Boolean criticalViolation;

    // 🏷️ 카테고리 (예: 인권, 환경, 공급망 등)
    private String category;

    // 💬 비고 또는 코멘트 (선택값)
    private String remarks;
}