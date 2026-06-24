package com.security.backend.domain.pages;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScrollPageParams implements Serializable {
    private Long lastTimestamp;    // 本次查询最小时间戳
    private Integer offset; // 最小时间戳出现次数
    private Integer limit; // 总数量
}
