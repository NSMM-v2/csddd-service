package com.nsmm.esg.csddd_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentRequest;
import com.nsmm.esg.csddd_service.dto.response.*;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentAnswerRepository;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentResultRepository;
import com.nsmm.esg.csddd_service.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelfAssessmentService {

    private final SelfAssessmentAnswerRepository answerRepository;
    private final SelfAssessmentResultRepository resultRepository;
    private final GradeCalculator gradeCalculator;
    private final ObjectMapper objectMapper;

    /**
     * 자가진단 제출 처리
     */
    @Transactional
    public void submitAssessment(
            Long userId,
            String userType,
            Long headquartersId,
            List<SelfAssessmentRequest> requestList
    ) {
        // 기존 결과가 있으면 삭제
        deleteByMemberId(userId, userType);

        SelfAssessmentResult result = SelfAssessmentResult.builder()
                .memberId(userId)
                .userType(userType)
                .headquartersId(headquartersId)
                .score(0)
                .actualScore(0.0)
                .totalPossibleScore(0.0)
                .grade(AssessmentGrade.D)
                .answersJson("[]")
                .build();

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

        answers.forEach(result::addAnswer);
        gradeCalculator.evaluate(result);

        try {
            result.setAnswersJson(objectMapper.writeValueAsString(requestList));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }

        resultRepository.save(result);
        log.info("자가진단 결과 저장 완료 - userId: {}, userType: {}, headquartersId: {}", userId, userType, headquartersId);
    }

    /**
     * 자가진단 요약 결과 조회 (권한별 분기)
     */
    @Transactional(readOnly = true)
    public SelfAssessmentResponse getResult(Long memberId, String userType, Long requesterId, String requesterType) {
        // 권한 검증
        validateAccess(memberId, userType, requesterId, requesterType);

        SelfAssessmentResult result = findResultWithAccess(memberId, userType, requesterId, requesterType);
        return result.toResponse();
    }

    /**
     * 자가진단 전체 결과 + 문항별 상세 답변 조회 (권한별 분기)
     */
    @Transactional(readOnly = true)
    public SelfAssessmentFullResponse getFullResult(Long memberId, String userType, Long requesterId, String requesterType) {
        // 권한 검증
        validateAccess(memberId, userType, requesterId, requesterType);

        SelfAssessmentResult result = findResultWithAccess(memberId, userType, requesterId, requesterType);

        List<SelfAssessmentAnswerDto> answers = result.getAnswers().stream()
                .map(this::toAnswerDto)
                .collect(Collectors.toList());

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
     * 위반 항목만 필터링하여 반환 (권한별 분기)
     */
    @Transactional(readOnly = true)
    public List<ViolationDto> getViolations(Long memberId, String userType, Long requesterId, String requesterType) {
        // 권한 검증
        validateAccess(memberId, userType, requesterId, requesterType);

        SelfAssessmentResult result = findResultWithAccess(memberId, userType, requesterId, requesterType);

        return result.getAnswers().stream()
                .filter(a -> a.isNo() || a.isPartial() || a.getCriticalViolation())
                .map(this::toViolationDto)
                .collect(Collectors.toList());
    }

    /**
     * 본사가 소속 협력사들의 평가 결과 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<SelfAssessmentResponse> getPartnerResults(Long headquartersId, String requesterType) {
        if (!"HEADQUARTERS".equals(requesterType)) {
            throw new IllegalArgumentException("본사만 접근 가능합니다.");
        }

        List<SelfAssessmentResult> results = resultRepository.findByHeadquartersIdAndUserTypeOrderByCreatedAtDesc(
                headquartersId, "PARTNER");

        return results.stream()
                .map(SelfAssessmentResult::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 협력사의 최신 결과 조회 (본사 전용)
     */
    @Transactional(readOnly = true)
    public SelfAssessmentResponse getPartnerResult(Long partnerId, Long headquartersId, String requesterType) {
        if (!"HEADQUARTERS".equals(requesterType)) {
            throw new IllegalArgumentException("본사만 접근 가능합니다.");
        }

        SelfAssessmentResult result = resultRepository.findTopByMemberIdAndHeadquartersIdOrderByCreatedAtDesc(
                        partnerId, headquartersId)
                .orElseThrow(() -> new IllegalStateException("해당 협력사의 평가 결과가 없습니다."));

        return result.toResponse();
    }

    /**
     * 기존 결과 삭제
     */
    @Transactional
    public void deleteByMemberId(Long memberId, String userType) {
        resultRepository.findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(memberId, userType).ifPresent(result -> {
            answerRepository.deleteAll(result.getAnswers());
            resultRepository.delete(result);
            log.info("기존 자가진단 결과 삭제 완료 - memberId: {}, userType: {}", memberId, userType);
        });
    }

    // === 권한 검증 및 내부 유틸리티 메서드 ===

    /**
     * 접근 권한 검증
     */
    private void validateAccess(Long targetMemberId, String targetUserType, Long requesterId, String requesterType) {
        if ("PARTNER".equals(requesterType)) {
            // 협력사는 자신의 데이터만 접근 가능
            if (!requesterId.equals(targetMemberId)) {
                log.warn("협력사 권한 위반 - requesterId: {}, targetMemberId: {}", requesterId, targetMemberId);
                throw new IllegalArgumentException("접근 권한이 없습니다.");
            }
        } else if ("HEADQUARTERS".equals(requesterType)) {
            // 본사는 자신의 데이터와 소속 협력사 데이터 접근 가능
            if (!requesterId.equals(targetMemberId)) {
                // 협력사 데이터에 접근하는 경우, 본사-협력사 관계 확인
                if (!"PARTNER".equals(targetUserType)) {
                    log.warn("본사 권한 위반 - 다른 본사 데이터 접근 시도. requesterId: {}, targetMemberId: {}", requesterId, targetMemberId);
                    throw new IllegalArgumentException("다른 본사의 데이터에 접근할 수 없습니다.");
                }
                // 추가 검증: 해당 협력사가 실제로 이 본사 소속인지 확인
                validateHeadquartersPartnerRelation(requesterId, targetMemberId);
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다: " + requesterType);
        }
    }

    /**
     * 본사-협력사 관계 검증
     */
    private void validateHeadquartersPartnerRelation(Long headquartersId, Long partnerId) {
        boolean exists = resultRepository.findTopByMemberIdAndHeadquartersIdOrderByCreatedAtDesc(partnerId, headquartersId)
                .isPresent();

        if (!exists) {
            log.warn("본사-협력사 관계 없음 - headquartersId: {}, partnerId: {}", headquartersId, partnerId);
            throw new IllegalArgumentException("해당 협력사는 귀하의 소속이 아닙니다.");
        }
    }

    /**
     * 권한에 따른 결과 조회
     */
    private SelfAssessmentResult findResultWithAccess(Long memberId, String userType, Long requesterId, String requesterType) {
        SelfAssessmentResult result;

        if ("HEADQUARTERS".equals(requesterType)) {
            if (memberId.equals(requesterId)) {
                // 본사가 자신의 데이터 조회
                result = resultRepository.findTopByMemberIdAndUserTypeAndHeadquartersIdOrderByCreatedAtDesc(
                                memberId, userType, requesterId)
                        .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));
            } else {
                // 본사가 협력사 데이터 조회
                result = resultRepository.findTopByMemberIdAndHeadquartersIdOrderByCreatedAtDesc(
                                memberId, requesterId)
                        .orElseThrow(() -> new IllegalStateException("해당 협력사의 평가 결과가 없습니다."));
            }
        } else {
            // 협력사는 자신의 데이터만 조회
            result = resultRepository.findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(memberId, userType)
                    .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));
        }

        return result;
    }

    // === 기존 변환 메서드들 (하위 호환성 유지) ===

    /**
     * @deprecated 권한 검증이 없는 기존 메서드. getResult(memberId, userType, requesterId, requesterType) 사용 권장
     */
    @Deprecated
    @Transactional(readOnly = true)
    public SelfAssessmentResponse getResult(Long memberId, String userType) {
        log.warn("권한 검증 없는 getResult 호출 - memberId: {}, userType: {}", memberId, userType);
        SelfAssessmentResult result = resultRepository.findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(memberId, userType)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));
        return result.toResponse();
    }

    /**
     * @deprecated 권한 검증이 없는 기존 메서드. getFullResult(memberId, userType, requesterId, requesterType) 사용 권장
     */
    @Deprecated
    @Transactional(readOnly = true)
    public SelfAssessmentFullResponse getFullResult(Long memberId, String userType) {
        log.warn("권한 검증 없는 getFullResult 호출 - memberId: {}, userType: {}", memberId, userType);
        SelfAssessmentResult result = resultRepository.findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(memberId, userType)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));

        List<SelfAssessmentAnswerDto> answers = result.getAnswers().stream()
                .map(this::toAnswerDto)
                .collect(Collectors.toList());

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
     * @deprecated 권한 검증이 없는 기존 메서드. getViolations(memberId, userType, requesterId, requesterType) 사용 권장
     */
    @Deprecated
    @Transactional(readOnly = true)
    public List<ViolationDto> getViolations(Long memberId, String userType) {
        log.warn("권한 검증 없는 getViolations 호출 - memberId: {}, userType: {}", memberId, userType);
        SelfAssessmentResult result = resultRepository.findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(memberId, userType)
                .orElseThrow(() -> new IllegalStateException("평가 결과가 없습니다."));

        return result.getAnswers().stream()
                .filter(a -> a.isNo() || a.isPartial() || a.getCriticalViolation())
                .map(this::toViolationDto)
                .collect(Collectors.toList());
    }

    // === 내부 변환 메서드 ===

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