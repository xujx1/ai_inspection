package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class IndexController {
    
    /**
     * 根路径重定向到index.html
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
    
    /**
     * 空路径重定向到index.html
     */
    @GetMapping("")
    public String emptyPath() {
        return "redirect:/index.html";
    }
}