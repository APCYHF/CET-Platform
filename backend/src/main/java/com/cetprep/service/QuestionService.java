package com.cetprep.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.dto.AnswerSubmitDTO;
import com.cetprep.entity.*;
import com.cetprep.mapper.*;
import com.cetprep.vo.QuestionVO;
import com.cetprep.vo.WordVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 题目与刷题服务
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamPaperMapper examPaperMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private UserAnswerRecordMapper answerRecordMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserVocabularyMapper vocabularyMapper;
    @Autowired
    private WordMapper wordMapper;
    @Autowired
    private WordSuggestionMapper wordSuggestionMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<QuestionVO> getReadingQuestions() {
        List<Question> list = questionMapper.selectList(
                new QueryWrapper<Question>().eq("type", "READING"));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public List<QuestionVO> getListeningQuestions() {
        List<Question> list = questionMapper.selectList(
                new QueryWrapper<Question>().eq("type", "LISTENING"));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public List<ExamPaper> getPapers(String level) {
        QueryWrapper<ExamPaper> qw = new QueryWrapper<>();
        if (level != null) qw.eq("level", level);
        qw.eq("status", 1).orderByDesc("year", "month");
        return examPaperMapper.selectList(qw);
    }

    public Map<String, Object> getPaperDetail(Long paperId) {
        ExamPaper paper = examPaperMapper.selectById(paperId);
        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new QueryWrapper<PaperQuestion>().eq("paper_id", paperId).orderByAsc("sort_no"));

        List<QuestionVO> listening = new ArrayList<>();
        List<QuestionVO> reading = new ArrayList<>();
        List<QuestionVO> writing = new ArrayList<>();
        List<QuestionVO> translation = new ArrayList<>();

        for (PaperQuestion pq : pqs) {
            Question q = questionMapper.selectById(pq.getQuestionId());
            if (q == null) continue;
            QuestionVO vo = convertToVO(q);
            switch (q.getType()) {
                case "LISTENING": listening.add(vo); break;
                case "READING": reading.add(vo); break;
                case "WRITING": writing.add(vo); break;
                case "TRANSLATION": translation.add(vo); break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paper", paper);
        result.put("listening", listening);
        result.put("reading", reading);
        result.put("writing", writing);
        result.put("translation", translation);
        return result;
    }

    @Transactional
    public Map<String, Object> submitAnswers(AnswerSubmitDTO dto) {
        Long userId = getCurrentUserId();
        int correctCount = 0;
        int totalScore = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (AnswerSubmitDTO.AnswerItem item : dto.getAnswers()) {
            Question q = questionMapper.selectById(item.getQuestionId());
            if (q == null) continue;
            boolean isCorrect = q.getAnswer() != null && q.getAnswer().equalsIgnoreCase(item.getAnswer());
            if (isCorrect) {
                correctCount++;
                totalScore += q.getScore() != null ? q.getScore() : 0;
            }

            UserAnswerRecord record = new UserAnswerRecord();
            record.setUserId(userId);
            record.setPaperId(dto.getPaperId());
            record.setQuestionId(item.getQuestionId());
            record.setUserAnswer(item.getAnswer());
            record.setIsCorrect(isCorrect ? 1 : 0);
            record.setSpendTime(item.getSpendTime());
            answerRecordMapper.insert(record);

            Map<String, Object> detail = new HashMap<>();
            detail.put("questionId", q.getId());
            detail.put("yourAnswer", item.getAnswer());
            detail.put("correctAnswer", q.getAnswer());
            detail.put("isCorrect", isCorrect);
            detail.put("analysis", q.getAnalysis());
            details.add(detail);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("correctCount", correctCount);
        result.put("totalScore", totalScore);
        result.put("details", details);
        return result;
    }

    public List<Map<String, Object>> getWrongQuestions() {
        Long userId = getCurrentUserId();
        List<UserAnswerRecord> records = answerRecordMapper.selectList(
                new QueryWrapper<UserAnswerRecord>()
                        .eq("user_id", userId)
                        .eq("is_correct", 0)
                        .orderByDesc("answer_date"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserAnswerRecord r : records) {
            Question q = questionMapper.selectById(r.getQuestionId());
            if (q == null) continue;
            Map<String, Object> map = new HashMap<>();
            map.put("recordId", r.getId());
            map.put("question", convertToVO(q));
            map.put("yourAnswer", r.getUserAnswer());
            map.put("answerDate", r.getAnswerDate());
            result.add(map);
        }
        return result;
    }

    /**
     * 获取某题的建议词汇
     */
    public List<WordVO> getQuestionWords(Long questionId) {
        List<WordSuggestion> suggestions = wordSuggestionMapper.selectList(
                new QueryWrapper<WordSuggestion>().eq("question_id", questionId));
        List<WordVO> result = new ArrayList<>();
        for (WordSuggestion ws : suggestions) {
            Word word = wordMapper.selectById(ws.getWordId());
            if (word != null) result.add(convertToWordVO(word));
        }
        return result;
    }

    private QuestionVO convertToVO(Question q) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(q, vo);
        if (q.getOptions() != null) {
            try {
                vo.setOptions(objectMapper.readValue(q.getOptions(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                vo.setOptions(new ArrayList<>());
            }
        }
        return vo;
    }

    private WordVO convertToWordVO(Word word) {
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
