package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.InspectionResult;
import org.example.enums.InspectionTypeEnum;
import org.example.mapper.InspectionResultMapper;
import org.example.service.PicCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
public class InspectionCheckProcessor {

    @Autowired
    private PicCompareService picCompareService;

    @Autowired
    private InspectionResultMapper inspectionResultMapper;

    /**
     * 定时任务执行方法
     * 每1分钟执行一次页面截图比对任务
     */
    @Scheduled(fixedRate = 60000)
    public void executeCheckTask() {

        List<InspectionResult> resultDOList = inspectionResultMapper.selectByCondition(
                null, null, null, InspectionTypeEnum.PAGE.getCode(), null);

        // 使用公共的图片比较服务
        picCompareService.picCompare(resultDOList);

    }
}