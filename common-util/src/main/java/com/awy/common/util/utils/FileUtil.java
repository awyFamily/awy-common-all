package com.awy.common.util.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    /**
     * 会默认创建一个当前时间的文件夹，并只保留今天的文件夹，其他文件夹将被删除
     * @param fileName
     * @return
     */
    public static String getFolderPath(String fileName) {
        String currentPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath().concat(File.separator).concat(fileName);
        SimpleDateFormat df = new SimpleDateFormat(DatePattern.PURE_DATE_PATTERN);
        String filePath = currentPath.concat(File.separator).concat(df.format(new Date()));
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        delAllFile(currentPath,df.format(new Date()));
        return filePath;
    }

    public static String getFilePath(String folderName,String fileName){
        if(StrUtil.isEmpty(folderName)){
            folderName = "tmp";
        }
        if(StrUtil.isEmpty(fileName)){
            fileName = UUID.randomUUID().toString();
        }
        String filePath = getFolderPath(folderName);
        return filePath.concat(File.separator).concat(fileName);
    }

    /**
     * 删除指定文件下文件(保留一个文件)
     * @param path 文件路径
     * @param reservedName  保留的文件夹名
     * @return 布尔值
     * @author yhw
     */
    public static boolean delAllFile(String path,String reservedName) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if(!tempList[i].equals(reservedName)) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }

                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + "/" + tempList[i]);
                    delFolder(path + "/" + tempList[i]);
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return 布尔值
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除文件夹
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
