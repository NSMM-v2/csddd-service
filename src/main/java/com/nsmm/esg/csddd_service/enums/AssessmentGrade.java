package com.nsmm.esg.csddd_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  AssessmentGrade
 * - 자가진단 결과에 따라 매겨지는 평가 등급(Enum)
 * - 점수 기준 또는 위반사항 수에 따라 A ~ D 또는 B/C 로 등급 분류
 *
 * A: 매우 우수
 * B: 양호
 * C: 보통
 * D: 미흡 / 위험 수준
 * B/C: 경계 등급
 */
public enum AssessmentGrade {
    D,    // 0 - 가장 나쁨 (min에서 선택됨)
    C,    // 1
    B_C,  // 2
    B,    // 3
    A;    // 4 - 가장 좋음

    @JsonValue
    public String toValue() {
        return this == B_C ? "B/C" : name();
    }

    @JsonCreator
    public static AssessmentGrade fromValue(String value) {
        if ("B/C".equalsIgnoreCase(value)) {
            return B_C;
        }
        return AssessmentGrade.valueOf(value);
    }
}