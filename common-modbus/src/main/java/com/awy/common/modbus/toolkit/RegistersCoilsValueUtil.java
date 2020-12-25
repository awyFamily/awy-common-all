package com.awy.common.modbus.toolkit;

import io.netty.buffer.ByteBuf;

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

/*    public static void main(String[] args) {
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf original = allocator.directBuffer();
//        original.writeZero(8);
        original.writeByte(3);
        original.writeByte(5);
//        List<Integer> result = getCoilsStatus(original);
//        List<Integer> result = getCoilsStatus(original,20);
        List<Integer> result = getRegistersValues(original);
        System.out.println(result.toString());
    }*/
}
