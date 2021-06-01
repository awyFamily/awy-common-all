package com.awy.common.mongo.model;

/**
 * @author yhw
 * @since 2021-05-07
 */
public class SimpleGroupStatisticalModel {

    /**
     * statistic field
     */
    private String _id;

    /**
     * statistic value
     */
    private Object value;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
