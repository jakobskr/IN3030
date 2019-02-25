import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
public class Oblig1 {
	int n = 0;
	int[] a = new int[n];
	int[] b = new int[n];
	int k = 20;
	CyclicBarrier cb;


	public static void main(String[] args) {
		Oblig1 task = new Oblig1();
		task.k = 20;
		for (int i = 1000; i < 1000000000 ; i= i * 10) {
			task.does(i,20);

		}
		
		System.out.println(" ");

		for (int i = 1000; i < 1000000000 ; i= i * 10) {
			task.does(i,100);
		}
	}

	public void does(int nx, int kx) {
		n = nx;
		k = kx;
		Double[] seq = new Double[7];
		Double[] para = new Double[7];

		Random random = new Random();
		for (int j = 0; j < 7; j++) {
		a = new int[n];
		b = new int[n];

			for (int i = 0;i < n ;i++ ) {
				int d = random.nextInt(n);
				a[i] = d;
				b[i] = d;
			}

		Long t2 = System.nanoTime();
		task2();
		para[j] = (System.nanoTime()-t2)/1000000.0;

		Long t1 = System.nanoTime();
		task1();
		seq[j] = (System.nanoTime()-t1)/1000000.0;
		
		}
		Arrays.sort(seq);
		Arrays.sort(para);
		System.out.printf("N: %d k: %d Seq: %f Para:%f S: %f\n", n, k, seq[3], para[3], seq[3]/para[3]);

	}


	public void task1() {
		
		insertSort(b, k);
		//System.out.println("before: " + Arrays.toString(b));

		int tmp;
		for (int j = k; j < b.length; j++) {
			if (b[j] > b[k - 1]) {
				tmp = b[j];
				b[j] = b[k - 1];
				b[k - 1] = tmp;
				insertSort(b, k, 0);
			}
		}

		//System.out.println("seq: " + Arrays.toString(b));
	}

	public void task2() {
		int threads = 4;
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
		}

		workers[threads - 1] = new Worker(start, end + workSegment % threads);
		for (int i = 0; i < threads ;i ++ ) {
			new Thread(workers[i]).start(); 
		}

		insertSort(a, k);

		try {
		cb.await();
		}
		catch(Exception e){return;}

		for (Worker w: workers) {
			for (int j = w.start;j < w.end; j++ ) {
				if (a[j] > a[k - 1]) {
					tmp = a[j];
					a[j] = a[k - 1];
					a[k - 1] = tmp;
					insertSort(a,k,0);
				}

				else break;
			}
		}

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
		//TODO: Find out why this is so slow; 
		int tmp;
		for (int j = start + k - 1; j > start; j-- ) {
			if (a[j] < a[j - 1]) {
				return;
			}
			else {
				tmp = a[j];
				a[j] = a[j - 1];
				a[j - 1] = tmp;
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
		for (int j = start; j < ind; j++ ) {
			int i = j;
			while(i >= start && a[i] < a[i + 1]) {
				tmp = a[i];
				a[i] = a[i + 1];
				a[i + 1] = tmp;
				i--; 
			}
		}

		for (int j = ind + 1; j < end; j++) {
			if (a[j] > a[ind]) {
				tmp = a[j];
				a[j] = a[ind];
				a[ind] = tmp;
				
				//insertSort(a, k, start);
				for (int l = start + k - 1; l > start; l-- ) {
					if (a[l] < a[l - 1]) {
						break;
					}
					else {
						tmp = a[l];
						a[l] = a[l - 1];
						a[l - 1] = tmp;
					}
				}
			}
			//System.out.println(start + "  " + j);
		}
			try{
			cb.await();
			} catch(Exception e) {return;}
		}
		
	}
}   