package org.example.service.impl;

import org.example.dto.InspectionTagDTO;
import org.example.entity.InspectionTag;
import org.example.mapper.InspectionTagMapper;
import org.example.query.InspectionTagQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡检标签服务实现类
 */
@Service
public class InspectionTagServiceImpl implements InspectionTagService {

    private static final Logger log = LoggerFactory.getLogger(InspectionTagServiceImpl.class);

    @Autowired
    private InspectionTagMapper inspectionTagMapper;

    @Override
    public PojoResult<List<InspectionTagDTO>> queryTagList(InspectionTagQuery query) {
        PojoResult<List<InspectionTagDTO>> result = new PojoResult<>();
        try {
            log.info("查询Tag列表，查询条件: {}", query);
            
            List<InspectionTag> tagList = inspectionTagMapper.selectByCondition(
                query.getTag(),
                query.getTagName(),
                query.getTagDesc()
            );
            
            List<InspectionTagDTO> dtoList = tagList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            result.setContent(dtoList);
            result.setSuccess(true);
            log.info("查询成功，返回 {} 条记录", dtoList.size());
            
        } catch (Exception e) {
            log.error("查询Tag列表失败", e);
            result.setSuccess(false);
            result.setErrorMsg("查询失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<InspectionTagDTO> queryTagById(Integer id) {
        PojoResult<InspectionTagDTO> result = new PojoResult<>();
        try {
            log.info("根据ID查询Tag: {}", id);
            
            InspectionTag tag = inspectionTagMapper.selectById(id);
            if (tag != null) {
                result.setContent(convertToDTO(tag));
                result.setSuccess(true);
                log.info("查询成功");
            } else {
                result.setSuccess(false);
                result.setErrorMsg("记录不存在");
            }
            
        } catch (Exception e) {
            log.error("根据ID查询Tag失败", e);
            result.setSuccess(false);
            result.setErrorMsg("查询失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> insertTag(InspectionTagDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("新增Tag: {}", dto);
            
            InspectionTag tag = convertToEntity(dto);
            Date now = new Date();
            tag.setGmtCreate(now);
            tag.setGmtModified(now);
            tag.setIsDeleted("n");
            
            int count = inspectionTagMapper.insert(tag);
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("新增成功");
            } else {
                result.setErrorMsg("新增失败");
            }
            
        } catch (Exception e) {
            log.error("新增Tag失败", e);
            result.setSuccess(false);
            result.setErrorMsg("新增失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> updateTag(InspectionTagDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("更新Tag: {}", dto);
            
            InspectionTag tag = convertToEntity(dto);
            tag.setGmtModified(new Date());
            
            int count = inspectionTagMapper.updateById(tag);
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("更新成功");
            } else {
                result.setErrorMsg("更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新Tag失败", e);
            result.setSuccess(false);
            result.setErrorMsg("更新失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<Boolean> deleteTag(InspectionTagDTO dto) {
        PojoResult<Boolean> result = new PojoResult<>();
        try {
            log.info("删除Tag: {}", dto.getId());
            
            int count = inspectionTagMapper.logicalDeleteById(dto.getId());
            
            result.setContent(count > 0);
            result.setSuccess(count > 0);
            if (count > 0) {
                log.info("删除成功");
            } else {
                result.setErrorMsg("删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除Tag失败", e);
            result.setSuccess(false);
            result.setErrorMsg("删除失败: " + e.getMessage());
        }
        return result;
    }

    @Override
    public PojoResult<List<InspectionTagDTO>> queryAllValidTags() {
        PojoResult<List<InspectionTagDTO>> result = new PojoResult<>();
        try {
            log.info("查询所有有效的Tag");
            
            List<InspectionTag> tagList = inspectionTagMapper.selectAllValid();
            
            List<InspectionTagDTO> dtoList = tagList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            result.setContent(dtoList);
            result.setSuccess(true);
            log.info("查询成功，返回 {} 条记录", dtoList.size());
            
        } catch (Exception e) {
            log.error("查询所有有效Tag失败", e);
            result.setSuccess(false);
            result.setErrorMsg("查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 实体转DTO
     */
    private InspectionTagDTO convertToDTO(InspectionTag tag) {
        InspectionTagDTO dto = new InspectionTagDTO();
        dto.setId(tag.getId());
        dto.setTag(tag.getTag());
        dto.setTagName(tag.getTagName());
        dto.setTagDesc(tag.getTagDesc());
        dto.setAccount(tag.getAccount());
        dto.setPassword(tag.getPassword());
        dto.setLoginUrl(tag.getLoginUrl());
        dto.setInspectionUrl(tag.getInspectionUrl());
        dto.setIsDeleted(tag.getIsDeleted());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (tag.getGmtCreate() != null) {
            dto.setGmtCreate(sdf.format(tag.getGmtCreate()));
        }
        if (tag.getGmtModified() != null) {
            dto.setGmtModified(sdf.format(tag.getGmtModified()));
        }
        
        return dto;
    }

    /**
     * DTO转实体
     */
    private InspectionTag convertToEntity(InspectionTagDTO dto) {
        InspectionTag tag = new InspectionTag();
        tag.setId(dto.getId());
        tag.setTag(dto.getTag());
        tag.setTagName(dto.getTagName());
        tag.setTagDesc(dto.getTagDesc());
        tag.setAccount(dto.getAccount());
        tag.setPassword(dto.getPassword());
        tag.setLoginUrl(dto.getLoginUrl());
        tag.setInspectionUrl(dto.getInspectionUrl());
        tag.setIsDeleted(dto.getIsDeleted());
        
        return tag;
    }
}