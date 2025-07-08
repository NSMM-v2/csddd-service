package com.nsmm.esg.csddd_service.dto.request;

import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSDDD 자가진단 개별 답변 요청 DTO
 * 
 * 각 문항에 대한 개별 응답 데이터
 * 문항 식별자, 답변, 가중치, 중대위반 정보 등을 포함
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Schema(description = "자가진단 개별 답변 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SelfAssessmentAnswerRequest {

    /**
     * 문항 식별자
     * 각 질문을 고유하게 식별하는 코드
     */
    @Schema(description = "문항 식별자", example = "Q1_1")
    @NotBlank(message = "문항 식별자는 필수입니다.")
    private String questionId;

    /**
     * 문항 카테고리
     * CSDDD 5개 주요 평가 영역
     */
    @Schema(description = "문항 카테고리", example = "인권및노동")
    @NotBlank(message = "문항 카테고리는 필수입니다.")
    private String category;

    /**
     * 사용자 응답
     * yes: 준수, no: 미준수
     */
    @Schema(description = "사용자 응답", example = "yes", allowableValues = { "yes", "no" })
    @NotBlank(message = "응답은 필수입니다.")
    private String answer;

    /**
     * 문항 가중치
     * 점수 계산 시 사용되는 가중치 값
     */
    @Schema(description = "문항 가중치", example = "2.5")
    @NotNull(message = "가중치는 필수입니다.")
    @Positive(message = "가중치는 양수여야 합니다.")
    private Double weight;

    /**
     * 중대위반 항목 여부
     * 해당 문항이 중대위반 대상인지 표시
     */
    @Schema(description = "중대위반 항목 여부", example = "false")
    private Boolean critical;

    /**
     * 중대위반 시 적용 등급
     * 중대위반 발생 시 강등될 등급 정보
     */
    @Schema(description = "중대위반 시 적용 등급", example = "D")
    private AssessmentGrade criticalGrade;
}