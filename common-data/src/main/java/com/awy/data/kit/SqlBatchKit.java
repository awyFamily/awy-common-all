package com.awy.data.kit;


import cn.hutool.core.bean.BeanUtil;
import com.awy.common.util.model.BasePageDTO;
import com.awy.common.util.utils.CollUtil;
import com.awy.common.util.utils.ReflexUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.awy.data.kit.InsertOrUpdateSql.*;


/**
 * 批量操作数据库工具
 * @author yhw
 */
@Slf4j
public final class SqlBatchKit {

    private SqlBatchKit(){}

    /**
     * 批量更新
     * @param jdbcTemplate jdbc模板
     * @param t 实体名称
     * @param list 更新list
     * @param <T> 映射实体对象
     */
    public static <T> void updateBatch(JdbcTemplate jdbcTemplate,Class<T> t,List<T> list){
        updateBatch(jdbcTemplate,t,list,null);
    }

    /**
     * 批量更新
     * @param jdbcTemplate jdbc模板
     * @param t 实体名称
     * @param list 更新list
     * @param ignoreColumn 忽略列
     * @param <T>  映射实体对象
     */
    public static <T> void updateBatch(JdbcTemplate jdbcTemplate,Class<T> t,List<T> list,String...ignoreColumn){
        updateBatch(jdbcTemplate,t,"",list,ignoreColumn);
    }

    /**
     * 批量更新
     * @param jdbcTemplate jdbc模板
     * @param t 实体名称
     * @param primaryKey 主键key
     * @param list 更新list
     * @param ignoreColumn 忽略列
     * @param <T> 映射实体对象
     */
    public static <T> void updateBatch(JdbcTemplate jdbcTemplate,Class<T> t,String primaryKey,List<T> list,String...ignoreColumn){
        jdbcTemplate.update(getUpdateSql(t,primaryKey,list,ignoreColumn));
    }

    /**
     * 批量插入（默认id为自增主键）
     * @param jdbcTemplate jdbc模板
     * @param t 实体名
     * @param list 插入list
     * @param <T>  映射实体对象
     */
    public static <T> void  addBatch(JdbcTemplate jdbcTemplate, Class<T> t, List<T> list){
        addBatch(jdbcTemplate,t,list,true);
    }

    /**
     * 批量插入
     * @param jdbcTemplate jdbc模板
     * @param t 实体名
     * @param list 插入list
     * @param isAutoIncrement true (id为自增主键)
     * @param <T> 映射实体对象
     */
    public static <T> void  addBatch(JdbcTemplate jdbcTemplate, Class<T> t, List<T> list,boolean isAutoIncrement){
        String ignoreId = isAutoIncrement ? "id" : null;
        addBatch(jdbcTemplate,t,list,ignoreId);
    }

    /**
     * 批量插入
     * @param jdbcTemplate jdbc模板
     * @param t 实体名
     * @param list 插入list
     * @param ignoreColumn 忽略插入的列(如果id为自增列，请忽略id)
     * @param <T> 映射实体对象
     */
    public static <T> void  addBatch(JdbcTemplate jdbcTemplate, Class<T> t, List<T> list, String...ignoreColumn){
        jdbcTemplate.update(getInsertSql(t,list,ignoreColumn));
    }



    /**
     * 精准大批量插入数据。
     * @param jdbcTemplate jdbc模板
     * @param tableName 表名
     * @param fields 列名
     * @param values 插入值
     */
    public static void addBatch(JdbcTemplate jdbcTemplate, String tableName, String[] fields, List<Object[]> values){
       try{
           long startTime = System.currentTimeMillis();
           if (jdbcTemplate != null && fields != null && values != null) {
               Connection conn = jdbcTemplate.getDataSource().getConnection();
               conn.setAutoCommit(false);
               PreparedStatement pst = conn.prepareStatement(" ");
               int total = values.size();
               int step = 10000;
               int per = total % step == 0 ? total / step : total / step + 1;

               for(int i = 0; i < per; ++i) {
                   String sql = null;
                   if (total <= step) {
                       sql = generateInsertSql(tableName, fields, values);
                   } else if (i == per - 1) {
                       sql = generateInsertSql(tableName, fields, values.subList(i * step, total));
                   } else {
                       sql = generateInsertSql(tableName, fields, values.subList(i * step, i * step + step));
                   }

                   pst.addBatch(sql);
                   pst.executeBatch();
                   conn.commit();
               }

               pst.close();
               conn.close();
               long endTime = System.currentTimeMillis();
               log.info("===批量插入用时（s）：" + (endTime - startTime) / 1000L);
           } else {
               throw new NullPointerException("不能传null");
           }
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }

    /**
     * 批量执行
     * @param function 函数
     * @param dto 查询参数
     * @param result 响应结果
     */
    public static  <Q extends BasePageDTO,R> void batchQueryPage(Function<Q, Page<R>> function, Q dto, CopyOnWriteArrayList<R> result) {
        ExecutorService batchExportJobPool = Executors.newFixedThreadPool(3);
        batchQueryPage(function,dto,result,batchExportJobPool,100, TimeUnit.SECONDS);
    }

    /**
     * 批量执行
     * @param function 函数
     * @param dto 查询参数
     * @param result 响应结果
     * @param batchExportJobPool 线程池
     * @param timeout 超时时间
     * @param unit 超时单位
     */
    public static  <Q extends BasePageDTO,R> void batchQueryPage(Function<Q, Page<R>> function, Q dto, CopyOnWriteArrayList<R> result,ExecutorService batchExportJobPool,long timeout, TimeUnit unit) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Page<R> page = function.apply(dto);
        log.info("==================== listData size : " + page.getSize());
        if (page != null && CollUtil.isNotEmpty(page.getRecords())) {
            result.addAll(page.getRecords());
            //need async
            if (page.getPages() > 1) {
                for (long i = 2; i < page.getPages(); i++) {
                    Q q = ReflexUtils.newInstance(dto.getClass().getName());
                    BeanUtil.copyProperties(dto,q);

                    q.setCurrent(i);
                    log.info("==================== 开始导出 第" + i + " 页 , size : " + page.getSize());
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        long begin = System.currentTimeMillis();
                        Page<R> innerPage = function.apply(q);
                        log.info(Thread.currentThread().getName() + "执行第 " + q.getCurrent() + "  页 ,  耗时 ：" + (System.currentTimeMillis() - begin) + "毫秒");
                        result.addAll(innerPage.getRecords());
                    },batchExportJobPool);
                    futures.add(future);
                }

                try {
                    for (CompletableFuture future : futures) {
                        future.get(timeout, unit);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

