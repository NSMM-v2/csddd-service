package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import lombok.Builder;
import lombok.Getter;

/**
 * CSDDD 자가진단 위반 항목 응답 DTO
 * 
 * 자가진단 결과에서 위반이 발생한 항목들만을 필터링하여 제공하는 DTO입니다.
 * 위험 관리 및 개선이 필요한 항목들을 집중적으로 분석할 때 사용됩니다.
 * 
 * 위반 분류 기준:
 * - NO 응답: 완전 미준수
 * - PARTIAL 응답: 부분 준수 (개선 필요)
 * - 중대위반 항목: 특별 관리 대상
 * 
 * 사용 용도:
 * - 위반 항목 전용 조회 API
 * - 개선 계획 수립을 위한 데이터 제공
 * - 위험 관리 대시보드
 * - 우선순위 개선 과제 식별
 * 
 * @author ESG Project Team
 * @version 2.0
 * @since 2024
 * @lastModified 2024-12-20
 */
@Getter
@Builder
public class ViolationDto {

    // ============================================================================
    // 질문 정보 (Question Information)
    // ============================================================================

    /**
     * 질문 식별자
     * 예시: "Q1_1", "Q2_3", "Q5_2" 등
     * 위반이 발생한 문항을 특정하는 고유 키
     */
    private String questionId;

    /**
     * 질문 카테고리
     * 예시: "인권및노동", "산업안전보건", "환경경영", "공급망및조달", "윤리경영및정보보호"
     * 위반 항목의 영역별 분류에 사용
     */
    private String category;

    // ============================================================================
    // 위반 정보 (Violation Information)
    // ============================================================================

    /**
     * 응답 선택지
     * - NO: 완전 미준수 (가장 심각한 위반)
     * - PARTIAL: 부분 준수 (개선이 필요한 상태)
     * - YES: 완전 준수 (위반 아님, 필터링에서 제외됨)
     */
    private AnswerChoice answer;

    /**
     * 중대위반 여부
     * true인 경우 특별 관리가 필요한 중요 위반 항목
     * 중대위반 항목은 우선적으로 개선되어야 함
     */
    private Boolean criticalViolation;

    /**
     * 위반 항목에 대한 비고사항
     * 사용자가 작성한 현재 상황 설명이나 개선 계획
     * 부분 준수인 경우 진행 중인 개선 활동 내용 포함
     */
    private String remarks;
}