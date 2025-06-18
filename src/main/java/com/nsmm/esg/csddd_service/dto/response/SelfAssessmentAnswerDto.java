package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 *  자가진단 결과 중, 단일 문항의 응답 정보를 담는 DTO
 * - 클라이언트에게 개별 답변 정보를 전달할 때 사용됨
 */
@Getter
@Builder
public class SelfAssessmentAnswerDto {

    //  답변 고유 ID (DB에서 자동 생성된 ID)
    private Long id;

    //  질문 식별자 (예: "1.1", "2.3")
    private String questionId;

    //  응답 선택지 (YES, NO, PARTIAL) — enum으로 관리됨
    private AnswerChoice answer;

    //  문항의 가중치 (점수 계산에 사용됨)
    private Double weight;

    //  중대 위반 여부
    private Boolean criticalViolation;

    //  문항 카테고리 (예: "인권", "환경", "윤리")
    private String category;

    //  응답 시 남긴 비고 또는 코멘트
    private String remarks;

    //  생성 시각 (DB에서 자동 설정)
    private LocalDateTime createdAt;

    //  마지막 수정 시각 (DB에서 자동 설정)
    private LocalDateTime updatedAt;
}