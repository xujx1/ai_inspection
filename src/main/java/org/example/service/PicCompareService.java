package org.example.service;

import org.example.dto.InspectionResultDTO;
import org.example.entity.InspectionResult;

import java.util.List;

public interface PicCompareService {

    void picCompare(List<InspectionResult> resultList);

    void picCompareSingle(InspectionResult result);
}
