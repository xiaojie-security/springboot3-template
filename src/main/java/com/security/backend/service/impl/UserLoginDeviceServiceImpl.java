package com.security.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.security.backend.domain.UserLoginDevice;
import com.security.backend.service.UserLoginDeviceService;
import com.security.backend.mapper.UserLoginDeviceMapper;
import org.springframework.stereotype.Service;

/**
* @author 17607
* @description 针对表【user_login_device(用户登录设备表)】的数据库操作Service实现
* @createDate 2026-06-24 16:59:23
*/
@Service
public class UserLoginDeviceServiceImpl extends ServiceImpl<UserLoginDeviceMapper, UserLoginDevice>
    implements UserLoginDeviceService{

}




