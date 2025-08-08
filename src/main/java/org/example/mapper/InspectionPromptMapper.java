package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.InspectionPrompt;

import java.util.List;

/**
 * 巡检Prompt数据访问层
 */
@Mapper
public interface InspectionPromptMapper {

    /**
     * 新增记录
     */
    int insert(InspectionPrompt inspectionPrompt);

    /**
     * 批量插入
     */
    int insertBatch(List<InspectionPrompt> list);

    /**
     * 根据ID查询
     */
    InspectionPrompt selectById(@Param("id") Integer id);
    
    /**
     * 根据条件查询列表
     */
    List<InspectionPrompt> selectByCondition(@Param("tag") String tag,
                                           @Param("promptName") String promptName,
                                           @Param("methodName") String methodName);
    
    /**
     * 更新记录
     */
    int updateById(InspectionPrompt inspectionPrompt);

    /**
     * 根据ID删除（物理删除）
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据ID逻辑删除
     */
    int logicalDeleteById(@Param("id") Integer id);
    
    /**
     * 根据条件统计记录数
     */
    int countByCondition(@Param("tag") String tag,
                        @Param("promptName") String promptName,
                        @Param("methodName") String methodName);
}