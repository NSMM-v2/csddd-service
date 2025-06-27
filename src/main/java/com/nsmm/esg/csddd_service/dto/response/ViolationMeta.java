package com.nsmm.esg.csddd_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViolationMeta {
    private String category;
    private String penaltyInfo;
    private String legalBasis;
}