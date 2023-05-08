package com.awy.common.util.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.awy.common.util.model.BaseTree;
import com.awy.common.util.model.MenuTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * @author yhw
 * @date 2022-12-10
 */
public class TreeUtil {

    /**
     * 获取菜单树(递归)
     * @param source 未生成树的列表
     * @param topId 顶级节点ID
     * @param topName 顶级节点ID
     * @return 树列表
     */
    public static MenuTree getMenuTree(List<MenuTree> source, Long topId, String topName){
        MenuTree topTree = new MenuTree();
        topTree.setId(topId);
        topTree.setName(topName);
        return buildTree(source,topTree,(byte) 10);
    }

    /**
     * 构建指定层高的树(递归)
     * @param source 原始列表
     * @param root root 树
     * @param depth 获取层搞
     * @param <T> 树对象
     * @return 构建好的树
     */
    public static <T extends BaseTree> T buildTree(List<T> source, T root, byte depth){
        if(depth <= 0){
            return root;
        }
        List<T> child = source.stream().filter(menu->{
            return  root.getId().equals(menu.getParentId());
        }).sorted(comparing(BaseTree::getSorts)).collect(Collectors.toList());
        root.setChildren(child);

        if(CollectionUtil.isEmpty(child)){
            return root;
        }

        --depth;
        for (T tree : child){
            buildTree(source,tree,depth);
        }
        return root;
    }

    /**
     * 构建指定层高的森林
     * @param source 原始列表
     * @param rootId root 顶ID标识
     * @param depth 获取层搞
     * @param <T> 树对象
     * @return 构建好的树
     */
    public static <T extends BaseTree> List<T>  buildDepthForest(List<T> source, Long rootId , byte depth) {
        if (CollUtil.isEmpty(source)) {
            return new ArrayList<>();
        }

        Map<Long, List<T>> groupMap = source.stream().collect(Collectors.groupingBy(BaseTree::getParentId));

        List<T> rootList = groupMap.get(rootId).stream().sorted(comparing(BaseTree::getSorts)).collect(Collectors.toList());
        if (CollUtil.isEmpty(rootList)) {
            return new ArrayList<>();
        }

        List<T> inner = rootList;
        List<T> temps;
        for (byte i = 1; i < depth; i++) {
            if (CollUtil.isEmpty(inner)) {
                break;
            }
            temps = new ArrayList<>();
            for (T tree : inner) {
                tree.setChildren(groupMap.getOrDefault(tree.getId(), new ArrayList<>()).stream().sorted(comparing(BaseTree::getSorts)).collect(Collectors.toList()));
                temps.addAll(tree.getChildren());
            }
            inner = temps;
        }
        return rootList;
    }

    /**
     * 构建指定层高的森林
     * @param source 原始列表
     * @param rootId root 顶ID标识
     * @param <T> 树对象
     * @return 构建好的树
     */
    public static <T extends BaseTree> List<T> buildForest(List<T> source, Long rootId) {
        if (CollUtil.isEmpty(source)) {
            return new ArrayList<>();
        }

        Map<Long, List<T>> groupMap = source.stream().collect(Collectors.groupingBy(BaseTree::getParentId));
        for (T tree : source) {
            tree.setChildren(groupMap.getOrDefault(tree.getId(),new ArrayList<>()).stream().sorted(comparing(BaseTree::getSorts)).collect(Collectors.toList()));
        }
        return groupMap.getOrDefault(rootId,new ArrayList<>());
    }

    /**
     * 获取当前树下所有节点
     * @param trees 目标树
     * @param <T> 树对象
     * @return 所有节点
     */
    public <T extends BaseTree> List<T> getAllNodes(List<T> trees) {
        if (CollUtil.isEmpty(trees)) {
            return new ArrayList<>();
        }
        Stack<T> stack = new Stack<>();
        stack.addAll(trees);

        List<T> allNodes = new ArrayList<>(trees);
        T tree;
        while (!stack.isEmpty()) {
            tree = stack.pop();
            if (CollUtil.isNotEmpty(tree.getChildren())) {
                stack.addAll(tree.getChildren());
                allNodes.addAll(tree.getChildren());
            }
        }
        return allNodes;
    }
}
