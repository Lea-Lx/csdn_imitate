package com.csdn.article.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("tb_notice")
public class Notice implements Serializable {

    @TableId(type = IdType.INPUT)
    private String id;

    private String receiverId;
    private String operatorId;

    @TableField(exist = false)
    private String operatorName;
    private String action;
    private String targetType;

    @TableField(exist = false)
    private String targetName;
    private String targetId;
    private Date createTime;
    private String type;
    private String state;
}
