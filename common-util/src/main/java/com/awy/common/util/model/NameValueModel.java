package com.awy.common.util.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NameValueModel<T> {

    private String name;

    private T value;

    public NameValueModel(String name, T value) {
        this.name = name;
        this.value = value;
    }
}
