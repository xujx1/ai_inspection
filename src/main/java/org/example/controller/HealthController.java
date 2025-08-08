package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "Application is running!";
    }

    @GetMapping("/welcome")
    @ResponseBody
    public String home() {
        return "<html><body><h1>Welcome to AI Inspection Application!</h1>" +
               "<p><a href='/prompt.html'>Prompt 管理页面</a></p>" +
               "<p><a href='/inspection.html'>巡检结果页面</a></p>" +
               "<p><a href='/tag.html'>标签管理页面</a></p>" +
               "</body></html>";
    }

    @GetMapping("/prompt")
    public String promptPage() {
        return "prompt";
    }

    @GetMapping("/inspection")
    public String inspectionPage() {
        return "inspection";
    }
}