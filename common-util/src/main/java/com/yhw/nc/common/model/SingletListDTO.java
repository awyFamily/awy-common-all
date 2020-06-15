package com.yhw.nc.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class SingletListDTO<T> implements Serializable {

    private List<T> ids;
}
