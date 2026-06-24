package com.cetprep.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.entity.CompositionTranslation;
import com.cetprep.entity.User;
import com.cetprep.mapper.CompositionTranslationMapper;
import com.cetprep.mapper.UserMapper;
import com.cetprep.util.AiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作文/翻译服务
 */
@Service
public class CompositionService {

    @Autowired
    private CompositionTranslationMapper compositionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AiClient aiClient;

    public void submit(CompositionTranslation ct) {
        ct.setUserId(getCurrentUserId());
        ct.setManualStatus("NONE");
        // 调用AI基础批改（异步或同步，此处简化直接调用）
        String aiResult = aiClient.correctComposition(ct.getContent());
        ct.setAiFeedback(aiResult);
        compositionMapper.insert(ct);
    }

    public List<CompositionTranslation> getMyList() {
        return compositionMapper.selectList(
                new QueryWrapper<CompositionTranslation>()
                        .eq("user_id", getCurrentUserId())
                        .orderByDesc("created_at"));
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null ? user.getId() : null;
    }
}
