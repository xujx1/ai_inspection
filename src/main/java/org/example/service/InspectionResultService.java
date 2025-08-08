package org.example.service;

import org.example.dto.InspectionResultDTO;
import org.example.query.InspectionResultQuery;
import org.example.result.PojoResult;

import java.util.List;

public interface InspectionResultService {

    /**
     * 查询巡检case
     *
     * @param query
     * @return
     */
    PojoResult<List<InspectionResultDTO>> queryInspectionCase(InspectionResultQuery query);

    /**
     * 查询图片
     * @param id
     * @param picName
     * @return
     */
    PojoResult<String> getInspectionPicture(Integer id, String picName);

    /**
     * 查询巡检case
     *
     * @param dto
     * @return
     */
    PojoResult<Boolean> insertInspectionCase(InspectionResultDTO dto);

    /**
     * 查询巡检case
     *
     * @param dto
     * @return
     */
    PojoResult<Boolean> updateInspectionCase(InspectionResultDTO dto);

    /**
     * 查询巡检case
     *
     * @param query
     * @return
     */
    PojoResult<Boolean> deleteInspectionCase(InspectionResultDTO query);
}
