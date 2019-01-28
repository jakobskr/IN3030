import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
public class Oblig1 {
	int n = 2000000;
	int[] a = new int[n];
	int[] b = new int[n];
	int k = 100;
	CyclicBarrier cb;



	public static void main(String[] args) {
		Random random = new Random();
		for (int j = 0; j < 7; j++) {
			Oblig1 t = new Oblig1();
			for (int i = 0;i < t.n ;i++ ) {
				int d = random.nextInt(t.n);
				t.a[i] = d;
				t.b[i] = d;
			}

		
		//System.out.println("before: " + Arrays.toString(t.b));

		Long t2 = System.nanoTime();
		t.task2();
		Double time2 = (System.nanoTime()-t2)/1000000.0;

		Long t1 = System.nanoTime();
		t.task1();
		Double time1 = (System.nanoTime()-t1)/1000000.0;
		
		
		System.out.printf("Seq: %f Para:%f S: %f\n", time1, time2, time1/time2);
		}

	}


	public void task1() {
		
		insertSort(b, k);


		int tmp;
		for (int j = k; j < b.length; j++) {
			if (b[j] > b[k - 1]) {
				tmp = b[j];
				b[j] = b[k - 1];
				b[k - 1] = tmp;
				insertSort(b, k);
			}
		}

		//System.out.println("seq: " + Arrays.toString(b));
	}

	public void task2() {
		int threads = 3;
		Worker[] workers = new Worker[threads];
		int workSegment = a.length - k;
		int segmentLenght = workSegment / threads;
		int start = k;
		int end = start + segmentLenght;
		int tmp;
		//System.out.printf("Threads: %d N: %d k: %d s1: %d e1: %d\n" ,threads,n,k,start,end);

 		cb = new CyclicBarrier(threads + 1);


		for (int i = 0; i < threads - 1;i ++ ) {
			workers[i] = new Worker(start, end);
			start = end;
			end = end + segmentLenght;
			new Thread(workers[i]).start();
		}

		workers[threads - 1] = new Worker(start, end + workSegment % threads);
		new Thread(workers[threads - 1]).start(); 
		insertSort(a, k);

		try {
		cb.await();
		}
		catch(Exception e){return;}

		for (Worker w: workers) {
			for (int j = w.start;j < w.start + k; j++ ) {
				if (a[j] > a[k - 1]) {
					tmp = a[j];
					a[j] = a[k - 1];
					a[k - 1] = tmp;
					insertSort(a,k);
				}

				else break;
			}
		}
		//System.out.println(Arrays.toString(a));

	}

	public static void insertSort(int a[], int k) {
		for (int j = 0; j < k - 1; j++ ) {
			int i = j;
			int tmp;
			while(i >= 0 && a[i] < a[i + 1]) {
				tmp = a[i];
				a[i] = a[i + 1];
				a[i + 1] = tmp;
				i--; 
			}
		}
	}

	public void insertSort(int a[], int k, int start) {
		for (int j = start; j < start + k - 1; j++ ) {
			int i = j;
			int tmp;
			while(i >= 0 && a[i] < a[i + 1]) {
				tmp = a[i];
				a[i] = a[i + 1];
				a[i + 1] = tmp;
				i--; 
			}
		}
	}

	class Worker implements Runnable {

		int min, start, end;
		public int[] maxk = new int[k];
		int ind = k - 1;
		int temp;
		public Worker(int start, int end) {
			this.start = start;
			this.end = end;

			//System.out.println(start + " " + end);
		}

		public void run() {
		int tmp;
		int ind = start + k - 1;
		for (int j = start; j < start + k - 1; j++ ) {
			int i = j;
			while(i >= 0 && a[i] < a[i + 1]) {
				tmp = a[i];
				a[i] = a[i + 1];
				a[i + 1] = tmp;
				i--; 
			}
		}

		for (int j = ind; j < end - 1; j++) {
			if (a[j] > a[ind]) {
				tmp = a[j];
				a[j] = a[ind];
				a[ind] = tmp;
				
				insertSort(a, k, start);
			}
			//System.out.println(start + "  " + j);
		}
			try{
			cb.await();
			} catch(Exception e) {return;}
		}
		
	}
}   