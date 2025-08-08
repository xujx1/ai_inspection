package org.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dto.InspectionResultDTO;
import org.example.entity.InspectionResult;
import org.example.mapper.InspectionResultMapper;
import org.example.query.InspectionResultQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionResultService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InspectionServiceImpl implements InspectionResultService {

    private static final Logger log = LoggerFactory.getLogger(InspectionServiceImpl.class);

    @Autowired
    private InspectionResultMapper inspectionResultMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PojoResult<List<InspectionResultDTO>> queryInspectionCase(InspectionResultQuery query) {
        PojoResult<List<InspectionResultDTO>> result = new PojoResult<>();
        try {
            log.info("查询巡检案例，查询条件: {}", query);

            List<InspectionResult> inspectionResults = inspectionResultMapper.selectByCondition(
                    query.getWorkNo(),
                    query.getMethodName(),
                    query.getCheckFlag(),
                    query.getInspectionType(),
                    query.getTag()
            );

            List<InspectionResultDTO> dtoList = inspectionResults.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            result.setContent(dtoList);
            result.setSuccess(true);
            log.info("查询成功，返回 {} 条记录", dtoList.size());

        } catch (Exception e) {
            log.error("查询巡检案例失败", e);
            result.setSuccess(false);
            result.setError("QUERY_ERROR", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<String> getInspectionPicture(Integer id, String picName) {
        PojoResult<String> result = new PojoResult<>();

        try {
            if (id == null || org.apache.commons.lang3.StringUtils.isEmpty(picName)) {
                result.setSuccess(false);
                result.setErrorCode("PARAM_ERROR");
                result.setErrorMsg("参数不能为空");
                return result;
            }

            // 根据ID查询巡检结果
            InspectionResult resultDO = inspectionResultMapper.selectById(id);
            if (resultDO == null) {
                result.setSuccess(false);
                result.setErrorCode("DATA_NOT_FOUND");
                result.setErrorMsg("未找到对应的巡检记录");
                return result;
            }


            // 解析req字段获取图片名称
            String pictureData = null;
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(resultDO.getReq())) {
                Map<String, String> reqMap = JSON.parseObject(resultDO.getReq(), new TypeReference<Map<String, String>>() {
                });

                String thisPicName = reqMap.get("thisPicName");
                String lastPicName = reqMap.get("lastPicName");

                if (picName.equals(thisPicName) && org.apache.commons.lang3.StringUtils.isNotEmpty(resultDO.getThisResp())) {
                    // 返回thisResp（解密后的图片数据）
                    pictureData = resultDO.getThisResp();
                } else if (picName.equals(lastPicName) && org.apache.commons.lang3.StringUtils.isNotEmpty(resultDO.getLastResp())) {
                    // 返回lastResp（解密后的图片数据）
                    pictureData = resultDO.getLastResp();
                } else {
                    result.setSuccess(false);
                    result.setErrorCode("PIC_NOT_FOUND");
                    result.setErrorMsg("未找到对应的图片数据");
                    return result;
                }
            } else {
                result.setSuccess(false);
                result.setErrorCode("REQ_EMPTY");
                result.setErrorMsg("请求参数为空");
                return result;
            }

            result.setSuccess(true);
            result.setContent(pictureData);
            log.info("获取巡检图片数据成功，ID: {}, picName: {}", id, picName);

        } catch (Exception e) {
            log.error("获取巡检图片数据失败，ID: {}, picName: {}", id, picName, e);
            result.setSuccess(false);
            result.setErrorCode("GET_PIC_ERROR");
            result.setErrorMsg("获取图片数据失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public PojoResult<Boolean> insertInspectionCase(InspectionResultDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("新增巡检案例: {}", dto);

            InspectionResult entity = convertToEntity(dto);
            entity.setGmtCreate(LocalDateTime.now());
            entity.setGmtModified(LocalDateTime.now());
            entity.setIsDeleted("n");

            int rows = inspectionResultMapper.insert(entity);
            boolean success = rows > 0;

            result.setContent(success);
            result.setSuccess(success);

            if (success) {
                log.info("新增巡检案例成功，影响行数: {}", rows);
            } else {
                log.warn("新增巡检案例失败，影响行数: {}", rows);
                result.setError("INSERT_ERROR", "新增失败");
            }

        } catch (Exception e) {
            log.error("新增巡检案例失败", e);
            result.setSuccess(false);
            result.setContent(false);
            result.setError("INSERT_ERROR", "新增失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> updateInspectionCase(InspectionResultDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("更新巡检案例: {}", dto);

            if (dto.getId() == null) {
                result.setSuccess(false);
                result.setContent(false);
                result.setError("PARAM_ERROR", "更新记录时ID不能为空");
                return result;
            }

            InspectionResult entity = convertToEntity(dto);
            entity.setGmtModified(LocalDateTime.now());

            int rows = inspectionResultMapper.updateById(entity);
            boolean success = rows > 0;

            result.setContent(success);
            result.setSuccess(success);

            if (success) {
                log.info("更新巡检案例成功，影响行数: {}", rows);
            } else {
                log.warn("更新巡检案例失败，影响行数: {}", rows);
                result.setError("UPDATE_ERROR", "更新失败，可能记录不存在");
            }

        } catch (Exception e) {
            log.error("更新巡检案例失败", e);
            result.setSuccess(false);
            result.setContent(false);
            result.setError("UPDATE_ERROR", "更新失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> deleteInspectionCase(InspectionResultDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("删除巡检案例: {}", dto);

            if (dto.getId() == null) {
                result.setSuccess(false);
                result.setContent(false);
                result.setError("PARAM_ERROR", "删除记录时ID不能为空");
                return result;
            }

            // 使用逻辑删除
            int rows = inspectionResultMapper.logicalDeleteById(dto.getId());
            boolean success = rows > 0;

            result.setContent(success);
            result.setSuccess(success);

            if (success) {
                log.info("删除巡检案例成功，影响行数: {}", rows);
            } else {
                log.warn("删除巡检案例失败，影响行数: {}", rows);
                result.setError("DELETE_ERROR", "删除失败，可能记录不存在");
            }

        } catch (Exception e) {
            log.error("删除巡检案例失败", e);
            result.setSuccess(false);
            result.setContent(false);
            result.setError("DELETE_ERROR", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 实体转DTO
     */
    private InspectionResultDTO convertToDTO(InspectionResult entity) {
        InspectionResultDTO dto = new InspectionResultDTO();
        BeanUtils.copyProperties(entity, dto);

        // 时间格式转换
        if (entity.getGmtCreate() != null) {
            dto.setGmtCreate(entity.getGmtCreate().format(DATE_TIME_FORMATTER));
        }
        if (entity.getGmtModified() != null) {
            dto.setGmtModified(entity.getGmtModified().format(DATE_TIME_FORMATTER));
        }

        return dto;
    }

    /**
     * DTO转实体
     */
    private InspectionResult convertToEntity(InspectionResultDTO dto) {
        InspectionResult entity = new InspectionResult();
        BeanUtils.copyProperties(dto, entity);

        // 时间格式转换 - 如果DTO中有字符串时间，需要转换
        if (StringUtils.hasText(dto.getGmtCreate())) {
            try {
                entity.setGmtCreate(LocalDateTime.parse(dto.getGmtCreate(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                log.warn("解析创建时间失败: {}", dto.getGmtCreate());
            }
        }
        if (StringUtils.hasText(dto.getGmtModified())) {
            try {
                entity.setGmtModified(LocalDateTime.parse(dto.getGmtModified(), DATE_TIME_FORMATTER));
            } catch (Exception e) {
                log.warn("解析修改时间失败: {}", dto.getGmtModified());
            }
        }

        return entity;
    }
}