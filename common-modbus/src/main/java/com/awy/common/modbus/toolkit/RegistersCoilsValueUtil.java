package com.awy.common.modbus.toolkit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author yhw
 */
public final class RegistersCoilsValueUtil {

    /**
     * 获取寄存器值列表
     * @param byteBuf buf
     * @return 寄存器值列表
     */
    public static List<Integer> getRegistersValues(ByteBuf byteBuf){
        if(byteBuf == null){
            return new ArrayList<>();
        }
        int readable = byteBuf.readableBytes();
        int readerIndex = byteBuf.readerIndex();
        List<Integer> result = new ArrayList<>();
        while (readable > readerIndex){
            result.add(byteBuf.readUnsignedShort());
            readerIndex = byteBuf.readerIndex();
        }
        return result;
    }

    /**
     * 获取线圈状态列表
     * @param byteBuf buf
     * @param coilsNumber 获取线圈数量
     * @return 线圈状态列表
     */
    public static List<Integer> getCoilsStatus(ByteBuf byteBuf,Integer coilsNumber){
        List<Integer> result = getCoilsStatus(byteBuf);
        if(result == null || result.isEmpty()){
            return result;
        }
        int arraySize = result.size();
        if(arraySize <= coilsNumber){
            return result;
        }
        return result.subList(0,coilsNumber);
    }

    /**
     * 获取线圈状态列表
     * @param byteBuf buf
     * @return 线圈状态列表
     */
    public static List<Integer> getCoilsStatus(ByteBuf byteBuf){
        if(byteBuf == null){
            return new ArrayList<>();
        }
        Stack<Integer> stack = new Stack<>();
        int readable = byteBuf.readableBytes();
        int readerIndex = byteBuf.readerIndex();
        for (int i = readerIndex; i < readable; i++) {
            stack.add((int)byteBuf.readUnsignedByte());
        }
        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()){
            result.addAll(getBinaryList(stack.pop()));
        }
        return getListReverse(result);
    }

    private static List<Integer> getBinaryList(Integer number){
        if(number == null){
            return new ArrayList<>();
        }
        String binaryStr = toBinaryString(Integer.toBinaryString(number));
        List<Integer> binaryNumbers = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            binaryNumbers.add(Integer.valueOf(binaryStr.substring(i,(1+i))));
        }
        return binaryNumbers;
    }

    /**
     * 不够位数的在前面补0，保留code的长度位数字
     * @param code
     * @return
     */
    private static String toBinaryString(String code) {
        return String.format("%08d", Integer.valueOf(code));
    }


    //====================================================

    /**
     * 获取寄存器值ByteBuf
     * @param values 寄存器值列表
     * @return 寄存器值 ByteBuf
     */
    public static ByteBuf getWriteRegistersValues(short... values){
        if(values == null || values.length <= 0){
            return null;
        }
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf original = allocator.directBuffer();
        for (short value : values) {
            original.writeShort(value);
        }
        return original;
    }

    /**
     * 获取线圈值ByteBuf
     * @param status 寄存器值列表(0-关 1-开)
     * @return 线圈值 ByteBuf
     */
    public static ByteBuf getWriteCoilsStatus(int... status){
        if(status == null || status.length <= 0){
            return null;
        }
        //0000 0011  0000 0001(source)
        //0000 0001   0000 0011 (result : High and low swap)
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf original = allocator.directBuffer();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(int i = 0; i < status.length; i++){
            count++;
            sb.append(status[i] == 0 ? 0 : 1);
            if(i == (status.length - 1)){
                original.writeByte(Integer.parseInt(toBinaryString(sb.reverse().toString()),2));
                break;
            }

            if(count%8 == 0){
                original.writeByte(Integer.parseInt(sb.reverse().toString(),2));
                count = 0;
                sb = new StringBuilder();
            }
        }
        return original;
    }

    //============= list Reverse =========================
    /**
     * 将列表反转
     * @param list 原列表
     * @param end 截取的长度
     * @return  截取列表反转
     */
    private static List<Integer> getListReverse(List<Integer> list,int end){
        int forEnd = list.size() - end;
        List<Integer> result = new ArrayList<>();
        for (int i = ( list.size() - 1); i >= forEnd; i--) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 将列表反转
     * @param list 原列表
     * @return  列表反转
     */
    private static List<Integer> getListReverse(List<Integer> list){
        List<Integer> result = new ArrayList<>();
        for (int i = ( list.size() - 1); i >= 0; i--) {
            result.add(list.get(i));
        }
        return result;
    }

 /*   public static void main(String[] args) {
//        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
//        ByteBuf original = allocator.directBuffer();
////        original.writeZero(8);
//        original.writeByte(3);
//        original.writeByte(5);
////        List<Integer> result = getCoilsStatus(original);
////        List<Integer> result = getCoilsStatus(original,20);
//        List<Integer> result = getRegistersValues(original);
//        System.out.println(result.toString());


        //0000 0011  0000 0001
        ByteBuf writeCoilsStatus = getWriteCoilsStatus(1, 1, 0, 0,   0, 0, 0, 0,   1, 0, 1,0, 1);
        System.out.println(writeCoilsStatus.readableBytes());
        System.out.println(Integer.toHexString(writeCoilsStatus.readUnsignedByte()));
        System.out.println(Integer.toHexString(writeCoilsStatus.readUnsignedByte()));
//        System.out.println(getCoilsStatus(writeCoilsStatus).toString());
    }*/
}
