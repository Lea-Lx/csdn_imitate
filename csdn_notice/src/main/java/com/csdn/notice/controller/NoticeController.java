package com.csdn.notice.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.csdn.entity.PageResult;
import com.csdn.entity.Result;
import com.csdn.entity.StatusCode;
import com.csdn.notice.pojo.Notice;
import com.csdn.notice.pojo.NoticeFresh;
import com.csdn.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    //1.根据id查询消息通知
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result selectById(@PathVariable String id) {
        Notice notice = noticeService.selectById(id);
        return new Result(true, StatusCode.OK, "查询成功", notice);
    }

    //2.根据条件分页查询消息通知
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result selectByList(@RequestBody Notice notice,
                               @PathVariable int page,
                               @PathVariable int size) {
        Page<Notice> pageData = noticeService.selectByList(notice, page, size);
        PageResult<Notice> pageResult = new PageResult(pageData.getTotal(), pageData.getRecords());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    //3.新增通知
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return new Result(true, StatusCode.OK, "新增成功");
    }
    //4.修改通知
    @RequestMapping(method = RequestMethod.PUT)
    public Result updateById(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    //5.根据用户id查询该用户的待推送消息（新消息）
    @RequestMapping(value = "/fresh/{userid}/{page}/{size}", method = RequestMethod.GET)
    public Result freshPage(@PathVariable String userid,
                            @PathVariable int page,
                            @PathVariable int size) {
        Page<NoticeFresh> pageData = noticeService.freshPage(userid, page, size);
        PageResult<NoticeFresh> pageResult = new PageResult<>(pageData.getTotal(), pageData.getRecords());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    //6.删除待推送消息（新消息）
    @RequestMapping(value = "/fresh", method = RequestMethod.DELETE)
    public Result freshDelete(@RequestBody NoticeFresh noticeFresh) {
        noticeService.freshDelete(noticeFresh);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
