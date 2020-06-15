package com.yhw.common.email.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;

/**
 * freemarker 工具类
 * @@author  yhw
 */
public class FreemarkerUtil {

    /**
     * 把数据结构渲染到模板页面。
     * @param ftlFullName 模板目录下文件全名如：test.ftlh
     * @param data 数据结构。
     * @return 返回渲染后的静态文件。html,否则返回null
     */
    public static final  String getFreemarkerDealText(Configuration cfg, String ftlFullName, Object data)  {
        try (StringWriter out = new StringWriter()){
            Template temp = cfg.getTemplate(ftlFullName);
            temp.process(data,out);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 渲染数据到页面并输出新的html页面。
     * @param ftlFullName 要渲染的模板文件，在模板目录下
     * @param targetHtmlPath 要输出的新的html文件路径
     * @param data 模板数据
     */
    public static void createHTML(Configuration cfg,String ftlFullName,String targetHtmlPath,Object data)  {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetHtmlPath)), "UTF-8"))){
            Template template = cfg.getTemplate(ftlFullName);
            template.process(data, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
