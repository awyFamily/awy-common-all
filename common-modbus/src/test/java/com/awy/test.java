package com.awy;

import com.digitalpetri.modbus.codec.utils.CRC;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public class test {

    public static void main(String[] args) {
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf original = allocator.directBuffer(32);

        original.writeByte(2);
        original.writeByte(5);
        original.writeShort(0);
        original.writeShort(0xFF00);

       System.out.println(original.readerIndex());
       System.out.println(original.readableBytes());
        original.readerIndex(3); //剩下还有3，起始是3，剩下可读的是3
       System.out.println(original.readerIndex());
       System.out.println(original.readableBytes());
//       System.out.println(original.readerIndex());
        original.readerIndex(6); //剩下还有3，起始是3，剩下可读的是3
        System.out.println(original.readerIndex());
        System.out.println(original.readableBytes());


    }

    public static void main1(String[] args) {
        //02 05 0000  FF00      8C09
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf original = allocator.directBuffer(32);
//        original.writeZero(8);
        original.writeByte(2);
        original.writeByte(5);
//        original.writeByte(0x00);
//        original.writeByte(0x00);
//        original.writeByte(0xFF);
//        original.writeByte(0x00);
        original.writeShort(0);
        original.writeShort(0xFF00);

        int readableBytes = original.readableBytes();//返回可读的字节数
        int startIndex = original.readerIndex();
        System.out.println(readableBytes);
        System.out.println(startIndex);
        int end = startIndex + readableBytes;
        System.out.println(end);
        int[] data = new int[end];
        int crc = 0;
        //数据是以补码的形式存储的，正数的补码是本身，负数的补码是除符号位外其它位取反，
        // 再加上1，1111 1111 是 补码，转换成原码是 1000 0001，就是 -1.
        for (int i = startIndex;i < end;i++){
            crc = original.getUnsignedByte(i);
            System.out.println("------------- " + i + "    data : " + crc);
            data[i] = crc;
        }

        String s = CRC.crc16(data);
        System.out.println(s);

        ///写入最后两位
        Integer d8 = Integer.valueOf(s, 16);
        System.out.println("d8: " + d8);
        original.writeShort(d8);

        System.out.println(original.getByte(0));
        System.out.println(original.getByte(1));
        System.out.println(original.getShort(2));
        System.out.println(original.getUnsignedShort(4));
        System.out.println(original.getUnsignedShort(6));
    }


    /**
     * 读线圈
     */
    public void getReadTestCoils(){
        //sn 线圈起始地址  数量
        //url = url.concat("/readCoils").concat("/YB8A20430000000003").concat("/1").concat("/11");

        //response: 2, 1, 2, 3, 5, 3d, f
        //3(0000 0011)   5(0000 0101) ->  0000 0101 0000 0011 (对应 1,2,9,11 水阀打开)
        //1 和 3 交互位置 ，从后往前数
    }

    /**
     * 读寄存器
     */
    public void  getReadHoldingRegisters(){
        ///readHoldingRegisters/{snNumber}/{address}/{number}
        //url = url.concat("/readHoldingRegisters").concat("/YB8A20430000000003").concat("/605").concat("/2");
        //2, 3, 2, 5d, 0, 2, 54, 52  -> 2, 3, 4, 0, a, 0, c, e9, 34


        //request： 02 03 02 5D 00 02 54 52  (02 5D )起始地址  (00 02)寄存器数量
        //response: 02 03 04 00 0A 00 0C E9 34   (04) 字节数   (00 0A) 寄存器值1 (00 0C)寄存器值2
        //02 5D -> (00 0A)     02 5E ->  00 0C  -->  (D605 10   D606 12)
    }
}
