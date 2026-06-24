# 项目说明

## 项目作用

这是一个基于 Spring Boot 3 的后端模板，核心目标不是提供单一业务功能，而是提供一套可复用的基础能力骨架，方便后续业务系统直接在此基础上扩展。

当前模板已经内置了这些能力：

- Undertow 服务容器
- Spring Security 无状态认证
- JWT 登录态与权限链路
- 请求解密、响应加密
- 请求体重复读取支持
- 防重放校验
- 系统配置动态开关
- Redis / 本地缓存抽象
- 操作日志
- 统一响应与全局异常处理
- P6Spy SQL 日志与慢 SQL 监控

## 核心机制

### Undertow

作用：

- 作为当前项目的嵌入式 Web 容器
- 负责承载 HTTP 请求并接入 Spring MVC / Spring Security

解决的问题：

- 替换默认 Tomcat，统一当前项目的运行容器
- 支撑当前接口服务的基础网络请求处理

### RequestCachingFilter

作用：

- 在请求进入后续 Spring Security 和 Spring MVC 之前，先缓存原始请求体
- 把原始 `HttpServletRequest` 包装成可重复读取的 request wrapper

解决的问题：

- 解决请求体默认只能读取一次的问题
- 解决后续 Filter、Advice、AOP、工具类再次读取请求体时直接变空流的问题

注意：

- 它缓存的是原始请求体
- 它不会把请求体自动替换成解密后的明文

### ResolveTokenFilter

作用：

- 从请求头中提取 token
- 去除前缀并标准化后写入 request attribute

解决的问题：

- 统一 token 读取方式
- 避免后续过滤器重复解析 header 细节

### TokenAuthenticationFilter

作用：

- 校验 JWT
- 加载用户权限
- 建立 Spring Security 的认证上下文

解决的问题：

- 解决接口身份校验问题
- 解决基于权限的访问控制问题
- 解决每次请求都要重新解析用户身份的问题

### Spring MVC 拦截器链

当前项目内置了以下拦截器：

- `SystemMaintenanceInterceptor`
- `TimestampInterceptor`
- `NonceInterceptor`
- `ContextClearInterceptor`

#### SystemMaintenanceInterceptor

作用：

- 根据系统配置决定是否开启维护模式

解决的问题：

- 让系统可以通过配置快速进入维护态
- 维护期间统一阻断对外访问

#### TimestampInterceptor

作用：

- 校验请求时间戳是否合法

解决的问题：

- 限制过期请求进入业务逻辑
- 降低接口被重放利用的风险

#### NonceInterceptor

作用：

- 校验请求随机串是否重复使用

解决的问题：

- 防止相同请求被重复提交
- 防止简单重放攻击

#### ContextClearInterceptor

作用：

- 在请求结束后统一清理上下文

解决的问题：

- 防止线程复用导致 ThreadLocal 数据污染
- 防止用户上下文、加密上下文串请求

### EncryptRequestBodyAdvice

作用：

- 在 Controller 参数绑定前对请求体做解密

解决的问题：

- 解决敏感请求数据明文传输问题
- 让 Controller 直接拿到解密后的业务对象，而不是手工解密原始报文

### EncryptResponseBodyAdvice

作用：

- 在响应写出前对响应体做加密

解决的问题：

- 解决敏感响应数据明文返回问题
- 让客户端和服务端形成成对的加解密链路

### ResultResponseBodyAdvice

作用：

- 统一包装接口返回结构

解决的问题：

- 解决不同接口返回格式不一致的问题
- 让前端或调用方统一按固定结构处理响应

### OperationLogAspect

作用：

- 拦截标记了 `@OperationLog` 的方法
- 记录请求信息、用户信息、执行结果

解决的问题：

- 解决关键操作缺少审计记录的问题
- 解决接口操作过程不可追踪的问题

注意：

- 当前更适合记录方法参数和业务返回值
- 如果再次读取 `HttpServletRequest`，读到的是原始缓存请求体，不是解密后的 DTO 明文

### ContextHolder / UserContext / EncryptContext

作用：

- 统一保存当前请求的用户上下文和加密上下文

解决的问题：

- 解决各层之间传递用户信息不方便的问题
- 解决请求解密和响应加密之间需要共享 AES 密钥的问题

### ContextCopyingDecorator

作用：

- 把主线程上下文透传到异步线程

解决的问题：

- 解决异步线程拿不到用户信息的问题
- 解决异步线程拿不到加密上下文的问题
- 解决异步任务中上下文丢失的问题

### SysConfigHandler

作用：

- 统一读取系统配置并提供开关能力

解决的问题：

- 解决配置读取逻辑散落在各处的问题
- 解决运行期功能开关无法统一管理的问题

当前主要承载的开关包括：

- 数据传输加密
- 系统维护
- 防重放
- token 过期时间
- nonce 过期时间
- 操作日志开关

### ConfigCacheLoader

作用：

- 应用启动后预热系统配置缓存

解决的问题：

- 解决首次读取配置时必须查库的问题
- 降低系统启动后的首批请求配置读取开销

### CacheService

作用：

- 抽象统一缓存访问接口

解决的问题：

- 解决业务层直接依赖具体缓存实现的问题
- 让本地缓存和 Redis 缓存可以按配置切换

### P6Spy

作用：

- 代理 JDBC 调用
- 打印 SQL
- 监控慢 SQL

解决的问题：

- 解决 SQL 执行不可观察的问题
- 解决慢 SQL 不容易被发现的问题

当前项目的接入方式：

- 使用 `P6SpyDriver`
- 使用 `jdbc:p6spy:mysql://...`
- 使用 `spy.properties` 控制日志输出和慢 SQL 检测

### SqlLoggerConfiguration

作用：

- 自定义 P6Spy 的 SQL 日志格式和输出方式

解决的问题：

- 解决默认 SQL 日志可读性差的问题
- 统一当前项目的 SQL 日志输出格式

### 慢 SQL 机制

作用：

- 通过 P6Spy 的 `P6OutageFactory` 监控 SQL 执行耗时

解决的问题：

- 自动识别执行时间超过阈值的 SQL
- 方便开发和排查数据库性能问题

当前阈值：

- `outagedetectioninterval=2`
- 超过 2 秒的 SQL 会被认定为慢 SQL

## 当前项目解决的典型问题

这个模板主要解决的是下面这些通用后端问题：

- 登录态和权限链路重复建设
- 请求体被多次读取后变空
- 请求和响应敏感数据明文传输
- 防重放和重复提交缺少统一方案
- ThreadLocal 上下文在异步线程中丢失
- 系统配置开关散落、不可统一管理
- SQL 日志不可观测
- 慢 SQL 难以及时发现
- 缺少统一响应格式
- 缺少统一异常处理
- 缺少关键操作审计日志

## 适合继续扩展的方向

在当前模板基础上，后续业务开发通常直接扩展这些位置：

- `controller`：增加业务接口
- `service`：增加业务逻辑
- `mapper`：增加数据库访问
- `annotation` + `aspect`：扩展统一横切能力
- `interceptor` / `filter`：扩展链路级安全能力
- `handler`：扩展系统配置、认证、缓存、工具能力
