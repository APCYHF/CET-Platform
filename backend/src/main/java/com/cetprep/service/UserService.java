package com.cetprep.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.dto.LoginDTO;
import com.cetprep.dto.RegisterDTO;
import com.cetprep.entity.User;
import com.cetprep.exception.BusinessException;
import com.cetprep.mapper.UserMapper;
import com.cetprep.security.JwtTokenUtil;
import com.cetprep.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Map<String, Object> login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtil.generateToken((org.springframework.security.core.userdetails.User) authentication.getPrincipal());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", dto.getUsername()));
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToVO(user));
        return result;
    }

    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("username", dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("账号已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setRole("USER");
        user.setStatus(1);
        user.setPoints(0);
        user.setStudyDuration(0);
        user.setTargetScore(425);
        userMapper.insert(user);
    }

    public UserVO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return convertToVO(user);
    }

    public void updateUser(UserVO vo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) return;
        user.setNickname(vo.getNickname());
        user.setAvatar(vo.getAvatar());
        user.setSchool(vo.getSchool());
        user.setGrade(vo.getGrade());
        user.setTargetScore(vo.getTargetScore());
        userMapper.updateById(user);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
