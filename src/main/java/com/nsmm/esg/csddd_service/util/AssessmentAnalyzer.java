package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.dto.ActionPlanDto;
import com.nsmm.esg.csddd_service.dto.CategoryAnalysisDto;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AssessmentAnalyzer {

    /**
     * 카테고리별 점수 및 상태 분석
     */
    public List<CategoryAnalysisDto> analyzeByCategory(SelfAssessmentResult result) {
        Map<String, List<SelfAssessmentAnswer>> grouped = result.getAnswers().stream()
                .filter(a -> a.getCategory() != null && a.getWeight() != null)
                .collect(Collectors.groupingBy(SelfAssessmentAnswer::getCategory));

        List<CategoryAnalysisDto> analysisList = new ArrayList<>();

        for (Map.Entry<String, List<SelfAssessmentAnswer>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            List<SelfAssessmentAnswer> answers = entry.getValue();

            double total = answers.stream().mapToDouble(SelfAssessmentAnswer::getWeight).sum();
            double actual = answers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.isYes()))
                    .mapToDouble(SelfAssessmentAnswer::getWeight)
                    .sum();

            int score = total == 0 ? 0 : (int) Math.round((actual / total) * 100);
            String status = getStatus(score);
            String color = getColor(score);

            analysisList.add(CategoryAnalysisDto.builder()
                    .category(category)
                    .score(score)
                    .status(status)
                    .color(color)
                    .build());
        }

        return analysisList;
    }

    /**
     * 주요 강점 영역 추출 (80점 이상)
     */
    public List<String> extractStrengths(List<CategoryAnalysisDto> categories) {
        return categories.stream()
                .filter(c -> c.getScore() >= 80)
                .sorted(Comparator.comparingInt(CategoryAnalysisDto::getScore).reversed())
                .map(CategoryAnalysisDto::getCategory)
                .limit(3)
                .toList();
    }

    /**
     * 개선 계획 도출
     * - 중대 위반 항목 → "긴급"
     * - 70점 미만 카테고리 → "높음"
     */
    public List<ActionPlanDto> buildActionPlan(SelfAssessmentResult result, List<CategoryAnalysisDto> categories) {
        List<ActionPlanDto> plans = new ArrayList<>();

        // 중대 위반 항목 기반
        result.getAnswers().stream()
                .filter(a -> Boolean.TRUE.equals(a.getCriticalViolation()))
                .forEach(a -> plans.add(ActionPlanDto.builder()
                        .issue("중대 위반 항목 (" + a.getCategory() + ", ID: " + a.getQuestionId() + ")")
                        .priority("긴급")
                        .recommendation("즉각적인 시정조치가 필요합니다.")
                        .build()));

        // 70점 미만 카테고리 기반
        categories.stream()
                .filter(c -> c.getScore() < 70)
                .forEach(c -> plans.add(ActionPlanDto.builder()
                        .issue("저점 영역: " + c.getCategory())
                        .priority("높음")
                        .recommendation("관련 정책 및 문서를 정비하세요.")
                        .build()));

        return plans;
    }

    /**
     * 점수 → 등급 텍스트
     */
    private String getStatus(int score) {
        if (score >= 90) return "우수";
        if (score < 70) return "개선 필요";
        return "보통";
    }

    /**
     * 점수 → 색상 텍스트
     */
    private String getColor(int score) {
        if (score >= 90) return "green";
        if (score < 70) return "red";
        return "yellow";
    }
}