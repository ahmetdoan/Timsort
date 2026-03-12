package timsort;

import java.util.Arrays;
import java.util.Random;

public class TimsortBenchmark {
	static int[] randomArr(int size) {
		int[] arr=new int[size];
		Random rand = new Random();
		
		for(int i=0; i<size ; i++) {
			arr[i] = rand.nextInt(0,100000);
		}
		
		return arr;
	}
	
	static int[] closeToSortedArr(int size, double ratio) {
		int[] arr=new int[size];
		
		Random rand = new Random();
		
		int disruptNum= (int)(size * ratio);
		for(int i=0; i<size ; i++) {
			arr[i] = rand.nextInt(0,100000);
		}
		Timsort.timSortStart(arr);
		for(int i=0; i<disruptNum ; i++) {
			int s1= rand.nextInt(0,size-1);
			int s2= rand.nextInt(0,size-1);
			int temp= arr[s1];
			arr[s1]= arr[s2];
			arr[s2]= temp;
		}
		
		return arr;
	}
//ignore first 20 time calculation for Max Min and Average(JVM warm-up)
static long getMaxforBench(long[] arr) {
	long max= arr[20];
	for(int i=20; i<arr.length; i++) {
		if(arr[i]> max) {
			max=arr[i];
		}
	}
	
	return max;
}
static long getMinforBench(long[] arr) {
	long min= arr[20];
	for(int i=20; i<arr.length; i++) {
		if(arr[i]< min) {
			min=arr[i];
		}
	}
	
	return min;
}
static long getAverageforBench(long[] arr) {
	long sum= 0;
	for(int i=20; i<arr.length; i++) {
		sum+=arr[i];
	}
	
	return (sum / (arr.length-20));
}
	public static void main(String[] args) {
		int size = 100;// ilk 20 run JVM warm-up için yok sayılacak ve son 80 run'ın ortalaması alınacak.
		long[] timeGap1= new long[size];
		long[] timeGap11= new long[size];
		long[] timeGap2= new long[size];
		long[] timeGap22= new long[size];
		long[] timeGap3= new long[size];
		long[] timeGap33= new long[size];

		for(int i=0; i<size;i++) {
			
			int[] arr1 = randomArr(10000);
			int[] arr11 = Arrays.copyOf(arr1, 10000);
			
			int[] arr2 = randomArr(100000);
			int[] arr22 = Arrays.copyOf(arr2, 100000);
			
			int[] arr3 = randomArr(500000);
			int[] arr33= Arrays.copyOf(arr3, 500000);;
			
			
			// size 10000
			long start1= System.nanoTime();
			Timsort.insertionSort(arr1, 0, 10000-1);
			long end1 = System.nanoTime();
			long start11= System.nanoTime();
			Timsort.timSortStart(arr11);
			long end11 = System.nanoTime();
			
			
			// size 100000
			long start2= System.nanoTime();
			Timsort.insertionSort(arr2, 0, 100000-1);
			long end2 = System.nanoTime();
			long start22= System.nanoTime();
			Timsort.timSortStart(arr22);
			long end22 = System.nanoTime();
			
			// size 500000
			long start3= System.nanoTime();
			Timsort.timSortStart(arr33);
			long end3 = System.nanoTime();
			
			int[] arr = closeToSortedArr(500000, 0.01);
			long start33= System.nanoTime();
			Timsort.timSortStart(arr);
			long end33 = System.nanoTime();
			
			
			timeGap1[i] =end1 - start1;
			timeGap11[i] = end11- start11;
			timeGap2[i] = end2- start2;
			timeGap22[i] =end22- start22;
			timeGap3[i] = end3- start3;
			timeGap33[i] = end33- start33;
		}
		// size 10000
		System.out.println("For an array contains 10000 element:\n");
		System.out.println("Insertion sort: ");
		System.out.println("Max:"+getMaxforBench(timeGap1)/ 1e6+"\n"+
		"Min:"+getMinforBench(timeGap1)/ 1e6+"\n"+
		"Average:"+getAverageforBench(timeGap1)/ 1e6);

		System.out.println("\nFor an array contains 10000 element:");
		System.out.println("Timsort:\n ");
		System.out.println("Max:"+getMaxforBench(timeGap11)/ 1e6+"\n"+
		"Min:"+getMinforBench(timeGap11)/ 1e6 +"\n"+
		"Average:"+getAverageforBench(timeGap11)/ 1e6+ "\n--------------------------------------------------");

		
		// size 100000
		System.out.println("For an array contains 100000 element:\n");
		System.out.println("Insertion sort: ");
		System.out.println("Max:"+getMaxforBench(timeGap2)/ 1e6+"\n"+
		"Min:"+getMinforBench(timeGap2)/ 1e6+"\n"+
		"Average:"+getAverageforBench(timeGap2)/ 1e6+"\n");
		
		System.out.println("\nFor an array contains 100000 element:\n");
		System.out.println("Timsort: ");
		System.out.println("Max:"+getMaxforBench(timeGap22)/ 1e6+"\n"+
		"Min:"+getMinforBench(timeGap22)/ 1e6+"\n"+
		"Average:"+getAverageforBench(timeGap22)/ 1e6);

		
		// size 500000
		System.out.println("--------------------------------------------------\n\n"
							+ "How Timsort performs if array is close to sorted?\n"+
							"For an array contains 500000 element:");
		
		
		System.out.println("\nTimsort on unsorted arr: ");
		System.out.println("Max:"+getMaxforBench(timeGap3)/ 1e6+"\n"+
		"Min:"+getMinforBench(timeGap3)/ 1e6+"\n"+
		"Average:"+getAverageforBench(timeGap3)/ 1e6);
		
		System.out.println("\nTimsort on mostly sorted arr: ");
		System.out.println("Max:"+getMaxforBench(timeGap33)/ 1e6 +"\n"+
		"Min:"+getMinforBench(timeGap33)/ 1e6 +"\n"+
		"Average:"+getAverageforBench(timeGap33)/ 1e6 );
		
		
		
	}

}
