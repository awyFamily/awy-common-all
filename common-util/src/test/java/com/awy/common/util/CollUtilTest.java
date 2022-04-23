package com.awy.common.util;

import com.awy.common.util.utils.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CollUtilTest {

    @Test
    public void dbSorts() {
        List<Student> sources = new ArrayList<>();
        sources.add(new Student(1,18,"aa"));
        sources.add(new Student(2,1,"ab"));
        sources.add(new Student(3,10,"ac"));
        sources.add(new Student(4,18,"ad"));
        sources.add(new Student(5,1,"ae"));
        sources.add(new Student(6,2,"af"));
        sources.add(new Student(7,18,"ag"));
        sources.add(new Student(8,1,"bg"));
        sources.add(new Student(9,2,"cg"));
        sources.add(new Student(10,1,"eg"));
        sources.add(new Student(11,2,"fg"));

        CollUtil.sorts(sources,false,Student::getAge,Student::getId);
        for (Student source : sources) {
            System.out.println(source.toString());
        }
    }


    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Student {

        private Integer id;

        private Integer age;

        private String name;

    }
}
