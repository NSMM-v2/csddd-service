package com.nsmm.esg.csddd_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 📦 자가진단 요약 응답 DTO
 * - 상세 문항 없이, 진단 결과 요약만 제공
 */
@Getter
@Builder
public class SelfAssessmentResponse {

    // 📌 자가진단 결과 ID
    private Long id;

    // 👤 응답자(회원) ID
    private Long memberId;

    // 🧮 점수 (정규화된 점수: 0~100 범위 내 정수)
    private Integer score;

    // 🧮 실제 점수 (가중치가 반영된 소수점 점수)
    private Double actualScore;

    // 🧮 전체 가능한 점수 (모든 문항의 가중치 합산)
    private Double totalPossibleScore;

    // 🏅 평가 등급 (예: A, B, C, D)
    private String grade;

    // ⏳ 평가 상태 (예: NOT_STARTED, IN_PROGRESS, COMPLETED)
    private String status;

    // 🚨 중대 위반 문항 수
    private Integer criticalViolationCount;

    // 📊 완료율 (퍼센트 %): actualScore / totalPossibleScore * 100
    private Double completionRate;

    // 📝 진단 요약 설명
    private String summary;

    // 🛠️ 개선 권고사항
    private String recommendations;

    // 🕓 진단 생성 시각
    private LocalDateTime createdAt;

    // 🕓 마지막 수정 시각
    private LocalDateTime updatedAt;

    // 🕓 진단 완료 시각 (COMPLETED일 때만 존재)
    private LocalDateTime completedAt;
}