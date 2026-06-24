package com.security.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.security.backend.domain.OperationLog;
import com.security.backend.service.OperationLogService;
import com.security.backend.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 17607
* @description 针对表【operation_log(操作日志表)】的数据库操作Service实现
* @createDate 2026-06-24 11:33:52
*/
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
    implements OperationLogService{

}




