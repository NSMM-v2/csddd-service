package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.dto.ActionPlanDto;
import com.nsmm.esg.csddd_service.dto.CategoryAnalysisDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CSDDD 자가진단 전체 결과 응답 DTO
 * 
 * 자가진단의 요약 정보와 문항별 상세 응답을 모두 포함하는 완전한 결과 DTO입니다.
 * 클라이언트에서 진단 결과의 전체적인 분석과 상세 내역을 모두 확인할 때 사용됩니다.
 * 
 * 포함 정보:
 * - 기본 평가 결과 (점수, 상태 등)
 * - 문항별 상세 답변 목록
 * - 카테고리별 분석 결과
 * - 주요 강점 영역
 * - 구체적인 개선 계획
 * 
 * 사용 용도:
 * - 자가진단 상세 결과 페이지
 * - PDF 보고서 생성
 * - 종합적인 평가 분석
 * - 개선 계획 수립 지원
 * 
 * @author ESG Project Team
 * @version 2.0
 * @since 2024
 * @lastModified 2024-12-20
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentFullResponse {

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
    private Integer score;

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
     * - IN_PROGRESS: 진행중 (향후 확장용)
     * - CANCELLED: 취소됨 (향후 확장용)
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

    // ============================================================================
    // 상세 분석 정보 (Detailed Analysis Information)
    // ============================================================================

    /**
     * 문항별 상세 응답 목록
     * 각 질문에 대한 개별 답변 정보를 포함
     * 상세 분석 및 위반 항목 확인에 사용
     */
    private List<SelfAssessmentAnswerDto> answers;

    /**
     * 카테고리별 점수 분석
     * CSDDD 5개 주요 영역별 점수 및 현황 분석
     * 영역별 강점과 약점 파악에 활용
     */
    private List<CategoryAnalysisDto> categoryAnalysis;

    /**
     * 주요 강점 영역
     * 평가 결과에서 우수한 성과를 보인 영역들의 목록
     * 긍정적인 측면 강조 및 모범 사례 활용
     */
    private List<String> strengths;

    /**
     * 구체적인 개선 계획 목록
     * 진단 결과를 바탕으로 한 실행 가능한 개선 방안들
     * 우선순위와 구체적인 실행 방법 포함
     */
    private List<ActionPlanDto> actionPlan;
}