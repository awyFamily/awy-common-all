package com.yhw.nc.common.excel;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

public class MultiSheetUtil extends AbstractExcelUtil<MultiSheetImportVO> {

    @Override
    public List<Map<String, Object>> readData(Workbook workbook, String[] columns) {
        return null;
    }

    @Override
    public List<MultiSheetImportVO> readData(Workbook workbook, String[] columns, Class<MultiSheetImportVO> clazz) {
        return null;
    }

    @Override
    public void writeData(Sheet sheet,CellStyle style,List<MultiSheetImportVO> datas, String[] columns) {
        MultiSheetImportVO vo;
        Row row;
        for (int i = 0; i < datas.size() ; i++) {
            vo = datas.get(i);
            row = sheet.createRow((i + 1));

            //第一列sn号
            setCellGBKValue(style,row.createCell(0, CellType.STRING),vo.getSn());
            if(CollectionUtil.isNotEmpty(vo.getDetailsSort())){
                int size = vo.getDetailsSort().size();
                MultiSheetImportVO.Detail detail;
                for(int j = 0 ; j < size ;j++){
                    detail = vo.getDetailsSort().get(j);
                    setCellGBKValue(style,row.createCell((j+1), CellType.STRING),detail.getData().concat("/").concat(String.valueOf(detail.getNumber())));
                }
            }
        }

    }

    @Override
    protected void writeCustomizeData(Workbook workbook, List<MultiSheetImportVO> datas, String[] titles, Integer[] widths) {
        Sheet sheet;
        Row row;
        CellStyle style = getCellStyle(workbook,IndexedColors.GREEN,HorizontalAlignment.CENTER,VerticalAlignment.CENTER);
        for (MultiSheetImportVO detail : datas) {
            sheet = createSheet(workbook, detail.getSn());
            if(widths != null){
                setColumnWidth(sheet,widths);
            }

            //创建表头
            createTitleHead(sheet,style,titles);
            //第一行数据
            row = sheet.createRow(1);
            setCellGBKValue(style,row.createCell(0, CellType.STRING),detail.getSn());
            //正常天数
            setCellGBKValue(style,row.createCell(1, CellType.STRING),String.valueOf(detail.getDetails().size()));
            //异常天数
            setCellGBKValue(style,row.createCell(2, CellType.STRING),"0");

            //空两行

            //第四行（对应excel 下标5行）
            row = sheet.createRow(4);
            setCellGBKValue(style,row.createCell(0, CellType.STRING),"详细信息");

            //第五行
            row = sheet.createRow(5);
            setCellGBKValue(style,row.createCell(0, CellType.STRING),"日期");
            setCellGBKValue(style,row.createCell(1, CellType.STRING),"条数");

            //变量明细
            if(CollectionUtil.isNotEmpty(detail.getDetailsSort())){
                int size = detail.getDetailsSort().size();
                MultiSheetImportVO.Detail inner;
                for(int j = 0 ; j < size ;j++){
                    //从0开始，所有起始值为6
                    row = sheet.createRow(6+j);
                    inner = detail.getDetailsSort().get(j);
                    setCellGBKValue(style,row.createCell((0), CellType.STRING),inner.getData());
                    setCellGBKValue(style,row.createCell((1), CellType.STRING),String.valueOf(inner.getNumber()));
                }
            }
        }

    }

}
