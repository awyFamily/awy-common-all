package com.awy.common.util.utils;

import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yhw
 * @date 2022-05-19
 */
public final class IpMatcher {

    private List<String> ips;
    private List<String> ipsComAdap;

    public IpMatcher(List<String> ips) {
        this.ips = ips;
        if (CollUtil.isNotEmpty(ips)) {
            this.ipsComAdap = ips.stream()
                    .filter(str -> {
                        String suffix = str.substring(str.lastIndexOf(".") + 1);
                        return "0".equals(suffix) || "*".equals(suffix);
                    }).map(str -> {
                        return str.substring(0,str.lastIndexOf("."));
                    }).collect(Collectors.toList());
        }
    }

    public boolean match(List<String> ips) {
        if (CollUtil.isEmpty(ips)) {
            return false;
        }
        for (String ip : ips) {
            return this.match(ip);
        }
        return false;

    }

    public boolean match(String ip) {
        if (this.ips.contains(ip)) {
            return true;
        }
        if (CollUtil.isNotEmpty(this.ipsComAdap)) {
            if (this.ipsComAdap.contains(ip.substring(0,ip.lastIndexOf(".")))) {
                return true;
            }
        }
        return false;
    }

}
