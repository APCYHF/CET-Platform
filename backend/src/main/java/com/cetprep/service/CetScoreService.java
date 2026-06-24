package com.cetprep.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.entity.CetScore;
import com.cetprep.entity.User;
import com.cetprep.mapper.CetScoreMapper;
import com.cetprep.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务
 */
@Service
public class CetScoreService {

    @Autowired
    private CetScoreMapper scoreMapper;
    @Autowired
    private UserMapper userMapper;

    public void addScore(CetScore score) {
        score.setUserId(getCurrentUserId());
        score.setIsManual(1);
        scoreMapper.insert(score);
    }

    public List<CetScore> getMyScores() {
        return scoreMapper.selectList(
                new QueryWrapper<CetScore>()
                        .eq("user_id", getCurrentUserId())
                        .orderByDesc("exam_date"));
    }

    public Map<String, Object> getScoreTrend() {
        List<CetScore> list = getMyScores();
        // 简单返回列表供前端折线图使用
        Map<String, Object> result = new HashMap<>();
        result.put("trend", list);
        return result;
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null ? user.getId() : null;
    }
}
