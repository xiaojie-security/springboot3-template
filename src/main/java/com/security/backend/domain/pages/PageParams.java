package com.security.backend.domain.pages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageOn;
    private Integer pageSize;
}
