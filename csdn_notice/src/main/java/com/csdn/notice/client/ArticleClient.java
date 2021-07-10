package com.csdn.notice.client;

import com.csdn.entity.Result;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableFeignClients(value = "csdn-article")
public interface ArticleClient {

    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public Result findByProblemId(@PathVariable("articleId") String articleId);
}
