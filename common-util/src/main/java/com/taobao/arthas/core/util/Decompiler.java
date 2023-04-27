package com.taobao.arthas.core.util;

import cn.hutool.core.util.StrUtil;
import com.taobao.arthas.common.Pair;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns.LineNumberMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author hengyunabc 2018-11-16
 *
 */
public class Decompiler {

    /**
     * 获取指定行代码
     * @param classFilePath 地址
     * @param methodName 方法名称
     * @param lineNumber 行号
     * @return 行代码
     */
    public static String decompile(String classFilePath, String methodName, int lineNumber) {
        if (lineNumber < 0) {
            return StrUtil.EMPTY;
        }
        Pair<String, NavigableMap<Integer, Integer>> pair = decompileWithMappings(classFilePath, methodName, false);
        NavigableMap<Integer, Integer> lineMapping = pair.getSecond();
        String resultCode = pair.getFirst();
        int actualLine = -1;
        for (Map.Entry<Integer, Integer> entry : lineMapping.entrySet()) {
            if (lineNumber == entry.getValue().intValue()) {
                actualLine = entry.getKey() - 1;
                break;
            }
        }
        if (actualLine < 0) {
            return StrUtil.EMPTY;
        }
        List<String> lines = toLines(resultCode);
        return lines.get(actualLine);
    }

    public static String decompile(String classFilePath, String methodName) {
        return decompile(classFilePath, methodName, false);
    }

    public static String decompile(String classFilePath, String methodName, boolean hideUnicode) {
        return decompile(classFilePath, methodName, hideUnicode, true);
    }

    public static String decompile(String classFilePath, String methodName, boolean hideUnicode,
                                   boolean printLineNumber) {
        return decompileWithMappings(classFilePath, methodName, hideUnicode, printLineNumber).getFirst();
    }

    public static Pair<String, NavigableMap<Integer, Integer>> decompileWithMappings(String classFilePath,
                                                                                     String methodName, boolean hideUnicode, boolean printLineNumber) {
        Pair<String, NavigableMap<Integer, Integer>> pair = decompileWithMappings(classFilePath, methodName, hideUnicode);
        if (!printLineNumber) {
            return pair;
        }

        NavigableMap<Integer, Integer> lineMapping = pair.getSecond();
        String resultCode = pair.getFirst();
        if (!lineMapping.isEmpty()) {
            resultCode = addLineNumber(resultCode, lineMapping);
        }
        return Pair.make(resultCode, lineMapping);
    }

    private static Pair<String, NavigableMap<Integer, Integer>> decompileWithMappings(String classFilePath,
            String methodName, boolean hideUnicode) {
        final StringBuilder sb = new StringBuilder(8192);
        final NavigableMap<Integer, Integer> lineMapping = new TreeMap<Integer, Integer>();

        OutputSinkFactory mySink = new OutputSinkFactory() {
            @Override
            public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                return Arrays.asList(SinkClass.STRING, SinkClass.DECOMPILED, SinkClass.DECOMPILED_MULTIVER,
                        SinkClass.EXCEPTION_MESSAGE, SinkClass.LINE_NUMBER_MAPPING);
            }

            @Override
            public <T> Sink<T> getSink(final SinkType sinkType, final SinkClass sinkClass) {
                return new Sink<T>() {
                    @Override
                    public void write(T sinkable) {
                        // skip message like: Analysing type demo.MathGame
                        if (sinkType == SinkType.PROGRESS) {
                            return;
                        }
                        if (sinkType == SinkType.LINENUMBER) {
                            LineNumberMapping mapping = (LineNumberMapping) sinkable;
                            NavigableMap<Integer, Integer> classFileMappings = mapping.getClassFileMappings();
                            NavigableMap<Integer, Integer> mappings = mapping.getMappings();
                            if (classFileMappings != null && mappings != null) {
                                for (Entry<Integer, Integer> entry : mappings.entrySet()) {
                                    Integer srcLineNumber = classFileMappings.get(entry.getKey());
                                    lineMapping.put(entry.getValue(), srcLineNumber);
                                }
                            }
                            return;
                        }
                        sb.append(sinkable);
                    }
                };
            }
        };

        HashMap<String, String> options = new HashMap<String, String>();
        /**
         * @see org.benf.cfr.reader.util.MiscConstants.Version.getVersion() Currently,
         *      the cfr version is wrong. so disable show cfr version.
         */
        options.put("showversion", "false");
        options.put("hideutf", String.valueOf(hideUnicode));
        options.put("trackbytecodeloc", "true");
        if (!StrUtil.isBlank(methodName)) {
            options.put("methodname", methodName);
        }

        CfrDriver driver = new CfrDriver.Builder().withOptions(options).withOutputSink(mySink).build();
        List<String> toAnalyse = new ArrayList<String>();
        toAnalyse.add(classFilePath);
        driver.analyse(toAnalyse);

        String resultCode = sb.toString();
//        if (printLineNumber && !lineMapping.isEmpty()) {
//            resultCode = addLineNumber(resultCode, lineMapping);
//        }

        return Pair.make(resultCode, lineMapping);
    }

    private static String addLineNumber(String src, Map<Integer, Integer> lineMapping) {
        int maxLineNumber = 0;
        for (Integer value : lineMapping.values()) {
            if (value != null && value > maxLineNumber) {
                maxLineNumber = value;
            }
        }

        String formatStr = "/*%2d*/ ";
        String emptyStr = "       ";

        StringBuilder sb = new StringBuilder();

        List<String> lines = toLines(src);

        if (maxLineNumber >= 1000) {
            formatStr = "/*%4d*/ ";
            emptyStr = "         ";
        } else if (maxLineNumber >= 100) {
            formatStr = "/*%3d*/ ";
            emptyStr = "        ";
        }

        int index = 0;
        for (String line : lines) {
            Integer srcLineNumber = lineMapping.get(index + 1);
            if (srcLineNumber != null) {
                sb.append(String.format(formatStr, srcLineNumber));
            } else {
                sb.append(emptyStr);
            }
            sb.append(line).append("\n");
            index++;
        }

        return sb.toString();
    }

    public static List<String> toLines(String text) {
        List<String> result = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(text));
        try {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (IOException exc) {
            // quit
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return result;
    }

}
