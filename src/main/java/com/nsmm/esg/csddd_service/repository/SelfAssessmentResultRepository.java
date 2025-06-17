package com.nsmm.esg.csddd_service.repository;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 📌 SelfAssessmentResultRepository
 * - 자가진단 결과(Result) 엔티티에 대한 JPA 리포지토리
 * - 기본적인 CRUD 기능은 JpaRepository에서 자동 제공
 */
public interface SelfAssessmentResultRepository extends JpaRepository<SelfAssessmentResult, Long> {

    /**
     * 🔍 특정 회원(memberId)의 최신 자가진단 결과 1건을 조회
     * - createdAt 기준 내림차순으로 정렬하여 가장 최근의 결과 반환
     * - 평가 결과 요약, 상세 조회 등에 활용
     *
     * @param memberId 회원 고유 ID
     * @return 가장 최근 자가진단 결과(Optional)
     */
    Optional<SelfAssessmentResult> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}