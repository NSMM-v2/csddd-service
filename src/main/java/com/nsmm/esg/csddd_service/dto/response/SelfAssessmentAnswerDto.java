package com.nsmm.esg.csddd_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
/**
 * CSDDD 자가진단 개별 답변 응답 DTO
 *
 * 자가진단 결과 중 단일 문항의 응답 정보를 담는 DTO입니다.
 * 클라이언트에게 문항별 상세 답변 정보를 전달할 때 사용됩니다.
 *
 * 사용 용도:
 * - 자가진단 상세 결과 조회
 * - 문항별 답변 현황 분석
 * - 위반 항목 필터링 및 표시
 * - PDF 보고서 생성 시 상세 데이터
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentAnswerDto {

    // ============================================================================
    // 기본 식별 정보 (Basic Identification)
    // ============================================================================

    /**
     * 답변 고유 식별자
     * 데이터베이스에서 자동 생성된 Primary Key
     */
    private Long id;

    // ============================================================================
    // 질문 정보 (Question Information)
    // ============================================================================

    /**
     * 질문 식별자
     * 예시: "Q1_1", "Q2_3", "Q5_2" 등
     * 각 질문을 고유하게 식별하는 코드
     */
    private String questionId;

    /**
     * 질문 카테고리
     * 예시: "인권및노동", "산업안전보건", "환경경영", "공급망및조달", "윤리경영및정보보호"
     * CSDDD 5개 주요 평가 영역별 분류
     */
    private String category;

    // ============================================================================
    // 응답 정보 (Answer Information)
    // ============================================================================

    /**
     * 응답 결과
     * true: "예" (요구사항 충족)
     * false: "아니오" (요구사항 미충족)
     */
    private String answer;

    /**
     * 응답에 대한 부가 설명 및 비고사항
     * 사용자가 답변과 함께 작성한 상세 설명이나 추가 정보
     */
    private String remarks;

    // ============================================================================
    // 평가 기준 정보 (Assessment Criteria)
    // ============================================================================

    /**
     * 질문 가중치
     * 각 질문의 중요도에 따른 가중치 값
     * 점수 계산 시 사용되며, 높은 가중치일수록 전체 점수에 미치는 영향이 큼
     */
    private Double weight;

    /**
     * 중대위반 여부
     * 해당 문항이 중대위반 항목인지 표시
     * true인 경우 NO 답변 시 전체 등급에 영향을 미침
     */
    private Boolean criticalViolation;

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    /**
     * 답변 생성 일시
     * 해당 문항에 대한 답변이 처음 입력된 시간
     */
    private LocalDateTime createdAt;

    /**
     * 답변 수정 일시
     * 해당 문항에 대한 답변이 마지막으로 수정된 시간
     */
    private LocalDateTime updatedAt;
    public static SelfAssessmentAnswerDto from(SelfAssessmentAnswer answer) {
        return SelfAssessmentAnswerDto.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .category(answer.getCategory())
                .answer(answer.isAnswer() ? "yes" : "no")
                .remarks(answer.getRemarks())
                .weight(answer.getWeight())
                .criticalViolation(answer.getCriticalViolation())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}