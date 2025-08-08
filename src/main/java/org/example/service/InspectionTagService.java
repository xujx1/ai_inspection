package org.example.service;

import org.example.dto.InspectionTagDTO;
import org.example.query.InspectionTagQuery;
import org.example.result.PojoResult;

import java.util.List;

/**
 * 巡检标签服务接口
 */
public interface InspectionTagService {

    /**
     * 查询标签列表
     */
    PojoResult<List<InspectionTagDTO>> queryTagList(InspectionTagQuery query);

    /**
     * 根据ID查询标签
     */
    PojoResult<InspectionTagDTO> queryTagById(Integer id);

    /**
     * 新增标签
     */
    PojoResult<Boolean> insertTag(InspectionTagDTO dto);

    /**
     * 更新标签
     */
    PojoResult<Boolean> updateTag(InspectionTagDTO dto);

    /**
     * 删除标签
     */
    PojoResult<Boolean> deleteTag(InspectionTagDTO dto);

    /**
     * 查询所有有效的标签（用于下拉框）
     */
    PojoResult<List<InspectionTagDTO>> queryAllValidTags();
}