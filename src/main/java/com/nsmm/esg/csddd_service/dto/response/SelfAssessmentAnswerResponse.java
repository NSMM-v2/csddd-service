// SelfAssessmentAnswerResponse.java 삭제
// SelfAssessmentAnswerDto.java 개선 버전 사용

package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * CSDDD 자가진단 답변 응답 DTO
 * 
 * 자가진단 결과 중 단일 문항의 응답 정보를 담는 DTO
 * 클라이언트에게 문항별 상세 답변 정보를 전달할 때 사용
 * 
 * @author ESG Project Team
 * @version 3.0
 */
@Schema(description = "자가진단 개별 답변 응답")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentAnswerResponse {

    @Schema(description = "답변 ID", example = "1")
    private Long id;

    @Schema(description = "문항 식별자", example = "Q1_1")
    private String questionId;

    @Schema(description = "문항 카테고리", example = "인권및노동")
    private String category;

    @Schema(description = "사용자 응답", example = "true")
    private Boolean answer;

    @Schema(description = "문항 가중치", example = "2.5")
    private Double weight;

    @Schema(description = "획득 점수", example = "2.5")
    private Double earnedScore;

    @Schema(description = "중대위반 항목 여부", example = "false")
    private Boolean criticalViolation;

    @Schema(description = "중대위반 발생 여부", example = "false")
    private Boolean hasCriticalViolation;

    @Schema(description = "중대위반 시 적용 등급", example = "D")
    private String criticalGrade;

    @Schema(description = "답변 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "답변 수정 일시")
    private LocalDateTime updatedAt;

    /**
     * Entity에서 응답 DTO로 변환
     */
    public static SelfAssessmentAnswerResponse from(SelfAssessmentAnswer answer) {
        return SelfAssessmentAnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .category(answer.getCategory())
                .answer(answer.isAnswer())
                .weight(answer.getWeight())
                .earnedScore(answer.calculateScore())
                .criticalViolation(answer.getCriticalViolation())
                .hasCriticalViolation(answer.hasCriticalViolation())
                .criticalGrade(answer.getCriticalGrade() != null ? answer.getCriticalGrade().name() : null)
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }

}