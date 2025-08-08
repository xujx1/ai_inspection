package org.example.service.impl;

import org.example.dto.InspectionPromptDTO;
import org.example.entity.InspectionPrompt;
import org.example.mapper.InspectionPromptMapper;
import org.example.query.InspectionPromptQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionPromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡检Prompt服务实现类
 */
@Service
public class InspectionPromptServiceImpl implements InspectionPromptService {

    private static final Logger log = LoggerFactory.getLogger(InspectionPromptServiceImpl.class);

    @Autowired
    private InspectionPromptMapper inspectionPromptMapper;

    @Override
    public PojoResult<List<InspectionPromptDTO>> queryPromptList(InspectionPromptQuery query) {
        PojoResult<List<InspectionPromptDTO>> result = new PojoResult<>();
        try {
            log.info("查询Prompt列表，查询条件: {}", query);
            
            List<InspectionPrompt> promptList = inspectionPromptMapper.selectByCondition(
                query.getTag(),
                query.getPromptName(),
                query.getMethodName()
            );
            
            List<InspectionPromptDTO> dtoList = promptList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            result.setContent(dtoList);
            result.setSuccess(true);
            log.info("查询成功，返回 {} 条记录", dtoList.size());
            
        } catch (Exception e) {
            log.error("查询Prompt列表失败", e);
            result.setSuccess(false);
            result.setErrorMsg("查询失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<InspectionPromptDTO> queryPromptById(Integer id) {
        PojoResult<InspectionPromptDTO> result = new PojoResult<>();
        try {
            log.info("根据ID查询Prompt: {}", id);
            
            InspectionPrompt prompt = inspectionPromptMapper.selectById(id);
            if (prompt != null) {
                result.setContent(convertToDTO(prompt));
                result.setSuccess(true);
                log.info("查询成功");
            } else {
                result.setSuccess(false);
                result.setErrorMsg("记录不存在");
            }
            
        } catch (Exception e) {
            log.error("根据ID查询Prompt失败", e);
            result.setSuccess(false);
            result.setErrorMsg("查询失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> insertPrompt(InspectionPromptDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("新增Prompt: {}", dto);
            
            InspectionPrompt prompt = convertToEntity(dto);
            Date now = new Date();
            prompt.setGmtCreate(now);
            prompt.setGmtModified(now);
            prompt.setIsDeleted("n");
            
            int count = inspectionPromptMapper.insert(prompt);
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("新增成功");
            } else {
                result.setErrorMsg("新增失败");
            }
            
        } catch (Exception e) {
            log.error("新增Prompt失败", e);
            result.setSuccess(false);
            result.setErrorMsg("新增失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> updatePrompt(InspectionPromptDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("更新Prompt: {}", dto);
            
            InspectionPrompt prompt = convertToEntity(dto);
            prompt.setGmtModified(new Date());
            
            int count = inspectionPromptMapper.updateById(prompt);
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("更新成功");
            } else {
                result.setErrorMsg("更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新Prompt失败", e);
            result.setSuccess(false);
            result.setErrorMsg("更新失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> deletePrompt(InspectionPromptDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("删除Prompt: {}", dto.getId());
            
            int count = inspectionPromptMapper.logicalDeleteById(dto.getId());
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("删除成功");
            } else {
                result.setErrorMsg("删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除Prompt失败", e);
            result.setSuccess(false);
            result.setErrorMsg("删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 实体转DTO
     */
    private InspectionPromptDTO convertToDTO(InspectionPrompt prompt) {
        InspectionPromptDTO dto = new InspectionPromptDTO();
        dto.setId(prompt.getId());
        dto.setTag(prompt.getTag());
        dto.setPromptName(prompt.getPromptName());
        dto.setMethodName(prompt.getMethodName());
        dto.setPromptContent(prompt.getPromptContent());
        dto.setIsDeleted(prompt.getIsDeleted());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (prompt.getGmtCreate() != null) {
            dto.setGmtCreate(sdf.format(prompt.getGmtCreate()));
        }
        if (prompt.getGmtModified() != null) {
            dto.setGmtModified(sdf.format(prompt.getGmtModified()));
        }
        
        return dto;
    }

    /**
     * DTO转实体
     */
    private InspectionPrompt convertToEntity(InspectionPromptDTO dto) {
        InspectionPrompt prompt = new InspectionPrompt();
        prompt.setId(dto.getId());
        prompt.setTag(dto.getTag());
        prompt.setPromptName(dto.getPromptName());
        prompt.setMethodName(dto.getMethodName());
        prompt.setPromptContent(dto.getPromptContent());
        prompt.setIsDeleted(dto.getIsDeleted());
        
        return prompt;
    }
}