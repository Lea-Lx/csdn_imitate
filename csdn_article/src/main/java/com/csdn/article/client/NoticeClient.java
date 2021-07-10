package com.csdn.article.client;

import com.csdn.article.pojo.Notice;
import com.csdn.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "csdn-notice")
public interface NoticeClient {

    @RequestMapping(value = "/notice", method = RequestMethod.POST)
    public Result add(@RequestBody Notice notice);
}
