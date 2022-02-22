package com.awy.data;

import cn.hutool.core.lang.UUID;
import com.awy.common.util.model.BasePageDTO;
import com.awy.data.kit.SqlBatchKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yhw
 * @date 2022-02-22
 */
public class SqlBatchKitTest {

    public void batchQueryPageTest() {
        CopyOnWriteArrayList<PageVO> result = new CopyOnWriteArrayList<>();
        PageDTO dto = new PageDTO();
        dto.setSize(10);
        dto.setCurrent(1);

        SqlBatchKit.batchQueryPage(query -> {
            Page<PageVO> page = new Page(query.getCurrent(), query.getSize(), 112);
            List<PageVO> vos = new ArrayList<>();
            PageVO vo;
            for (int i = 0; i < 5 ; i++) {
                vo = new PageVO();
                vos.add(vo);
            }
            page.setRecords(vos);
            return page;
        },dto,result);
    }

    @Data
    public static class  PageVO  {
        private LocalDateTime localDateTime;
        private String uid;

        public PageVO() {
            this.localDateTime = LocalDateTime.now();
            this.uid = UUID.randomUUID().toString();
        }
    }

    @Data
    public static class  PageDTO extends BasePageDTO {

    }
}
