package com.awy.common.redis.data;


import java.util.List;

/**
 * @author yhw
 */
public class ScanData {

    /**
     * 分页起始值,第一次为0, 下一次 scan 时 : cursor 值为 上次 scan 返回结果中第一个整数值 即 list.get(0)
    */
    private String cursor;

    /**
     * scan 到的 key 列表
     */
    private List<String> keys;


    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public ScanData(){}

    public ScanData(String cursor, List<String> keys) {
        this.cursor = cursor;
        this.keys = keys;
    }
}
