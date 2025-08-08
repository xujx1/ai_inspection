package org.example.service;

import org.example.dto.InspectionPromptDTO;
import org.example.query.InspectionPromptQuery;
import org.example.result.PojoResult;

import java.util.List;

/**
 * 巡检Prompt服务接口
 */
public interface InspectionPromptService {

    /**
     * 查询Prompt列表
     */
    PojoResult<List<InspectionPromptDTO>> queryPromptList(InspectionPromptQuery query);

    /**
     * 根据ID查询Prompt
     */
    PojoResult<InspectionPromptDTO> queryPromptById(Integer id);

    /**
     * 新增Prompt
     */
    PojoResult<Boolean> insertPrompt(InspectionPromptDTO dto);

    /**
     * 更新Prompt
     */
    PojoResult<Boolean> updatePrompt(InspectionPromptDTO dto);

    /**
     * 删除Prompt
     */
    PojoResult<Boolean> deletePrompt(InspectionPromptDTO dto);
}