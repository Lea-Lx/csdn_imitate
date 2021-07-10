package com.csdn.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.csdn.article.client.NoticeClient;
import com.csdn.article.dao.ArticleDao;
import com.csdn.article.pojo.Article;
import com.csdn.article.pojo.Notice;
import com.csdn.utils.IdWorker;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private NoticeClient noticeClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<Article> findAll() {
        return articleDao.selectList(null);
    }

    public Article findById(String id) {
        return articleDao.selectById(id);
    }

    public void add(Article article) {
        String id = idWorker.nextId()+"";
        article.setId(id);

        article.setCreatetime(new Date());
        article.setUpdatetime(new Date());
        article.setVisits(0);
        article.setThumbup(0);
        article.setComment(0);

        articleDao.insert(article);

        //TODO 使用jwt获取当前用户的userid，也就是文章作者的id
        String authorId = "3";

        //获取需要通知的读者
        String authorKey = "article_author_" + authorId;
        Set<String> set = redisTemplate.boundSetOps(authorKey).members();

        for(String uid : set) {
            Notice notice = new Notice();
            notice.setReceiverId(uid);
            notice.setOperatorId(authorId);
            notice.setAction("publish");
            notice.setTargetType("article");
            notice.setTargetId(id);
            notice.setCreateTime(new Date());
            notice.setType("sys");
            notice.setState("0");

            noticeClient.add(notice);

            rabbitTemplate.convertAndSend("article_subscribe", authorId, id);
        }
    }

    public void update(Article article) {
        articleDao.updateById(article);
    }

    public void delete(String id) {
        articleDao.deleteById(id);
    }

    public Page search(Map map, int page, int size) {
        EntityWrapper<Article> wrapper = new EntityWrapper();
        Set<String> set = map.keySet();
        for(String s : set) {
            wrapper.eq(map.get(s) != null, s, map.get(s));
        }
        Page page1 = new Page(page, size);
        List list = articleDao.selectPage(page1, wrapper);
        page1.setRecords(list);
        return page1;
    }

    public Boolean subscribe(String userId, String articleId) {
        String authorId = articleDao.selectById(articleId).getUserid();

        //创建Rabbit管理器
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

        //声明exchange
        DirectExchange exchange = new DirectExchange("article_subscribe");
        rabbitAdmin.declareExchange(exchange);

        //创建queue
        Queue queue = new Queue("article_subscribe_" + userId, true);

        //声明exchange和queue的绑定关系，设置路由键为作者id
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(authorId);

        //存放订阅用户以及订阅作者
        String userKey = "article_subscribe_" + userId;
        String authorKey = "article_author_" + authorId;

        Boolean flag = redisTemplate.boundSetOps(userKey).isMember(authorId);

        if(flag) {
            redisTemplate.boundSetOps(userKey).remove(authorId);
            redisTemplate.boundSetOps(authorKey).remove(userId);

            //删除绑定队列
            rabbitAdmin.removeBinding(binding);

            return false;
        } else {
            redisTemplate.boundSetOps(userKey).add(authorId);
            redisTemplate.boundSetOps(authorKey).add(userId);

            //声明队列和绑定队列
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(binding);

            return true;
        }
    }

    public Boolean thumbup(String articleId, String userId) {
        String key = "thumbup_article_" + userId + "_" + articleId;

        Object flag = redisTemplate.opsForValue().get(key);

        if(flag == null) {
            Article article = articleDao.selectById(articleId);
            article.setThumbup(article.getThumbup() + 1);
            articleDao.updateById(article);

            redisTemplate.opsForValue().set(key, 1);

            Notice notice = new Notice();
            notice.setReceiverId(article.getUserid());
            notice.setOperatorId(userId);
            notice.setAction("thumbup");
            notice.setType("user");
            notice.setTargetType("article");
            notice.setTargetId(articleId);
            notice.setCreateTime(new Date());
            notice.setState("0");

            noticeClient.add(notice);

            //1.创建Rabbit管理器
            RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

            //2.创建队列， 每个用户都有自己的队列
            Queue queue = new Queue("article_thumbup_" + article.getUserid(), true);
            rabbitAdmin.declareQueue(queue);

            //3.发送消息
            rabbitTemplate.convertAndSend("article_thumbup_"+article.getUserid(), articleId);

            return true;
        } else {
            return false;
        }
    }
}
