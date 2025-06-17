package com.nsmm.esg.csddd_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📦 자가진단 전체 결과 응답 DTO
 * - 요약 정보 + 문항별 응답 리스트를 모두 포함
 */
@Getter
@Builder
public class SelfAssessmentFullResponse {

    // 📌 자가진단 결과 ID (DB PK)
    private Long id;

    // 👤 응답한 회원 ID
    private Long memberId;

    // 🧮 점수 (환산 점수: 정수형)
    private Integer score;

    // 🧮 실제 점수 (가중치 계산된 소수점 점수)
    private Double actualScore;

    // 🧮 전체 가능한 최대 점수
    private Double totalPossibleScore;

    // 🏅 평가 등급 (A, B, C, D 등 문자열 형태)
    private String grade;

    // ⏳ 상태 (예: COMPLETED, IN_PROGRESS 등)
    private String status;

    // 🚨 중대 위반 문항 개수
    private Integer criticalViolationCount;

    // 📊 완료율 (%) = actualScore / totalPossibleScore * 100
    private Double completionRate;

    // 📝 전체 진단 요약 내용
    private String summary;

    // 🛠️ 개선 권고사항
    private String recommendations;

    // 🕓 생성일
    private LocalDateTime createdAt;

    // 🕓 수정일
    private LocalDateTime updatedAt;

    // 🕓 진단 완료일
    private LocalDateTime completedAt;

    // 📋 문항별 상세 응답 리스트
    private List<SelfAssessmentAnswerDto> answers;
}