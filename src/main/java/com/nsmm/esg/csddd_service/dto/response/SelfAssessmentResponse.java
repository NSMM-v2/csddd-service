package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
/**
 * CSDDD 자가진단 요약 응답 DTO
 *
 * 클라이언트에게 진단 결과의 요약 정보만을 제공하는 DTO입니다.
 * 상세한 문항별 답변은 포함하지 않고, 전체적인 평가 결과만을 담습니다.
 *
 * 사용 용도:
 * - 대시보드의 진단 결과 요약 표시
 * - 진단 결과 목록 조회
 * - 간단한 평가 현황 확인
 *
 * 등급 계산:
 * - 백엔드에서 점수 및 중대위반을 기반으로 최종 등급(A/B/C/D) 결정
 * - 프론트는 해당 값만 표시
 *
 * @author ESG Project Team
 * @version 2.1
 */
@Getter
@Builder
public class SelfAssessmentResponse {

    // ============================================================================
    // 기본 식별 정보 (Basic Identification)
    // ============================================================================

    /**
     * 자가진단 결과 고유 식별자
     * 데이터베이스의 Primary Key
     */
    private Long id;

    // ============================================================================
    // 사용자 식별 정보 (User Identification) - Scope3 방식
    // ============================================================================

    /**
     * 소속 본사 ID
     * - 본사인 경우: 자신의 본사 ID
     * - 협력사인 경우: 소속된 본사의 ID
     */
    private Long headquartersId;

    /**
     * 협력사 ID
     * - 본사인 경우: null
     * - 협력사인 경우: 해당 협력사의 ID
     */
    private Long partnerId;

    /**
     * 조직 트리 경로
     * 권한 검증 및 계층 구조 관리를 위한 경로 정보
     * 형식: "HQ001" (본사) 또는 "HQ001/L1-001/L2-003" (협력사)
     */
    private String treePath;

    // ============================================================================
    // 평가 점수 정보 (Assessment Scores)
    // ============================================================================

    /**
     * 정규화된 점수 (0~100)
     * 프론트엔드에서 등급 계산의 기준이 되는 점수
     * 계산 방식: (실제 점수 / 총 가능 점수) * 100
     */
    private double score;

    /**
     * 실제 획득 점수
     * 가중치가 적용된 실제 점수 (소수점 포함)
     * 예시: 34.5점
     */
    private Double actualScore;

    /**
     * 총 가능 점수
     * 해당 진단에서 획득 가능한 최대 점수
     * 모든 문항의 가중치 합계
     */
    private Double totalPossibleScore;

    // ============================================================================
    // 평가 상태 및 결과 정보 (Assessment Status & Results)
    // ============================================================================

    /**
     * 진단 상태
     * - COMPLETED: 완료
     * - IN_PROGRESS: 진행중
     * - CANCELLED: 취소됨
     */
    private String status;

    /**
     * 중대위반 건수
     * 중대위반으로 분류된 항목의 개수
     * 프론트엔드에서 등급 자동 강등 판단에 사용
     */
    private Integer criticalViolationCount;

    /**
     * 완료율 (퍼센트)
     * (실제 점수 / 총 가능 점수) * 100
     * 진단 결과의 달성도를 백분율로 표시
     */
    private Double completionRate;

    /**
     * 최종 등급 (A, B, C, D)
     * 백엔드에서 점수 및 중대위반 여부를 기반으로 계산
     */
    private String finalGrade;

    /**
     * 진단 결과 요약
     * 전체적인 평가 결과에 대한 간략한 설명
     */
    private String summary;

    /**
     * 개선 권고사항
     * 진단 결과를 바탕으로 한 구체적인 개선 방안 제시
     */
    private String recommendations;

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    /**
     * 진단 생성 일시
     * 자가진단을 최초 시작한 시간
     */
    private LocalDateTime createdAt;

    /**
     * 마지막 수정 일시
     * 진단 결과가 마지막으로 업데이트된 시간
     */
    private LocalDateTime updatedAt;

    /**
     * 진단 완료 일시
     * 자가진단이 완료된 시간 (COMPLETED 상태일 때만 존재)
     */
    private LocalDateTime completedAt;

    private List<SelfAssessmentAnswerDto> answers;


    public static SelfAssessmentResponse from(SelfAssessmentResult result) {
        return SelfAssessmentResponse.builder()
                .id(result.getId())
                .headquartersId(result.getHeadquartersId())
                .partnerId(result.getPartnerId())
                .treePath(result.getTreePath())
                .score(result.getScore())
                .actualScore(result.getActualScore())
                .totalPossibleScore(result.getTotalPossibleScore())
                .criticalViolationCount(result.getCriticalViolationCount())
                .completionRate(result.getCompletionRate())
                .finalGrade(result.getFinalGrade() != null ? result.getFinalGrade().name() : null)
                .summary(result.getSummary())
                .recommendations(result.getRecommendations())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .completedAt(result.getCompletedAt())
                .answers(result.getAnswers().stream()
                        .map(SelfAssessmentAnswerDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}