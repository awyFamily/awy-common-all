package com.awy.data.kit;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.awy.common.util.utils.ReflexUtils;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.util.StrUtil.toUnderlineCase;
import static cn.hutool.core.util.StrUtil.upperFirst;
import static com.awy.common.util.utils.ReflexUtils.getValue;

/**
 * @author yhw
 */
@Slf4j
public final class InsertOrUpdateSql {
    private InsertOrUpdateSql() {
    }

    @SuppressWarnings("deprecation")
	private static final String generateInsertValue(Object[] rowValues) {
        Assert.notNull(rowValues);
        StringBuilder sb = new StringBuilder("(");

        for(int i = 0; i < rowValues.length; ++i) {
            sb.append(ReflexUtils.getValue(rowValues[i]));
            if (i != rowValues.length - 1) {
                sb.append(",");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
	private static final String generateInsertValueBatch(List<Object[]> rowBatch) {
        Assert.notNull(rowBatch);
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < rowBatch.size(); ++i) {
            Object[] row = (Object[])rowBatch.get(i);
            String rowValues = generateInsertValue(row);
            sb.append(rowValues);
            if (i != rowBatch.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("deprecation")
	public static String generateInsertSql(String tableName, String[] fields, List<Object[]> rowBatch) {
        Assert.isFalse(StrUtil.isBlank(tableName),"符串不能为null或者空");
        Assert.notNull(fields);
        Assert.notNull(rowBatch);
        StringBuilder sb = (new StringBuilder("INSERT INTO ")).append(javaFieldName2SqlFieldName(tableName)).append("(");

        for(int i = 0; i < fields.length; ++i) {
            String field = javaFieldName2SqlFieldName(fields[i]);
            sb.append(field);
            if (i != fields.length - 1) {
                sb.append(",");
            }
        }

        sb.append(")").append(" VALUES");
        return sb.append(generateInsertValueBatch(rowBatch)).toString();
    }

    
    public static final String javaFieldName2SqlFieldName(String javaFieldName) {
        if (StrUtil.isEmpty(javaFieldName)) {
            throw new NullPointerException("javaFieldName不能为null");
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < javaFieldName.length(); ++i) {
                char c = javaFieldName.charAt(i);
                if (!Character.isLowerCase(c)) {
                    if (i != 0) {
                        sb.append("_");
                    }

                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    //==============================================================================
    private static  <T> List<String> getColumnList(Class<T> t, List<T> list, String...ignoreColumn){
        List<Field> fieldList = ReflexUtils.getClassAllField(t,ignoreColumn);
        //列名
        Assert.isFalse(fieldList==null||fieldList.isEmpty(),"列名为空");

        Assert.isFalse(list==null||list.isEmpty(),"集合为空");
        return fieldList.stream().map(field->field.getName()).collect(Collectors.toList());
    }
    //=============================================================================

    /**
     * 大数据批量新增,不需要setID(hasDelete默认为false,新增请忽略创建，修改时间)
     * @param t 实体名
     * @param list 新增对应的实体集合
     * @param ignoreColumn 忽略的列
     * @return 批量新增SQL语句
     * @author yhw
     */
    public static <T> String getInsertSql(Class<T> t,List<T> list,String...ignoreColumn){
        StringBuilder sql = new StringBuilder();
        List<String> columnList = getColumnList(t, list, ignoreColumn);
        sql.append("insert into ");
        sql.append(getTableName(t));//表名
        sql.append(" (");
        sql.append(columnList.stream().map(str->toUnderlineCase(str)).collect(Collectors.joining(",")));//列名
        sql.append(") values");

        sql.append(getInsertColumnStr(list,columnList));
        return sql.toString();
    }

    /**
     * 获取新增列SQLstr
     * @param list 实体数据列表
     * @param columnList 实体属性列表
     * @return SQL语句
     * @author yhw
     */
    private static <T> String getInsertColumnStr(List<T> list,List<String> columnList){
        long start = System.currentTimeMillis();
        StringBuilder sql = new StringBuilder();
        int size = list.size();
        int columnSize = columnList.size();
        Method method;
        String methodName;
        T obj;
        Object invokeValue;
        for(int i =0;i<size;i++) {
            obj = list.get(i);
            sql.append("(");
            for(int j = 0;j<columnSize;j++) {
                if("id".equals(columnList.get(j))) {
                    methodName = "get"+upperFirst(columnList.get(j));
                    method = ReflexUtils.getMethod(methodName, obj);
                    invokeValue = ReflexUtils.getMethodInvoke(method,obj);
                    sql.append(invokeValue != null ? getValue(invokeValue) : "'".concat(UUID.randomUUID().toString(true)).concat("'"));
                }else if("hasDelete".equals(columnList.get(j))){
                    sql.append("0");
                }else {
                    //拼接列
                    methodName = "get"+upperFirst(columnList.get(j));
                    method = ReflexUtils.getMethod(methodName, obj);
                    sql.append(getValue(ReflexUtils.getMethodInvoke(method,obj)));
                }
                sql.append(j==(columnSize-1) ? "":",");
            }
            sql.append(i==(size-1)?")":"),");
        }
        log.info("新增sql拼接耗时================>"+(System.currentTimeMillis() - start)+"(hs)");
        return sql.toString();
    }


    private static <T> String getTableName(Class<T> t) {
        TableName annotation = t.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return toUnderlineCase(t.getSimpleName());
    }
    //==================================================================================
    /**
     * 批量修改(请确保list对象中主键列值不能为空)
     * @param t 实体类名
     * @param primaryKey 主键列名(不传递默认主键ID)
     * @param list 修改对应的实体集合
     * @param ignoreColumn 忽略的列
     * @return 批量修改SQL语句
     * @author yhw
     */
    public static <T> String getUpdateSql(Class<T> t,String primaryKey,List<T> list,String...ignoreColumn) {
        long start = System.currentTimeMillis();
        StringBuilder sql = new StringBuilder();


        if(StrUtil.isEmpty(primaryKey)) {
            primaryKey = "id";
        }
        List<String> ignoreColumnList = Lists.newArrayList();
        if(ignoreColumn != null && ignoreColumn.length > 0) {
            ignoreColumnList.addAll(Stream.of(ignoreColumn).collect(Collectors.toList()));
        }
        ignoreColumnList.add(primaryKey);
        ignoreColumn = ignoreColumnList.toArray(new String[ignoreColumnList.size()]);


        List<String> columnList = getColumnList(t, list, ignoreColumn);

        sql.append("UPDATE ");
        sql.append(getTableName(t));//表名
        sql.append(" SET ");
        //修改内容
        sql.append(getUpdateSqlStr(primaryKey,list,columnList));
        log.info("修改sql拼接耗时================>"+(System.currentTimeMillis() - start)+"(hs)");
        return sql.toString();
    }

    /**
     * 获取修改语句
     * @param primaryKey 主键列名
     * @param list 修改对应的实体集合
     * @param columnList 实体属性列表
     * @return 修改语句
     * @author yhw
     */
    private static <T> StringBuilder getUpdateSqlStr(String primaryKey,List<T> list,List<String> columnList){
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlIn = new StringBuilder();//条件语句
        int size = list.size();//数据集合长度
        int columnSize = columnList.size();//属性集合长度
        Method method;//当前方法
        Method primaryKeyMethod = ReflexUtils.getMethod("get"+upperFirst(primaryKey), list.get(0));
        T obj;

        for(int i=0;i<columnSize;i++) {
            sql.append(" ");
            sql.append("`"+toUnderlineCase(columnList.get(i))+"`");//修改列
            sql.append(" = CASE ");
            sql.append(toUnderlineCase(primaryKey));//主键列
            for(int j = 0;j<size;j++) {
                obj = list.get(j);
                method = ReflexUtils.getMethod("get"+upperFirst(columnList.get(i)), obj);
				/*if(getValue(CheckUtil.getMethodInvoke(method, obj))==null||StringUtils.isEmpty(getValue(CheckUtil.getMethodInvoke(method, obj)).toString().trim())) {
					break;
				}*/
                sql.append(" WHEN ");
                sql.append(getValue(ReflexUtils.getMethodInvoke(primaryKeyMethod, obj)));
                sql.append(" THEN ");
                sql.append(getValue(ReflexUtils.getMethodInvoke(method, obj)));

                if(i==(columnSize -1)) {
                    sqlIn.append(getValue(ReflexUtils.getMethodInvoke(primaryKeyMethod, obj)));
                    sqlIn.append(j == (size-1)?"":",");
                }
            }
            sql.append(i == (columnSize -1) ? "END" : "END, ");
        }

        sql.append(" WHERE ");
        sql.append(toUnderlineCase(primaryKey));//主键列
        sql.append(" IN ");
        sql.append("(");
        sql.append(sqlIn);
        sql.append(")");
        return sql;
    }


}
