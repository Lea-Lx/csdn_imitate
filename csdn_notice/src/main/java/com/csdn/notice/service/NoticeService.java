package com.csdn.notice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csdn.entity.Result;
import com.csdn.notice.client.ArticleClient;
import com.csdn.notice.client.UserClient;
import com.csdn.notice.dao.NoticeDao;
import com.csdn.notice.dao.NoticeFreshDao;
import com.csdn.notice.pojo.Notice;
import com.csdn.notice.pojo.NoticeFresh;
import com.csdn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private IdWorker idWorker;

    private void getNoticeInfo(Notice notice) {
        Result userResult = userClient.findById(notice.getOperatorId());
        HashMap userMap = (HashMap) userResult.getData();
        notice.setOperatorName(userMap.get("nickname").toString());
        if("article".equals(notice.getTargetType())) {
            Result articleResult = articleClient.findByProblemId(notice.getTargetId());
            HashMap articleMap = (HashMap) articleResult.getData();
            notice.setTargetName(articleMap.get("title").toString());
        }
    }

    public Notice selectById(String id) {
        Notice notice = noticeDao.selectById(id);
        getNoticeInfo(notice);
        return notice;
    }

    public Page<Notice> selectByList(Notice notice, int page, int size) {
        Page<Notice> pageData =  new Page<>(page, size);
        EntityWrapper<Notice> entityWrapper = new EntityWrapper<>(notice);
        List<Notice> notices = noticeDao.selectPage(pageData, entityWrapper);

        for(Notice n : notices) {
            getNoticeInfo(n);
        }
        pageData.setRecords(notices);
        return pageData;
    }

    @Transactional
    public void save(Notice notice) {
        notice.setCreateTime(new Date());
        notice.setState("0");
        String id = idWorker.nextId() + "";
        notice.setId(id);
        noticeDao.insert(notice);

        //进入到待推送消息
        //NoticeFresh noticeFresh = new NoticeFresh();
        //noticeFresh.setNoticeId(id);
        //noticeFresh.setUserId(notice.getReceiverId());
        //noticeFreshDao.insert(noticeFresh);
    }

    public void updateById(Notice notice) {
        noticeDao.updateById(notice);
    }

    public Page<NoticeFresh> freshPage(String userid, int page, int size) {
        NoticeFresh noticeFresh = new NoticeFresh();
        noticeFresh.setUserId(userid);
        Page<NoticeFresh> pageData = new Page<>(page, size);
        EntityWrapper<NoticeFresh> entityWrapper = new EntityWrapper<>(noticeFresh);
        List<NoticeFresh> noticeFreshes = noticeFreshDao.selectPage(pageData, entityWrapper);
        pageData.setRecords(noticeFreshes);
        return pageData;
    }

    public void freshDelete(NoticeFresh noticeFresh) {
        noticeFreshDao.delete(new EntityWrapper<>(noticeFresh));
    }
}
