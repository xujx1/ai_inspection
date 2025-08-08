package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dto.InspectionResultDTO;
import org.example.query.InspectionResultQuery;
import org.example.result.PojoResult;
import org.example.service.InspectionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巡检结果控制器
 */
@RestController
@RequestMapping("/api/inspection")
@CrossOrigin(origins = "*")
public class InspectionResultController {

    private static final Logger log = LoggerFactory.getLogger(InspectionResultController.class);

    @Autowired
    private InspectionResultService inspectionResultService;

    /**
     * 查询巡检案例
     */
    @PostMapping("/query")
    public PojoResult<List<InspectionResultDTO>> queryInspectionCase(@RequestBody InspectionResultQuery query) {
        log.info("接收查询巡检案例请求: {}", query);
        return inspectionResultService.queryInspectionCase(query);
    }


    @GetMapping("/queryEpmInspectionPicture")
    public PojoResult<String> getInspectionPicture(
            @RequestParam("id") Integer id, @RequestParam("picName") String picName) {

        return inspectionResultService.getInspectionPicture(id, picName);
    }

    /**
     * 新增巡检案例
     */
    @PostMapping("/add")
    public PojoResult<Boolean> addInspectionCase(@RequestBody InspectionResultDTO dto) {
        log.info("接收新增巡检案例请求: {}", dto);
        return inspectionResultService.insertInspectionCase(dto);
    }

    /**
     * 更新巡检案例
     */
    @PostMapping("/update")
    public PojoResult<Boolean> updateInspectionCase(@RequestBody InspectionResultDTO dto) {
        log.info("接收更新巡检案例请求: {}", dto);
        return inspectionResultService.updateInspectionCase(dto);
    }

    /**
     * 删除巡检案例
     */
    @PostMapping("/delete")
    public PojoResult<Boolean> deleteInspectionCase(@RequestBody InspectionResultDTO dto) {
        log.info("接收删除巡检案例请求: {}", dto);
        return inspectionResultService.deleteInspectionCase(dto);
    }

    /**
     * 根据ID删除巡检案例
     */
    @DeleteMapping("/{id}")
    public PojoResult<Boolean> deleteInspectionCaseById(@PathVariable Integer id) {
        log.info("接收根据ID删除巡检案例请求, ID: {}", id);
        InspectionResultDTO dto = new InspectionResultDTO();
        dto.setId(id);
        return inspectionResultService.deleteInspectionCase(dto);
    }
}