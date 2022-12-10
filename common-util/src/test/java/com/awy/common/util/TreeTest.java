package com.awy.common.util;

import cn.hutool.json.JSONUtil;
import com.awy.common.util.model.MenuTree;
import com.awy.common.util.utils.TreeUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author yhw
 * @date 2022-12-10
 */
public class TreeTest {

    @Test
    public void menuTreeTest() {
        List<MenuTree> list = new ArrayList<>();
        Random random = new Random();
        for(long i = 1; i <= 5; i++){
            MenuTree menuTree = new MenuTree();
            menuTree.setId(i);
            menuTree.setParentId(0L);
            menuTree.setSorts(random.nextInt(5));
            menuTree.setName("一级菜单"+i);
            list.add(menuTree);
        }


        for(long i = 6; i <= 10; i++){
            MenuTree menuTree = new MenuTree();
            menuTree.setId(i);
            menuTree.setSorts(random.nextInt(10));
            menuTree.setParentId(Long.valueOf(random.nextInt(5)));
            menuTree.setName(i + "级菜单1");
            list.add(menuTree);
        }
        for(long i = 11; i <= 30; i++){
            MenuTree menuTree = new MenuTree();
            menuTree.setSorts(random.nextInt(30));
            menuTree.setId(i);
            menuTree.setParentId(Long.valueOf(random.nextInt(10)));
            menuTree.setName("菜单"+i);
            list.add(menuTree);
        }


        System.out.println(JSONUtil.toJsonPrettyStr(TreeUtil.getMenuTree(list,0L,"全部")));
    }
}
