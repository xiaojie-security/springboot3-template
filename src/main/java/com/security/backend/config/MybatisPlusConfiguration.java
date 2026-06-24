package com.security.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类。
 * <p>
 * 负责注册 MyBatis Plus 主拦截器以及分页、乐观锁、防全表更新删除等内部拦截器。
 * </p>
 */
@Configuration
public class MybatisPlusConfiguration {

    /**
     * 创建 MyBatis Plus 主拦截器。
     *
     * @return MyBatis Plus 主拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 创建分页拦截器。
     *
     * @return 分页拦截器
     */
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        PaginationInnerInterceptor pages = new PaginationInnerInterceptor();
        // 指定数据源为 Mysql
        pages.setDbType(DbType.MYSQL);
        // 指定单次分页的最大限制
        pages.setMaxLimit(1000L);
        return pages;
    }

    /**
     * 创建乐观锁拦截器。
     *
     * @return 乐观锁拦截器
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor(){
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 创建防全表更新与删除拦截器。
     *
     * @return 防全表更新与删除拦截器
     */
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor(){
        return new BlockAttackInnerInterceptor();
    }
}
