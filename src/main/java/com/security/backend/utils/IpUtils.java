package com.security.backend.utils;

import cn.hutool.core.net.Ipv4Util;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * IP地址工具类 - 支持IPv4和IPv6
 */
@Slf4j
public class IpUtils extends Ipv4Util {
    
    // IPv4 正则
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    // IPv6 正则（简化版）
    private static final Pattern IPV6_PATTERN = Pattern.compile(
        "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|" +
        "^::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|" +
        "^([0-9a-fA-F]{1,4}:){1,7}:$"
    );
    
    /**
     * 获取客户端真实IP地址（优先IPv4，其次IPv6）
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        String ip = getIpFromHeaders(request);
        
        // 如果从headers获取的IP是IPv6格式，保留
        if (ip != null && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        
        // 从RemoteAddr获取
        ip = request.getRemoteAddr();
        return ip != null ? ip.trim() : null;
    }
    
    /**
     * 获取客户端IPv4地址
     */
    public static String getClientIpv4(HttpServletRequest request) {
        String ip = getClientIp(request);
        if (isIPv4(ip)) {
            return ip;
        }
        return null;
    }
    
    /**
     * 获取客户端IPv6地址
     */
    public static String getClientIpv6(HttpServletRequest request) {
        String ip = getClientIp(request);
        if (isIPv6(ip)) {
            return ip;
        }
        return null;
    }
    
    /**
     * 从请求头中获取IP
     */
    private static String getIpFromHeaders(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 如果X-Forwarded-For有多个IP，取第一个真实IP
                if (ip.contains(",")) {
                    String[] ips = ip.split(",");
                    for (String singleIp : ips) {
                        String trimmedIp = singleIp.trim();
                        if (!trimmedIp.isEmpty() && !"unknown".equalsIgnoreCase(trimmedIp)) {
                            return trimmedIp;
                        }
                    }
                }
                return ip;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为IPv4地址
     */
    public static boolean isIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }
    
    /**
     * 判断是否为IPv6地址
     */
    public static boolean isIPv6(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        // 去除IPv6的zone index（如 fe80::1%eth0）
        if (ip.contains("%")) {
            ip = ip.substring(0, ip.indexOf("%"));
        }
        return IPV6_PATTERN.matcher(ip).matches();
    }
    
    /**
     * 判断是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.isSiteLocalAddress() || 
                   inetAddress.isLoopbackAddress() ||
                   inetAddress.isLinkLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    /**
     * 获取服务器本地IP地址列表（包括IPv4和IPv6）
     */
    public static String getLocalIps() {
        StringBuilder ips = new StringBuilder();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        String hostAddress = addr.getHostAddress();
                        if (!ips.isEmpty()) {
                            ips.append(", ");
                        }
                        ips.append(hostAddress);
                        if (addr instanceof Inet6Address) {
                            ips.append("[IPv6]");
                        } else {
                            ips.append("[IPv4]");
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error("IpUtils.getLocalIps 获取本地IP失败", e);
        }
        return ips.toString();
    }
    
    /**
     * 获取服务器IPv4地址
     */
    public static String getLocalIpv4() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("IpUtils.getLocalIpv4 获取IPv4地址失败", e);
        }
        return LOCAL_IP;
    }
    
    /**
     * 获取服务器IPv6地址
     */
    public static String getLocalIpv6() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof Inet6Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("IpUtils.getLocalIp v6获取IPv6地址失败", e);
        }
        return null;
    }
    
    /**
     * IP地址转换（IPv6转IPv4映射地址处理）
     */
    public static String normalizeIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }
        
        // 处理IPv6的IPv4映射地址（如 ::ffff:192.168.1.1）
        if (ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        
        return ip;
    }
}
