package com.nsmm.esg.csddd_service.repository;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *  SelfAssessmentResultRepository
 * - 자가진단 결과(Result) 엔티티에 대한 JPA 리포지토리
 * - 기본적인 CRUD 기능은 JpaRepository에서 자동 제공
 */
public interface SelfAssessmentResultRepository extends JpaRepository<SelfAssessmentResult, Long> {

    // 1. 기본 조회 (기존)
    Optional<SelfAssessmentResult> findTopByMemberIdAndUserTypeOrderByCreatedAtDesc(
            Long memberId, String userType);

    // 2. 본사가 자신의 데이터 조회
    Optional<SelfAssessmentResult> findTopByMemberIdAndUserTypeAndHeadquartersIdOrderByCreatedAtDesc(
            Long memberId, String userType, Long headquartersId);

    // 3. 본사가 소속 협력사들의 데이터 조회 (리스트)
    List<SelfAssessmentResult> findByHeadquartersIdAndUserTypeOrderByCreatedAtDesc(
            Long headquartersId, String userType);

    // 4. 특정 협력사의 최신 결과 조회
    Optional<SelfAssessmentResult> findTopByMemberIdAndHeadquartersIdOrderByCreatedAtDesc(
            Long partnerId, Long headquartersId);

    Optional<SelfAssessmentResult> findTopByMemberIdAndHeadquartersIdAndUserTypeOrderByCreatedAtDesc(
            Long subPartnerId, Long headquartersId, String secondTierPartner);
}