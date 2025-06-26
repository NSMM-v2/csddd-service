package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import lombok.Builder;
import lombok.Getter;

/**
 * 자가진단 위반 항목 DTO
 * - 위험 응답만 따로 필터링해서 전달할 때 사용
 * - 사용처 예: /result/violations API 응답
 */
@Getter
@Builder
public class ViolationDto {

    // 문항 ID (예: "2.3", "3.1" 등)
    private String questionId;

    // 문항 내용 (프론트에서 표시용)
    private String questionText;

    // 응답 선택 (YES, NO, PARTIAL)
    private AnswerChoice answer;

    // 중대 위반 여부 (true: 중대 위반)
    private Boolean criticalViolation;

    // 위반 시 자동 강등 등급
    private AssessmentGrade violationGrade;

    // 위반 사유 (비고)
    private String violationReason;

    // 카테고리 (예: 인권, 환경, 공급망 등)
    private String category;

    // 벌금이나 제재 정보
    private String penaltyInfo;

    // 관련 법적 조항
    private String legalBasis;
}