package com.cetprep.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.entity.DailyStudyPlan;
import com.cetprep.entity.User;
import com.cetprep.entity.UserVocabulary;
import com.cetprep.entity.Word;
import com.cetprep.mapper.DailyStudyPlanMapper;
import com.cetprep.mapper.UserMapper;
import com.cetprep.mapper.UserVocabularyMapper;
import com.cetprep.mapper.WordMapper;
import com.cetprep.vo.WordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 单词与背单词服务
 */
@Service
public class WordService {

    @Autowired
    private WordMapper wordMapper;
    @Autowired
    private UserVocabularyMapper vocabularyMapper;
    @Autowired
    private DailyStudyPlanMapper planMapper;
    @Autowired
    private UserMapper userMapper;

    // 艾宾浩斯复习间隔（天）
    private static final int[] REVIEW_INTERVALS = {1, 2, 4, 7, 15};

    public List<WordVO> getWordList(String level, int page, int size) {
        QueryWrapper<Word> qw = new QueryWrapper<>();
        if (level != null) qw.eq("level", level);
        qw.last("LIMIT " + (page - 1) * size + "," + size);
        return wordMapper.selectList(qw).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 获取今日学习任务（新词+复习）
     */
    public Map<String, Object> getTodayStudyPlan() {
        Long userId = getCurrentUserId();
        LocalDate today = LocalDate.now();

        // 检查今日计划
        DailyStudyPlan plan = planMapper.selectOne(
                new QueryWrapper<DailyStudyPlan>()
                        .eq("user_id", userId)
                        .eq("plan_date", today));
        if (plan == null) {
            plan = createDailyPlan(userId, today);
        }

        // 获取待复习单词
        List<UserVocabulary> reviewList = vocabularyMapper.selectList(
                new QueryWrapper<UserVocabulary>()
                        .eq("user_id", userId)
                        .le("next_review_date", today)
                        .orderByAsc("next_review_date"));

        // 获取新词（不在生词本中的）
        List<Long> existingWordIds = vocabularyMapper.selectList(
                        new QueryWrapper<UserVocabulary>().eq("user_id", userId))
                .stream().map(UserVocabulary::getWordId).collect(Collectors.toList());

        List<Word> newWords;
        if (existingWordIds.isEmpty()) {
            newWords = wordMapper.selectList(new QueryWrapper<Word>().last("LIMIT " + plan.getNewWords()));
        } else {
            newWords = wordMapper.selectList(
                    new QueryWrapper<Word>()
                            .notIn("id", existingWordIds)
                            .last("LIMIT " + plan.getNewWords()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        result.put("reviewWords", reviewList.stream().map(this::mapToWordVO).collect(Collectors.toList()));
        result.put("newWords", newWords.stream().map(this::convertToVO).collect(Collectors.toList()));
        return result;
    }

    /**
     * 更新单词学习状态
     */
    @Transactional
    public void updateWordStatus(Long wordId, String status) {
        Long userId = getCurrentUserId();
        UserVocabulary uv = vocabularyMapper.selectOne(
                new QueryWrapper<UserVocabulary>()
                        .eq("user_id", userId)
                        .eq("word_id", wordId));

        LocalDate nextReview = null;
        int proficiency = 0;

        if ("MASTERED".equals(status)) {
            proficiency = 2;
            nextReview = LocalDate.now().plusDays(30);
        } else if ("REVIEW".equals(status)) {
            proficiency = 1;
            nextReview = LocalDate.now().plusDays(1);
        } else {
            proficiency = 0;
            nextReview = LocalDate.now().plusDays(1);
        }

        if (uv == null) {
            uv = new UserVocabulary();
            uv.setUserId(userId);
            uv.setWordId(wordId);
            uv.setStatus(status);
            uv.setProficiency(proficiency);
            uv.setNextReviewDate(nextReview);
            uv.setAddSource("MANUAL");
            vocabularyMapper.insert(uv);
        } else {
            uv.setStatus(status);
            uv.setProficiency(proficiency);
            uv.setNextReviewDate(nextReview);
            vocabularyMapper.updateById(uv);
        }
    }

    /**
     * 一键将建议词汇加入生词本
     */
    @Transactional
    public void addWordsToVocabulary(List<Long> wordIds) {
        Long userId = getCurrentUserId();
        for (Long wordId : wordIds) {
            Long count = vocabularyMapper.selectCount(
                    new QueryWrapper<UserVocabulary>()
                            .eq("user_id", userId)
                            .eq("word_id", wordId));
            if (count > 0) continue;
            UserVocabulary uv = new UserVocabulary();
            uv.setUserId(userId);
            uv.setWordId(wordId);
            uv.setStatus("NEW");
            uv.setProficiency(0);
            uv.setNextReviewDate(LocalDate.now().plusDays(1));
            uv.setAddSource("RECOMMEND");
            vocabularyMapper.insert(uv);
        }
    }

    /**
     * 获取用户生词本
     */
    public List<Map<String, Object>> getMyVocabulary() {
        Long userId = getCurrentUserId();
        List<UserVocabulary> list = vocabularyMapper.selectList(
                new QueryWrapper<UserVocabulary>()
                        .eq("user_id", userId)
                        .orderByDesc("created_at"));
        return list.stream().map(uv -> {
            Map<String, Object> map = new HashMap<>();
            Word word = wordMapper.selectById(uv.getWordId());
            map.put("vocabularyId", uv.getId());
            map.put("status", uv.getStatus());
            map.put("proficiency", uv.getProficiency());
            map.put("nextReviewDate", uv.getNextReviewDate());
            if (word != null) map.put("word", convertToVO(word));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 获取学习统计
     */
    public Map<String, Object> getStudyStats() {
        Long userId = getCurrentUserId();
        long totalWords = vocabularyMapper.selectCount(
                new QueryWrapper<UserVocabulary>().eq("user_id", userId));
        long mastered = vocabularyMapper.selectCount(
                new QueryWrapper<UserVocabulary>().eq("user_id", userId).eq("status", "MASTERED"));
        long todayReview = vocabularyMapper.selectCount(
                new QueryWrapper<UserVocabulary>()
                        .eq("user_id", userId)
                        .le("next_review_date", LocalDate.now()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWords", totalWords);
        stats.put("mastered", mastered);
        stats.put("masteryRate", totalWords > 0 ? (int) ((mastered * 100.0) / totalWords) : 0);
        stats.put("todayReview", todayReview);
        return stats;
    }

    private DailyStudyPlan createDailyPlan(Long userId, LocalDate date) {
        DailyStudyPlan plan = new DailyStudyPlan();
        plan.setUserId(userId);
        plan.setPlanDate(date);
        plan.setNewWords(20);
        plan.setReviewWords(0);
        plan.setCompleted(0);
        planMapper.insert(plan);
        return plan;
    }

    private Map<String, Object> mapToWordVO(UserVocabulary uv) {
        Map<String, Object> map = new HashMap<>();
        Word word = wordMapper.selectById(uv.getWordId());
        if (word != null) {
            map.put("word", convertToVO(word));
        }
        map.put("vocabularyId", uv.getId());
        map.put("status", uv.getStatus());
        map.put("proficiency", uv.getProficiency());
        return map;
    }

    private WordVO convertToVO(Word word) {
        WordVO vo = new WordVO();
        BeanUtils.copyProperties(word, vo);
        return vo;
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null ? user.getId() : null;
    }
}
