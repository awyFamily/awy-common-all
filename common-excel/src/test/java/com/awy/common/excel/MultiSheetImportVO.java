package com.awy.common.excel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MultiSheetImportVO {

    private String sn;

    private List<Detail>  details;

    public List<Detail> getDetailsSort() {
        if(details == null || details.isEmpty()){
            return new ArrayList<>();
        }
        return details.stream().sorted(Comparator.comparing(Detail::getData)).collect(Collectors.toList());
    }


    @NoArgsConstructor
    @Data
    public static class Detail{
        private String data;
        private Integer number;


        public Detail(String data,Integer number){
            this.data = data;
            this.number = number;
        }

    }

}
