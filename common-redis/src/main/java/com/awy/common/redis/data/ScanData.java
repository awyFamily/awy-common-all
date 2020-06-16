package com.awy.common.redis.data;


import java.util.List;

/**
 * @author yhw
 */
public class ScanData {

    /**
     *
     */
    private String cursor;

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
