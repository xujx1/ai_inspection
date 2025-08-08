package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.InspectionTag;

import java.util.List;

/**
 * 巡检标签Mapper接口
 */
@Mapper
public interface InspectionTagMapper {

    /**
     * 新增记录
     */
    int insert(InspectionTag inspectionTag);

    /**
     * 批量插入
     */
    int insertBatch(List<InspectionTag> list);

    /**
     * 根据ID查询
     */
    InspectionTag selectById(Integer id);

    /**
     * 根据条件查询列表
     */
    List<InspectionTag> selectByCondition(@Param("tag") String tag,
                                         @Param("tagName") String tagName,
                                         @Param("tagDesc") String tagDesc);

    /**
     * 根据条件统计记录数
     */
    Integer countByCondition(@Param("tag") String tag,
                           @Param("tagName") String tagName,
                           @Param("tagDesc") String tagDesc);

    /**
     * 更新记录
     */
    int updateById(InspectionTag inspectionTag);

    /**
     * 根据ID删除（物理删除）
     */
    int deleteById(Integer id);

    /**
     * 根据ID逻辑删除
     */
    int logicalDeleteById(Integer id);

    /**
     * 查询所有有效的标签（用于下拉框）
     */
    List<InspectionTag> selectAllValid();

    /**
     * 查询所有标签（包括已删除的）
     */
    List<InspectionTag> selectAll();
}