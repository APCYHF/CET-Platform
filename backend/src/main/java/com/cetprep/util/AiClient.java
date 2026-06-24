package com.cetprep.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 大模型调用客户端（预留封装）
 * 实际使用时替换 apiKey 和 url 为对应服务商配置
 */
@Slf4j
@Component
public class AiClient {

    // 示例：智谱 AI / 通义千问 / OpenAI 等，按需替换
    private static final String API_URL = "https://api.example.com/v1/chat/completions";
    private static final String API_KEY = "your-api-key-here";

    /**
     * 提取文本中的核心词汇（用于管理端导入试题时预分析）
     */
    public String extractKeyWords(String text) {
        String prompt = "请从以下英语文本中提取5-10个核心词汇和关键解题词汇，只返回JSON数组格式 [{\"word\":\"单词\",\"type\":\"核心词/解题关键\"}]：\n" + text;
        return callModel(prompt);
    }

    /**
     * 作文/翻译基础批改
     */
    public String correctComposition(String content) {
        String prompt = "你是一位英语四六级阅卷老师，请对以下作文/翻译进行基础批改，给出总分（0-100）、语法拼写问题、连贯性评价和修改建议。请用中文回复。\n" + content;
        return callModel(prompt);
    }

    /**
     * 通用调用方法
     */
    public String callModel(String prompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", new Object[]{
                    Map.of("role", "user", "content", prompt)
            });

            // 实际对接时取消注释以下请求代码
            // String response = HttpUtil.createPost(API_URL)
            //         .header("Authorization", "Bearer " + API_KEY)
            //         .body(JSONUtil.toJsonStr(body))
            //         .timeout(30000)
            //         .execute()
            //         .body();
            // JSONObject json = JSONUtil.parseObj(response);
            // return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getStr("content");

            // 当前返回模拟数据，避免未配置 API Key 时报错
            log.info("AI 调用（模拟）: prompt长度={}", prompt.length());
            return "{\"mock\":true, \"message\":\"AI接口尚未配置，这是模拟返回。请在 AiClient 中配置真实API Key。\"}";
        } catch (Exception e) {
            log.error("AI调用失败", e);
            return "AI调用失败: " + e.getMessage();
        }
    }
}
