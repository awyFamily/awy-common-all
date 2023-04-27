package com.awy.common.util.utils;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import com.taobao.arthas.core.util.Decompiler;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 异常工具
 * 打印出现异常的代码;  类似 jdk14 输出空指针导致异常的代码
 * @author yhw
 * @date 2023-04-26
 */
public final class ExceptionHelper {

    private final static String classes = "classes";
    private final static String target = "target";
    private final static String dot_class = ".class";
    private final static String dot = ".";
    private final static String default_charset = "utf8";
    private final static String match_prefix = "/*";
    private final static String match_suffix = "*/";
    private final static String line_break_symbol = "\n";
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
                if (element.getClassName().contains(rootPackageName)) {
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

        File file = new File(dot);
        boolean hasNative = Arrays.stream(file.list()).anyMatch(obj -> target.equals(obj));
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(file.getCanonicalPath()).append(File.separator);
            if (hasNative) {
                sb.append(target).append(File.separator);
            }
            sb.append(classes).append(File.separator);
            sb.append(stackTraceElement.getClassName().replace(dot, File.separator));
            sb.append(dot_class);
//            String codes = Decompiler.decompile(sb.toString(), stackTraceElement.getMethodName());
//            lineContent = readLineByIndexOf(codes, stackTraceElement.getLineNumber());
            lineContent = Decompiler.decompile(sb.toString(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            CONTENT_CACHE.put(cacheKey, lineContent);
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
