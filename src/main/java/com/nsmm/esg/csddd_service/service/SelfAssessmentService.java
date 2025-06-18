package com.nsmm.esg.csddd_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentRequest;
import com.nsmm.esg.csddd_service.dto.response.*;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentAnswerRepository;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentResultRepository;
import com.nsmm.esg.csddd_service.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelfAssessmentService {

    private final SelfAssessmentAnswerRepository answerRepository;
    private final SelfAssessmentResultRepository resultRepository;
    private final GradeCalculator gradeCalculator;
    private final ObjectMapper objectMapper;

    /**
     * 자가진단 제출 처리
     * - 요청으로부터 답변 리스트를 받고,
     * - 결과(점수, 등급 등)를 계산한 후,
     * - DB에 저장한다.
     */
    @Transactional
    public void submitAssessment(
            Long userId,
            String userType,
            Long headquartersId,
            List<SelfAssessmentRequest> requestList
    ) {
        // 1. SelfAssessmentResult 엔티티 생성 (userType에 따라 memberId 또는 partnerId 저장 전략 가능)
        SelfAssessmentResult result = SelfAssessmentResult.builder()
                .memberId(userId)  // 현재 구조에서는 userId만 저장. 확장 필요 시 userType 고려
                .build();

        // 2. 요청(requestList)을 SelfAssessmentAnswer 엔티티로 변환
        List<SelfAssessmentAnswer> answers = requestList.stream().map(req -> {
            return SelfAssessmentAnswer.builder()
                    .questionId(req.getQuestionId())
                    .answer(AnswerChoice.fromString(req.getAnswer()))
                    .weight(req.getWeight())
                    .criticalViolation(Boolean.TRUE.equals(req.getCritical()))
                    .category(req.getCategory())
                    .remarks(req.getRemarks())
                    .result(result)
                    .build();
        }).collect(Collectors.toList());

        // 3. 결과에 답변들 추가
        answers.forEach(result::addAnswer);

        // 4. 점수 계산
        gradeCalculator.evaluate(result);

        // 5. JSON 직렬화
        try {
            result.setAnswersJson(objectMapper.writeValueAsString(requestList));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }

        // 6. 저장
        resultRepository.save(result);
    }

    /**
     * 자가진단 요약 결과 조회
     * - 최신 결과 1건을 가져와 DTO로 반환
     */
    @Transactional(readOnly = true)
    public SelfAssessmentResponse getResult(Long memberId) {
        SelfAssessmentResult result = resultRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));
        return result.toResponse();
    }

    /**
     * 자가진단 전체 결과 + 문항별 상세 답변 조회
     */
    @Transactional(readOnly = true)
    public SelfAssessmentFullResponse getFullResult(Long memberId) {
        SelfAssessmentResult result = resultRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));

        // 모든 답변을 DTO로 변환
        List<SelfAssessmentAnswerDto> answers = result.getAnswers().stream()
                .map(this::toAnswerDto)
                .collect(Collectors.toList());

        // 결과 + 답변 DTO 구성
        return SelfAssessmentFullResponse.builder()
                .id(result.getId())
                .memberId(result.getMemberId())
                .score(result.getScore())
                .actualScore(result.getActualScore())
                .totalPossibleScore(result.getTotalPossibleScore())
                .grade(result.getGrade().name())
                .status(result.getStatus().name())
                .criticalViolationCount(result.getCriticalViolationCount())
                .completionRate(result.getCompletionRate())
                .summary(result.getSummary())
                .recommendations(result.getRecommendations())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .completedAt(result.getCompletedAt())
                .answers(answers)
                .build();
    }

    /**
     * 위반 항목만 필터링하여 반환
     * - NO, PARTIAL, 중대위반 항목만 추출
     */
    @Transactional(readOnly = true)
    public List<ViolationDto> getViolations(Long memberId) {
        SelfAssessmentResult result = resultRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));

        return result.getAnswers().stream()
                .filter(a -> a.isNo() || a.isPartial() || a.getCriticalViolation())
                .map(this::toViolationDto)
                .collect(Collectors.toList());
    }

    // === 내부 헬퍼 메서드 ===

    /**
     * Answer 엔티티 → Answer DTO 변환
     */
    private SelfAssessmentAnswerDto toAnswerDto(SelfAssessmentAnswer a) {
        return SelfAssessmentAnswerDto.builder()
                .id(a.getId())
                .questionId(a.getQuestionId())
                .answer(a.getAnswer())
                .weight(a.getWeight())
                .criticalViolation(a.getCriticalViolation())
                .category(a.getCategory())
                .remarks(a.getRemarks())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    /**
     * Answer 엔티티 → Violation DTO 변환
     */
    private ViolationDto toViolationDto(SelfAssessmentAnswer a) {
        return ViolationDto.builder()
                .questionId(a.getQuestionId())
                .answer(a.getAnswer())
                .criticalViolation(a.getCriticalViolation())
                .category(a.getCategory())
                .remarks(a.getRemarks())
                .build();
    }
}