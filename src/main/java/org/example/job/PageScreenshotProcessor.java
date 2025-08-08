package org.example.job;

import com.alibaba.fastjson2.TypeReference;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.ScreenshotType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.entity.InspectionResult;
import org.example.entity.InspectionTag;
import org.example.enums.CheckFlagEnum;
import org.example.enums.InspectionTypeEnum;
import org.example.enums.YNTypeEnum;
import org.example.mapper.InspectionResultMapper;
import org.example.mapper.InspectionTagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 页面截图定时任务处理器
 * <p>
 * 功能说明：
 * 1. 从inspection_tag表中读取需要巡检的页面配置
 * 2. 依次登录每个页面并进行截图
 * 3. 将截图结果保存到inspection_result表中
 * <p>
 * 基于现有的EpmAiInspectionProcessor的设计模式，简化为本地场景使用
 */
@Component
public class PageScreenshotProcessor {

    private static final Logger log = LoggerFactory.getLogger(PageScreenshotProcessor.class);

    @Autowired
    private InspectionTagMapper inspectionTagMapper;

    @Autowired
    private InspectionResultMapper inspectionResultMapper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 定时任务执行方法
     * 每1分钟执行一次页面截图任务
     */
    @Scheduled(fixedRate = 60000)
    public void executeScreenshotTask() {
        log.info("开始执行页面截图定时任务");

        try {
            // 1. 查询所有需要巡检的页面配置
            List<InspectionTag> tagList = inspectionTagMapper.selectAll();

            if (CollectionUtils.isEmpty(tagList)) {
                log.info("没有找到需要巡检的页面配置，任务结束");
                return;
            }

            log.info("找到 {} 个页面配置，开始逐个处理", tagList.size());

            // 2. 逐个处理每个页面配置
            for (InspectionTag tag : tagList) {
                try {
                    processPageScreenshot(tag);
                } catch (Exception e) {
                    log.error("处理页面配置失败，tag: {}, error: {}", tag.getTag(), e.getMessage(), e);
                }
            }

            log.info("页面截图定时任务执行完成");

        } catch (Exception e) {
            log.error("页面截图定时任务执行失败", e);
        }
    }

    /**
     * 处理单个页面的截图任务
     *
     * @param tag 页面配置信息
     */
    private void processPageScreenshot(InspectionTag tag) {
        log.info("开始处理页面: {}, 登录URL: {}, 巡检URL: {}", tag.getTagName(), tag.getLoginUrl(), tag.getInspectionUrl());

        String timestamp = DATE_FORMAT.format(new Date());
        String screenshotName = tag.getTag() + "_" + timestamp + ".png";

        try (Playwright playwright = Playwright.create()) {

            // 启动浏览器
            BrowserType browserType = playwright.chromium();
            Browser browser = browserType.launch(new BrowserType.LaunchOptions()
                    .setHeadless(true) // 无头模式运行
                    .setSlowMo(1000)); // 添加延迟以便观察

            // 创建浏览器上下文并设置视口大小
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(1920, 1080)  // 设置分辨率
                    .setDeviceScaleFactor(1));     // 设置缩放因子

            // 创建页面对象
            Page page = context.newPage();

            try {
                // 3. 登录页面
                boolean loginSuccess = performLogin(page, tag);

                if (!loginSuccess) {
                    log.error("页面登录失败，跳过截图: {}", tag.getTag());
                    return;
                }

                // 4. 跳转到巡检页面并截图
                boolean screenshotSuccess = performScreenshot(page, tag, screenshotName);

                if (screenshotSuccess) {
                    log.info("页面截图成功: {}", tag.getTag());
                } else {
                    log.error("页面截图失败: {}", tag.getTag());
                }

            } finally {
                // 关闭浏览器
                browser.close();
            }

        } catch (Exception e) {
            log.error("处理页面截图时发生异常，tag: {}", tag.getTag(), e);
        }
    }

    /**
     * 执行页面登录
     *
     * @param page 页面对象
     * @param tag  页面配置
     * @return 登录是否成功
     */
    private boolean performLogin(Page page, InspectionTag tag) {
        try {
            log.info("开始登录页面: {}", tag.getLoginUrl());

            // 导航到登录页面
            page.navigate(tag.getLoginUrl());
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 等待页面加载完成
            Thread.sleep(2000);

            // 检查是否有登录表单
            if (page.locator("#username").count() > 0 && page.locator("#password").count() > 0) {

                // 填写登录信息
                page.fill("#username", tag.getAccount());
                page.fill("#password", tag.getPassword());

                // 等待一下确保输入完成
                Thread.sleep(1000);

                // 点击登录按钮
                if (page.locator(".login-button").count() > 0) {
                    page.click(".login-button");
                } else if (page.locator("button[type='submit']").count() > 0) {
                    page.click("button[type='submit']");
                } else {
                    // 尝试按Enter键登录
                    page.keyboard().press("Enter");
                }

                // 等待登录完成
                page.waitForLoadState(LoadState.NETWORKIDLE);

                log.info("登录操作完成: {}", tag.getTag());
                return true;

            } else {
                log.warn("未找到登录表单，可能页面已经登录或登录方式不同: {}", tag.getTag());
                return true; // 假设已经登录
            }

        } catch (Exception e) {
            log.error("登录页面失败，tag: {}, error: {}", tag.getTag(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 执行页面截图
     *
     * @param page           页面对象
     * @param tag            页面配置
     * @param screenshotName 截图文件名
     * @return 截图是否成功
     */
    private boolean performScreenshot(Page page, InspectionTag tag, String screenshotName) {
        try {
            log.info("开始截图页面: {}", tag.getInspectionUrl());

            // 导航到巡检页面
            page.navigate(tag.getInspectionUrl());
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 等待页面完全加载

            // 执行全页面截图
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)  // 全页面截图
                    .setType(ScreenshotType.PNG));

            // 保存截图到数据库
            saveScreenshotToDatabase(tag, screenshotName, screenshotBytes);

            log.info("页面截图完成，文件名: {}", screenshotName);
            return true;

        } catch (Exception e) {
            log.error("页面截图失败，tag: {}, error: {}", tag.getTag(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将截图保存到数据库
     *
     * @param tag             页面配置
     * @param screenshotName  截图文件名
     * @param screenshotBytes 截图数据
     */
    private void saveScreenshotToDatabase(InspectionTag tag, String screenshotName, byte[] screenshotBytes) {
        try {
            // 转换图片为Base64字符串
            String base64Image = imageToBase64(screenshotBytes);

            LocalDateTime now = LocalDateTime.now();

            // 查询是否已有记录
            List<InspectionResult> existingResults = inspectionResultMapper.selectByTagAndType(tag.getTag(), InspectionTypeEnum.PAGE.getCode());

            if (CollectionUtils.isNotEmpty(existingResults)) {
                // 更新现有记录
                InspectionResult existingResult = existingResults.get(0);

                // 将当前截图移动到历史截图
                existingResult.setLastResp(existingResult.getThisResp());
                existingResult.setThisResp(base64Image);
                existingResult.setGmtModified(now);
                existingResult.setCheckFlag(CheckFlagEnum.U.getCode()); // 标记为未检查

                // 更新请求信息
                if (StringUtils.isNotEmpty(existingResult.getReq())) {
                    Map<String, String> req = com.alibaba.fastjson2.JSON.parseObject(existingResult.getReq(), new TypeReference<>() {
                    });
                    req.put("lastPicName", req.get("thisPicName"));
                    req.put("thisPicName", screenshotName);
                    existingResult.setReq(com.alibaba.fastjson2.JSON.toJSONString(req));
                }

                inspectionResultMapper.updateByPrimaryKeySelective(existingResult);
                log.info("更新截图记录到数据库，ID: {}", existingResult.getId());

            } else {
                // 创建新记录
                InspectionResult newResult = new InspectionResult();
                newResult.setTag(tag.getTag());
                newResult.setWorkNo("system"); // 系统执行
                newResult.setMethodName("pageScreenshot");
                newResult.setThisResp(base64Image);
                newResult.setGmtCreate(now);
                newResult.setGmtModified(now);
                newResult.setIsDeleted(YNTypeEnum.N.getCode());
                newResult.setCheckFlag(CheckFlagEnum.U.getCode()); // 标记为未检查
                newResult.setInspectionType(InspectionTypeEnum.PAGE.getCode());

                // 设置请求信息

                Map<String, String> req = new HashMap<>() {{
                    put("thisPicName", screenshotName);
                }};
                newResult.setReq(com.alibaba.fastjson2.JSON.toJSONString(req));

                inspectionResultMapper.insert(newResult);
                log.info("保存新截图记录到数据库，tag: {}", tag.getTag());
            }

        } catch (Exception e) {
            log.error("保存截图到数据库失败，tag: {}, error: {}", tag.getTag(), e.getMessage(), e);
        }
    }

    /**
     * 将图片字节数组转换为Base64字符串
     *
     * @param imageBytes 图片字节数组
     * @return Base64编码的图片字符串
     */
    private String imageToBase64(byte[] imageBytes) {
        String base64String = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64String;
    }

    /**
     * 手动触发截图任务（用于测试）
     */
    public void manualTrigger() {
        log.info("手动触发页面截图任务");
        executeScreenshotTask();
    }
}