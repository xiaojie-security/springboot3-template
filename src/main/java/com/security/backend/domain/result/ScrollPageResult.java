package com.security.backend.domain.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrollPageResult<T> implements Serializable {
    private List<T> records;    // 本次查询结果
    private Long lastTimestamp;    // 本次查询最小时间戳
    private Long offset; // 最小时间戳出现次数
    private Long total; // 总数量

    public static <T> ScrollPageResult<T> defaultResult() {
        return new ScrollPageResult<>(Collections.emptyList(), 0L, 0L, 0L);
    }
}
