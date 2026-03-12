package timsort;

import java.util.Arrays;

public class Timsort {

	// stackSize 40 85 arası 
public static class RunStack {
    private static final int MAX_STACK = 65; 
    
    int[] runStart;
    int[] runLen;
    int stackSize;

    public RunStack() {
        this.runStart = new int[MAX_STACK];
        this.runLen = new int[MAX_STACK];
        this.stackSize = 0;
    }

    public void push(int start, int len) {
        runStart[stackSize] = start;
        runLen[stackSize] = len;
        stackSize++;
    }

    int runLenAt(int i) {
        return runLen[i];
    }
    public void pop() {
        stackSize--;
    }
    public int size() { return stackSize;}
    
    public void removeAt(int idx) {
        if (idx < 0 || idx >= stackSize) throw new IndexOutOfBoundsException();
        for (int i = idx; i < stackSize - 1; i++) {
            runStart[i] = runStart[i+1];
            runLen[i] =runLen[i + 1] ;
        }
        stackSize--;
    }
    public void setRun(int idx, int base, int len) {
        runStart[idx] = base;
        runLen[idx] = len;
    }
    public int runStartAt(int idx) {
        return runStart[idx];
    }
}

// belirli bir aralığı insertion sort ile sıralama.(aralıklar dahil)
public static void insertionSort(int[] arr,int i1, int i2) {
		for(int i=i1+1 ; i<i2+1; i++) {
			if(arr[i] >= arr[i-1]) continue;
			int comp = arr[i];
			int comp2 = i-1;
			
			while(comp2>=i1 && arr[comp2] > comp) {
				arr[comp2 + 1]= arr[comp2];
				comp2--;
			}
			arr[comp2+1]=comp;
		}
	
}

// belirli bir aralığı ters çevirme.(aralıklar dahil)
public static void reverseInRange(int[] arr, int left, int right) {
	while(right>left) {
		int temp = arr[right];
		arr[right]= arr[left];
		arr[left]= temp;
		left++;
		right--;
	}
}

// merge sort'un optimize çalışması için uygun olan minRun sayısını hesaplama
public static int minRunLen(int n) {
	
    int remainder = 0;
    while(n >= 64) {
        if(n % 2 == 1) {remainder = 1;}

        n = n / 2;
    }

    return (n+remainder);
}
// run tespiti
/**
 * @param arr   - sıralanacak dizi
 * @param start - run aramaya başlanacak index
 * @param end   - son index (dahil)
 * @return run'ın uzunluk
*/
static int countRunAndMakeAscending(int[] arr, int start, int end) {
    // start son eleman ise 1 elemanlık run
    if (start >= end) return 1;

    int i = start + 1;

    // azalan run
    if (arr[i] < arr[start]) {
        // azaldıkça devam
        while (i <= end && arr[i] < arr[i - 1]) {
            i++;
        }
        // azalan olduğu için ters çevir
        reverseInRange(arr, start, i - 1);
      
    } 
    // artan run
    else {
        // arttığı sürece devam et
        while (i <= end && arr[i] >= arr[i - 1]) {
            i++;
        }
    }
    return i - start;
}


public static void timSortStart(int[] arr) {
    int n = arr.length;
    if (n < 2) return;                     // tek elemansa zaten sıralı

    int minRun = minRunLen(n);          
    RunStack runStack = new RunStack();    

    int i = 0;
    while (i < n) {
        // run tespiti
        int runLen = countRunAndMakeAscending(arr, i, n - 1);

        // minRun boyutuna büyütme
        if (runLen < minRun) {
            int force = Math.min(minRun, n - i);
            insertionSort(arr, i, i + force - 1);  // insertion sort ile sıralama
            runLen = force;
        }

        // run hazır, stack'e pushla
        runStack.push(i, runLen);

        mergeCollapse(runStack, arr);
        i += runLen;
    }

    // tek run kalana kadar merge 
    mergeForceCollapse(runStack, arr);
}



// mergeCollapse push sonrası çağrılır
static void mergeCollapse(RunStack stack, int[] arr) {
    while (stack.size() > 1) {
        int n = stack.size();

        int A = (n >= 3) ? stack.runLenAt(n - 3) : Integer.MAX_VALUE;
        int B = stack.runLenAt(n - 2);
        int C = stack.runLenAt(n - 1);

        if (n >= 3 && A <= B + C) {
            // choose which runs to merge
            if (A < C) {
                mergeAt(stack, n - 3, arr);
            } else {
                mergeAt(stack, n - 2, arr);
            }
        } else if (B <= C) {
            mergeAt(stack, n - 2, arr);
        } else {
            // rule satisfied
            break;
        }
    }
}

// runlar bitene kadar merge et
static void mergeForceCollapse(RunStack stack, int[] arr) {
    while (stack.size() > 1) {
        int n = stack.size();
        // en sağ çifti merge et (n-2, n-1) 
        mergeAt(stack, n - 2, arr);
    }
}

// runları merge sort ile sıralama işlemi
static void mergeAt(RunStack stack, int k, int[] arr) {
    int base1 = stack.runStartAt(k);
    int len1 = stack.runLenAt(k);
    int base2 = stack.runStartAt(k + 1);
    int len2 = stack.runLenAt(k + 1);

   
    if (base1 + len1 != base2) {
        throw new IllegalStateException("Runs are not neighbor: base1=" + base1 + " len1=" + len1 + " base2=" + base2);
    }

    // sol run kopyası
    int[] left = Arrays.copyOfRange(arr, base1, base1 + len1);
    int i = 0;                    
        int j = base2;                 
    int dest = base1;              
    int leftEnd = len1;
    int rightEnd = base2 + len2;

    while (i < leftEnd && j < rightEnd) {
        if (left[i] <= arr[j]) {   
            arr[dest++] = left[i++];
        } else {
            arr[dest++] = arr[j++];
        }
    }
    while (i < leftEnd) {
        arr[dest++] = left[i++];
    }

    stack.setRun(k, base1, len1 + len2);
    stack.removeAt(k + 1);
}
static void printArr(int arr[])
{
    int n = arr.length;
    for (int i = 0; i < n; ++i) {
        System.out.print(arr[i] + " ");
    }
    System.out.println();
}

// TEST
public static void main(String[] args) {
	
	int[] arr = {5, 2, 9, 1, 5, 6, 7, 3, 4, 8, 0, 11, 10, 13, 12};
	System.out.print("Asıl array: " );
    printArr(arr);
    timSortStart(arr);
    System.out.print("Sıralanmış array : " );
    printArr(arr);




	}}

