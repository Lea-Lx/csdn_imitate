package com.csdn.notice.pojo;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_notice_fresh")
public class NoticeFresh implements Serializable {

    private String userId;
    private String noticeId;
}
