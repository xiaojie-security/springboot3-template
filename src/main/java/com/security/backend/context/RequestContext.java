package com.security.backend.context;

import com.security.backend.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RequestContext {
    private static final ThreadLocal<RequestInfo> request = new ThreadLocal<>();

    public RequestContext() {
    }

    public void clear() {
        request.remove();
    }

    private RequestInfo get() {
        return request.get();
    }

    private void set(RequestInfo requestInfo) {
        request.set(requestInfo);
    }

    public String getDeviceId() {
        RequestInfo requestInfo = get();
        return requestInfo != null ? requestInfo.getDeviceId() : null;
    }

    public DeviceType getDeviceType() {
        RequestInfo requestInfo = get();
        return requestInfo != null ? requestInfo.getDeviceType() : null;
    }

    public String getIpv4() {
        RequestInfo requestInfo = get();
        return requestInfo != null ? requestInfo.getIpv4() : null;
    }

    public String getIpv6() {
        RequestInfo requestInfo = get();
        return requestInfo != null ? requestInfo.getIpv6() : null;
    }

    public String getUserAgent() {
        RequestInfo requestInfo = get();
        return requestInfo != null ? requestInfo.getUserAgent() : null;
    }

    // 便捷方法：直接设置各个字段
    public void setDeviceId(String deviceId) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setDeviceId(deviceId);
        set(requestInfo);
    }

    public void setDeviceType(DeviceType deviceType) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setDeviceType(deviceType);
        set(requestInfo);
    }

    public void setIpv4(String ipv4) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setIpv4(ipv4);
        set(requestInfo);
    }

    public void setIpv6(String ipv6) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setIpv6(ipv6);
        set(requestInfo);
    }

    public void setUserAgent(String userAgent) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setUserAgent(userAgent);
        set(requestInfo);
    }

    public void setRequestInfo(String deviceId,DeviceType deviceType,String ipv4, String ipv6,String userAgent) {
        RequestInfo requestInfo = get();
        if (requestInfo == null) {
            requestInfo = new RequestInfo();
        }
        requestInfo.setDeviceId(deviceId);
        requestInfo.setDeviceType(deviceType);
        requestInfo.setIpv4(ipv4);
        requestInfo.setIpv6(ipv6);
        requestInfo.setUserAgent(userAgent);
        set(requestInfo);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class RequestInfo {
        private String deviceId;
        private DeviceType deviceType;
        private String ipv4;
        private String ipv6;
        private String userAgent;
    }
}
