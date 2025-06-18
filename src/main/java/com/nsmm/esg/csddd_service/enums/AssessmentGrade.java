package com.nsmm.esg.csddd_service.enums;

/**
 *  AssessmentGrade
 * - 자가진단 결과에 따라 매겨지는 평가 등급(Enum)
 * - 점수 기준 또는 위반사항 수에 따라 A ~ D로 등급 분류
 *
 * A: 매우 우수
 * B: 양호
 * C: 보통
 * D: 미흡 / 위험 수준
 */
public enum AssessmentGrade {
    A,  // 최고 등급 (위험 요인 없음 또는 극히 적음)
    B,  // 양호 (위험 요인 낮음)
    C,  // 보통 (개선 필요)
    D;  // 미흡 (위험도 높음 또는 중대 위반 존재)
}