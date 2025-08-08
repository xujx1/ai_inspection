package org.example.controller;

import org.example.dto.InspectionTagDTO;
import org.example.query.InspectionTagQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巡检标签控制器
 */
@RestController
@RequestMapping("/api/tag")
public class InspectionTagController {

    private static final Logger log = LoggerFactory.getLogger(InspectionTagController.class);

    @Autowired
    private InspectionTagService inspectionTagService;

    /**
     * 查询标签列表
     */
    @PostMapping("/query")
    public PojoResult<List<InspectionTagDTO>> queryTags(@RequestBody InspectionTagQuery query) {
        log.info("接收到查询标签列表请求: {}", query);
        return inspectionTagService.queryTagList(query);
    }

    /**
     * 根据ID查询标签
     */
    @GetMapping("/{id}")
    public PojoResult<InspectionTagDTO> queryTagById(@PathVariable Integer id) {
        log.info("接收到根据ID查询标签请求: {}", id);
        return inspectionTagService.queryTagById(id);
    }

    /**
     * 新增标签
     */
    @PostMapping("/add")
    public PojoResult<Boolean> addTag(@RequestBody InspectionTagDTO dto) {
        log.info("接收到新增标签请求: {}", dto);
        return inspectionTagService.insertTag(dto);
    }

    /**
     * 更新标签
     */
    @PostMapping("/update")
    public PojoResult<Boolean> updateTag(@RequestBody InspectionTagDTO dto) {
        log.info("接收到更新标签请求: {}", dto);
        return inspectionTagService.updateTag(dto);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public PojoResult<Boolean> deleteTag(@PathVariable Integer id) {
        log.info("接收到删除标签请求: {}", id);
        InspectionTagDTO dto = new InspectionTagDTO();
        dto.setId(id);
        return inspectionTagService.deleteTag(dto);
    }

    /**
     * 查询所有有效的标签（用于下拉框）
     */
    @GetMapping("/valid")
    public PojoResult<List<InspectionTagDTO>> queryValidTags() {
        log.info("接收到查询所有有效标签请求");
        return inspectionTagService.queryAllValidTags();
    }
}