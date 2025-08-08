package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dto.InspectionPromptDTO;
import org.example.query.InspectionPromptQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巡检Prompt控制器
 */
@RestController
@RequestMapping("/api/prompt")
@CrossOrigin(origins = "*")
public class InspectionPromptController {

    private static final Logger log = LoggerFactory.getLogger(InspectionPromptController.class);

    @Autowired
    private InspectionPromptService inspectionPromptService;

    /**
     * 查询Prompt列表
     */
    @PostMapping("/query")
    public PojoResult<List<InspectionPromptDTO>> queryPromptList(@RequestBody InspectionPromptQuery query) {
        log.info("接收查询Prompt列表请求: {}", query);
        return inspectionPromptService.queryPromptList(query);
    }

    /**
     * 根据ID查询Prompt
     */
    @GetMapping("/{id}")
    public PojoResult<InspectionPromptDTO> queryPromptById(@PathVariable Integer id) {
        log.info("接收根据ID查询Prompt请求, ID: {}", id);
        return inspectionPromptService.queryPromptById(id);
    }

    /**
     * 新增Prompt
     */
    @PostMapping("/add")
    public PojoResult<Boolean> addPrompt(@RequestBody InspectionPromptDTO dto) {
        log.info("接收新增Prompt请求: {}", dto);
        return inspectionPromptService.insertPrompt(dto);
    }

    /**
     * 更新Prompt
     */
    @PostMapping("/update")
    public PojoResult<Boolean> updatePrompt(@RequestBody InspectionPromptDTO dto) {
        log.info("接收更新Prompt请求: {}", dto);
        return inspectionPromptService.updatePrompt(dto);
    }

    /**
     * 删除Prompt
     */
    @PostMapping("/delete")
    public PojoResult<Boolean> deletePrompt(@RequestBody InspectionPromptDTO dto) {
        log.info("接收删除Prompt请求: {}", dto);
        return inspectionPromptService.deletePrompt(dto);
    }

    /**
     * 根据ID删除Prompt
     */
    @DeleteMapping("/{id}")
    public PojoResult<Boolean> deletePromptById(@PathVariable Integer id) {
        log.info("接收根据ID删除Prompt请求, ID: {}", id);
        InspectionPromptDTO dto = new InspectionPromptDTO();
        dto.setId(id);
        return inspectionPromptService.deletePrompt(dto);
    }
}