//package com.awy.common.util;
//
//
//
//public class DateUtilTest {
//
//    public static void main(String[] args) {
//        /*int length = 10000;
//        int[] arr = new int[length];
//        for (int i = 0; i < 10000;i++){
//            arr[i] = i;
//        }
//
//        binarySearch(arr,0);*/
//
////        System.out.println(Math.log(2));
////        System.out.println(Math.log(10));
//
////        位运算(&)效率要比代替取模运算(%)高很多，主要原因是位运算直接对内存数据进行操作，不需要转成十进制，因此处理速度非常快。
//
//        //& 同位为1就是1 否则就是0
////        System.out.println(10 & 2);   //1010 0010   & >  0010    >    2
////        System.out.println(2 & 2);    //0010 0010   & >  0010    >    2
////        System.out.println(2 & 3);    //0010 0011   & >  0010    >    2
////        System.out.println(5 & 3);    //0101 0011   & >  0001    >    1
////        System.out.println(20 & 8);    //0101 0011   & >  0001    >    1
////        System.out.println(18 & 8);    //0101 0011   & >  0001    >    1
//
//
//        //^   异或运算符   同位相同为0   否则为1
////        System.out.println(2 ^ 4);
//        System.out.println();
//
//        //  |  相同位只要有一个为1则为1 否则为0
//
//        // ~ 非运算符 二进制取反
////        System.out.println(~1); //
//    }
//
//   /* public static int binarySearch(int[] array,int value){
//        int low = 0;
//        int high = array.length - 1;
//        int middle = 0;
//        //退出条件(低位大于高位)
//        int index = 0;
//        while (low <= high){
//            middle = low +  ((high - low) >> 1);
//            if(array[middle] == value){
//                return middle;
//            }else if(array[middle] > value) {
//                high = middle -1;
//            }else {
//                low = middle + 1;
//
//            }
//
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>" + index++);
//        }
//        return - 1;
//    }*/
//
//
//    public static int binarySearch(int[] array,int value){
//        return binarySearch(array,array.length -1 ,0,value);
//    }
//
//    public static int binarySearch(int[] array,int high,int low,int value){
//        if(low > high){
//            return -1;
//        }
//        System.out.println("11111");
//        int middle = low +  ((high - low) >> 1);
//        if(array[middle] > value){
//            return binarySearch(array, middle -1,low,value);
//        }else if(array[middle] < value){
//            return binarySearch(array, high,middle + 1,value);
//        }
//        return middle;
//    }
//
///*   public static int binarySearch(int[] array,int value){
//       int high = array.length;
//       int low = 0;
//       int mid = 0;
//       while (high > low){
//           mid = (high + low) >> 1;
//           if(array[mid] > value){
//               high = mid;
//           }else if(array[mid] < value){
//                low = ++mid;
//           }else {
//               return mid;
//           }
//       }
//       return -1;
//   }*/
//
//}
