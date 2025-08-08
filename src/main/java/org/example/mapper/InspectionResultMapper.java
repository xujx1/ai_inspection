package org.example.mapper;

import org.example.entity.InspectionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 巡检结果Mapper接口
 */
@Mapper
public interface InspectionResultMapper {
    
    /**
     * 新增巡检结果
     */
    int insert(InspectionResult record);
    
    /**
     * 根据ID查询
     */
    InspectionResult selectById(@Param("id") Integer id);
    
    /**
     * 根据条件查询列表
     */
    List<InspectionResult> selectByCondition(
                                             @Param("workNo") String workNo,
                                             @Param("methodName") String methodName,
                                             @Param("checkFlag") String checkFlag,
                                             @Param("inspectionType") String inspectionType,
                                             @Param("tag") String tag);
    
    /**
     * 查询所有记录
     */
    List<InspectionResult> selectAll();
    
    /**
     * 根据ID更新
     */
    int updateById(InspectionResult record);
    
    /**
     * 根据ID物理删除
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据ID逻辑删除(更新is_deleted字段)
     */
    int logicalDeleteById(@Param("id") Integer id);
    
    /**
     * 根据条件统计记录数
     */
    int countByCondition(
                        @Param("workNo") String workNo,
                        @Param("methodName") String methodName,
                        @Param("checkFlag") String checkFlag,
                        @Param("inspectionType") String inspectionType,
                        @Param("tag") String tag);
    
    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<InspectionResult> list);
    
    /**
     * 根据工号和方法名查询最新记录
     */
    InspectionResult selectLatestByWorkNoAndMethod(@Param("workNo") String workNo,
                                                   @Param("methodName") String methodName);

    /**
     * 根据tag和类型查询记录
     */
    List<InspectionResult> selectByTagAndType(@Param("tag") String tag, 
                                              @Param("inspectionType") String inspectionType);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(InspectionResult record);
}