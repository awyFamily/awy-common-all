package com.awy.common.util.utils;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.taobao.arthas.core.util.Decompiler;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 异常工具
 * 打印出现异常的代码;  类似 jdk14 输出空指针导致异常的代码
 * @author yhw
 * @date 2023-04-26
 */
public final class ExceptionHelper {
    private final static String dot_class = ".class";
    private final static String dot_java = ".java";
    private final static String dot_jar = ".jar";
    private final static String dot = ".";
    private final static String default_charset = "utf8";
    private final static String match_prefix = "/*";
    private final static String match_suffix = "*/";
    private final static String line_break_symbol = "\n";
    private final static String resource_concat_symbol = "/";
    private final static String tmpdir = "java.io.tmpdir";
    private final static Cache<String, String>  CONTENT_CACHE = CacheUtil.newLRUCache(300);

    public static String getTriggerCodeLineContent(Exception e) {
        return getTriggerCodeLineContent(e, StrUtil.EMPTY);
    }

    public static String getTriggerCodeLineContent(Exception e, String rootPackageName) {
        if (e == null || e.getStackTrace() == null || !(e.getStackTrace().length > 0)) {
            return StrUtil.EMPTY;
        }
        StackTraceElement stackTraceElement = null;
        if (StrUtil.isNotBlank(rootPackageName)) {
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().length() >= rootPackageName.length() && rootPackageName.equals(element.getClassName().substring(0, rootPackageName.length()))) {
                    stackTraceElement = element;
                    break;
                }
            }
        }
        if (stackTraceElement == null) {
            stackTraceElement = e.getStackTrace()[0];
        }
        String cacheKey = stackTraceElement.getClassName() + stackTraceElement.getMethodName() + stackTraceElement.getLineNumber();
        String lineContent = CONTENT_CACHE.get(cacheKey);
        if (StrUtil.isNotBlank(lineContent)) {
            return lineContent;
        }

        Class<?> failClass;
        try {
            failClass = Class.forName(stackTraceElement.getClassName());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        URL location = failClass.getProtectionDomain().getCodeSource().getLocation();

        StringBuilder sb = new StringBuilder();
        try {
            File tempFile = null;
            int jarIndexOf = location.getPath().indexOf(dot_jar);
            if (jarIndexOf > 0) {
                InputStream stream = failClass.getClassLoader().getResourceAsStream(stackTraceElement.getClassName().replace(dot, resource_concat_symbol).concat(dot_class));
                String tempDir = System.getProperty(tmpdir);
                String className = stackTraceElement.getFileName().substring(0, stackTraceElement.getFileName().length() - dot_java.length()).concat(dot_class);
                tempFile = new File(tempDir.concat(File.separator).concat(className));
                FileWriter.create(tempFile).writeFromStream(stream);
                sb.append(tempFile.getPath());
            } else {
                sb.append(new File(location.getFile()).getCanonicalPath()).append(File.separator);
                sb.append(stackTraceElement.getClassName().replace(dot, File.separator));
                sb.append(dot_class);
            }
            lineContent = Decompiler.decompile(sb.toString(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            CONTENT_CACHE.put(cacheKey, lineContent);
            if (tempFile != null) {
                tempFile.delete();
            }
            return lineContent;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String readLine(String content, int lineNumber)  {
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(Charset.forName(default_charset))), Charset.forName(default_charset)))) {
            String line;
            String match = match_prefix + lineNumber + match_suffix;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > match.length()) {
                    if (match.equals(line.substring(0, match.length()))) {
                        line = line.replace(match, StrUtil.EMPTY).trim();
                        return line;
                    }
                }
            }
            return StrUtil.EMPTY;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readLineByIndexOf(String content, int lineNumber)  {
        String match = match_prefix + lineNumber + match_suffix;
        int indexOf = content.indexOf(match);
        if (indexOf < 0) {
            return StrUtil.EMPTY;
        }
        content = content.substring(indexOf + match.length(), content.length()).trim();
        indexOf = content.indexOf(line_break_symbol);
        if (indexOf < 0) {
            return content;
        }
        content = content.substring(0, indexOf);
        return content;
    }
}
