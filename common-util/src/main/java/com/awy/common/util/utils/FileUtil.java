package com.awy.common.util.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件工具类
 * @author yhw
 */
public class FileUtil {

    /**
     * 读取并解析文件(使用utf-8字符集)
     * @param filePath 文件相对路径(需要文件在当前项目上下文)
     * @param parsing 文件解析器
     * @return 文件解析列表
     */
    public static <T> List<T> readFile(String filePath, Function<String,T> parsing)   {
        return readFile(filePath,parsing,"utf-8");
    }

    /**
     * 读取并解析文件
     * @param filePath 文件相对路径(需要文件在当前项目上下文)
     * @param parsing 文件解析器
     * @param charsetName 字符集
     * @return 文件解析列表
     */
    public static <T> List<T> readFile(String filePath, Function<String,T> parsing,String charsetName)   {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath), charsetName))) {
            return br.lines().map(parsing).collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 在桌面创建一个文件夹
     * @param folderName
     */
    public static String createHomeFolder(String folderName){
        folderName = FileSystemView.getFileSystemView().getHomeDirectory().getPath().concat(File.separator).concat(folderName);
        createFolder(folderName);
        return folderName;
    }

    public static void createFolder(String path){
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获取 folderName 下当前时间文件夹的文件名称
     * 例如: folderName = temp , fileName = test ,当前时间等于(2019-03-12)
     * 返回: /temp/20190312/test 的文件地址
     * @param folderName 父文件名称
     * @param fileName 需要返回的文件的名称
     * @return 文件绝对路径
     */
    public static String getCurrentDataFormatFilePath(String folderName,String fileName){
        return getCurrentDataFormatFilePath(folderName,fileName,false);
    }

    /**
     * 获取 folderName 下当前时间文件夹的文件名称
     * 例如: folderName = temp , fileName = test ,当前时间等于(2019-03-12)
     * 返回: /temp/20190312/test 的文件地址
     * @param folderName 父文件名称
     * @param fileName 需要返回的文件的名称
     * @param isCleanOtherFile 是否清空当前文件夹的其他文件
     * @return 文件绝对路径
     */
    public static String getCurrentDataFormatFilePath(String folderName,String fileName,boolean isCleanOtherFile){
        if(StrUtil.isEmpty(fileName)){
            fileName = UUID.randomUUID().toString();
        }
        String filePath = createCurrentDataFormatFilePath(folderName,isCleanOtherFile);
        return filePath.concat(File.separator).concat(fileName);
    }

    /**
     * 在 fileName 下 创建一个当前时间(天) 的文件夹
     * 例如： folderName = temp, isCleanOtherFile = true ,当前时间等于(2019-03-12)
     * 返回: /temp/20190312 的文件夹地址,并且清除 test 下其他的所有文件(文件名为: 20190312 不会被删除)
     * @param folderName 父文件夹名称
     * @param isCleanOtherFile 是否清空当前文件夹的其他文件, 为 true(文件名为: 20190312 不会被删除)
     * @return  fileName下 当前时间(天)的文件夹绝对路径
     */
    public static String createCurrentDataFormatFilePath(String folderName,boolean isCleanOtherFile){
        if(StrUtil.isEmpty(folderName)){
            folderName = "tmp";
        }

        String currentPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath().concat(File.separator).concat(folderName);
        SimpleDateFormat df = new SimpleDateFormat(DatePattern.PURE_DATE_PATTERN);

        //文件名称
        String currentDateStr = df.format(new Date());
        String filePath = currentPath.concat(File.separator).concat(currentDateStr);
        createFolder(filePath);
        //删除子文件夹
        if(isCleanOtherFile){
            delAllFile(currentPath,currentDateStr);
        }
        return filePath;
    }

    /**
     * 删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return 布尔值
     */
    public static boolean delAllFile(String path) {
        return delAllFile(path,"");
    }

    /**
     * 删除指定文件或目录
     * @param path 文件路径
     * @param reservedName  保留的文件夹名
     * @return 布尔值
     */
    public static boolean delAllFile(String path,String reservedName) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        //获取文件下, 所有子文件
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if(!tempList[i].equals(reservedName)) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isDirectory()) {
                    delAllFile(path + File.separator + tempList[i],"");
                }
                temp.delete();
            }
        }
        return true;
    }
}
