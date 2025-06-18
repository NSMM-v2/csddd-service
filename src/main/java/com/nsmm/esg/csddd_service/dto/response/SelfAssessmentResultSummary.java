package com.nsmm.esg.csddd_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 *  자가진단 결과 요약 정보 DTO
 * - 리스트 조회용 (ex. 관리자 진단 결과 목록 테이블)
 * - 상세 내용 없이 핵심 정보만 포함
 */
@Getter
@Builder
public class SelfAssessmentResultSummary {

    //  자가진단 결과 ID
    private Long resultId;

    //  회원 ID
    private Long memberId;

    //  점수 (정규화 점수: 0~100 범위 정수)
    private Integer score;

    //  평가 등급 (A, B, C, D 등)
    private String grade;

    //  진단 상태 (NOT_STARTED, IN_PROGRESS, COMPLETED)
    private String status;

    //  중대 위반 문항 수
    private Integer criticalViolationCount;

    //  완료율 (%): actualScore / totalPossibleScore * 100
    private Double completionRate;

    //  진단 완료 시각 (COMPLETED 상태일 경우)
    private LocalDateTime completedAt;
}