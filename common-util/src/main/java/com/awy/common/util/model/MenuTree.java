package com.awy.common.util.model;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * 菜单树
 * @author yhw
 */
@Data
public class MenuTree implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;


    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 菜单路径
     */
    private String uri;


    /**
     * 排序
     */
    private Integer sorts;


    /**
     * 父权限Id
     */
    private Long parentId;

    /**
     * 几级菜单(层级)
     */
    private Integer level;

    /**
     * 图标路径
     */
    private String icon;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 下级菜单
     */
    private List<MenuTree> child;


    /**
     * 获取菜单树
     * @param source 未生成树的列表
     * @param topNode 顶级节点的条件
     * @return 树列表
     */
    public static List<MenuTree> getTree(List<MenuTree> source, Long topNode){
        MenuTree topTree = new MenuTree();
        topTree.setId(topNode);
        getChild(source,topTree,10);
        return topTree.getChild();
    }

    private static MenuTree getChild(List<MenuTree> source, MenuTree tree, int depth){
        if(depth <= 0){
            return tree;
        }

        List<MenuTree> child = source.stream().filter(menu->{
            return  tree.getId().equals(menu.getParentId());
        }).sorted(comparing(MenuTree::getSorts)).collect(Collectors.toList());

        tree.setChild(child);

        if(CollectionUtil.isEmpty(child)){
            return tree;
        }

        for (MenuTree menuTree : child){
            getChild(source,menuTree,(depth - 1));
        }

        return tree;
    }

    /*public static void main(String[] args) {
        List<MenuTree> list = new ArrayList<>();
        Random random = new Random();
        for(long i = 1; i <= 5; i++){
            MenuTree menuTree = new MenuTree();
            menuTree.setId(i);
            menuTree.setParentId(0L);
            menuTree.setSorts(random.nextInt(5));
            menuTree.setPermissionName("一级菜单"+i);
            list.add(menuTree);
        }


        for(long i = 6; i <= 10; i++){
            MenuTree menuTree = new MenuTree();
            menuTree.setId(i);
            menuTree.setSorts(random.nextInt(10));
            menuTree.setParentId(Long.valueOf(random.nextInt(5)));
            menuTree.setPermissionName(i + "级菜单1");
            list.add(menuTree);
        }
        for(long i = 11; i <= 30; i++){


            MenuTree menuTree = new MenuTree();
            menuTree.setSorts(random.nextInt(30));
            menuTree.setId(i);
            menuTree.setParentId(Long.valueOf(random.nextInt(10)));
            menuTree.setPermissionName("菜单"+i);
            list.add(menuTree);
        }


        list = getTree(list,0L);
        System.out.println(JSONUtil.toJsonPrettyStr(list));
    }*/
}
