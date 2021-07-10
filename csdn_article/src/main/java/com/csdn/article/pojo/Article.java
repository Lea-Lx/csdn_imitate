package com.csdn.article.pojo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@TableName("tb_article")
public class Article implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;
    private String columnid;
    private String userid;
    private String title;
    private String content;
    private String image;
    private Date createtime;
    private Date updatetime;
    private String ispublic;
    private String istop;
    private Integer visits;
    private Integer thumbup;
    private Integer comment;
    private String state;
    private String channelid;
    private String url;
    private String type;
}
