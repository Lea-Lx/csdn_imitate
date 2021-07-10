package com.csdn.article.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.csdn.article.pojo.Article;
import com.csdn.article.service.ArticleService;
import com.csdn.entity.PageResult;
import com.csdn.entity.Result;
import com.csdn.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Article> list = articleService.findAll();
        Result result = new Result(true, StatusCode.OK, "查询成功", list);
        return result;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        Article article = articleService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", article);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Article article) {
        articleService.add(article);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable String id,
                         @RequestBody Article article) {
        article.setId(id);
        articleService.update(article);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        articleService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result search(@RequestBody Map map, @PathVariable int page, @PathVariable int size) {
        Page page1 = articleService.search(map, page, size);
        PageResult<Article> pageResult = new PageResult<Article>(page1.getTotal(), page1.getRecords());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public Result exception() throws Exception {
        throw new Exception("测试统一异常处理");
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public Result subscribe(@RequestBody Map map) {
        Boolean flag = articleService.subscribe(map.get("userId").toString(), map.get("articleId").toString());

        if(flag) {
            return new Result(true, StatusCode.OK, "订阅成功");
        } else {
            return new Result(true, StatusCode.OK, "订阅取消");
        }
    }

    @RequestMapping(value = "/thumbup/{articleId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String articleId) {
        //TODO 拿到userId
        String userId = "4";
        Boolean flag = articleService.thumbup(articleId, userId);
        if(flag) {
            return new Result(true, StatusCode.OK, "点赞成功");
        } else {
            return new Result(false, StatusCode.REPEERROR, "不能重复点赞");
        }
    }

}
