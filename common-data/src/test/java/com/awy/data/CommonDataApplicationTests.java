package com.awy.data;


import com.awy.data.model.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public class CommonDataApplicationTests {

	public void contextLoads() {
//		Wrappers.<BaseEntity>lambdaUpdate().
        Wrappers.<BaseEntity>lambdaQuery().eq(BaseEntity::getRemarks,"");
	}

	public static void main(String[] args) {
		int[] array = new int[]{1,2,3,5,6, 7,8,9,12,13, 15,17,22,33,54, 65,66,67,68,69,70,80,90};
//		int[] array = new int[]{1,2,3};
		System.out.println(array.length);
		System.out.println(binarySearch(array,array.length,0,0));

		//8
		//7  9


		//17 + 7 = 24  17 +9+26
		//12
//		System.out.println(binarySearch(array,array.length - 1,0,8));
	}

	public static int binarySearch(int[] array,int value){
		return binarySearch(array,array.length,0,value);
	}

	public static int binarySearch(int[] array,int length,int end,int value){
		if(length <=  end ){
			return -1;
		}
		int mid = (length + end) / 2;
		//数组值比 当前值大 -> 数组则要缩小一半(length = mid)
		if(array[mid] > value){
			 return binarySearch(array,mid,end,value);
		}else if(array[mid] < value){
			//数组值比 当前值小 -> 数组范围则向前移动一半 (end = mid + 1)
			 return binarySearch(array,length,++mid,value);
		}
		return mid;
	}
}
