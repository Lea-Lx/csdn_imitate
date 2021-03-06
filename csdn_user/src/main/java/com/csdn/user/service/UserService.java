package com.csdn.user.service;

import com.csdn.user.dao.UserDao;
import com.csdn.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User login(User user) {
        return userDao.selectOne(user);
    }

    public User selectById(String id) {
        return userDao.selectById(id);
    }
}
