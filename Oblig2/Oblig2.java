import java.util.Arrays;
import java.util.concurrent.*;


public class Oblig2 {
	int n = 0;
	double[][] a;
	double[][] b;
	double[][] out;
	double[][] a_transposed;
	double[][] b_transposed;
	int threads;
	CyclicBarrier cb;

	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		int seed = Integer.parseInt(args[1]);
		execute(n,seed);
		//ma.transpose(ma.a);
	}

	public static void execute(int n, int seed) {
		Oblig2 ma = new Oblig2(n, seed);
		Long t;
		Double[][] time = new Double[6][7];
		for (int i = 0;i < 7 ;i ++ ) {
			
			//stantad seq
			t = System.nanoTime();
			ma.multiply_seq();
			time[0][i] = (System.nanoTime()-t)/1000000.0;

			//seq a transposed
			t = System.nanoTime();
			//ma.multiply_seq_transA();
			time[1][i] = (System.nanoTime()-t)/1000000.0;
			
			//seq b transposed
			t = System.nanoTime();
			//ma.multiply_seq_transB();
			time[2][i] = (System.nanoTime()-t)/1000000.0;
			
			//para standard
			t = System.nanoTime();
			ma.multiply_par();
			time[3][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			//task2();
			time[4][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			//task2();
			time[5][i] = (System.nanoTime()-t)/1000000.0;
		}

		for (int i = 0; i < 4; i++) {
			Arrays.sort(time[i]);
			System.out.println("i: " + i + " time: " + time[i][4]);
		}


	}

	public Oblig2(int n, int seed) {
		this.n = n;
		a = Oblig2Precode.generateMatrixA(seed, n);
		b = Oblig2Precode.generateMatrixB(seed, n);
		fillmeupdaddy(a);
		fillmeupdaddy(b);

		out = new double[n][n];
		printMatrix(a);
		printMatrix(b);
		transpose(a);
	}

	public void printMatrix(double[][] tbp) {
		if (n > 5) return;
		System.out.println(" ");
		for (int i = 0;i < n ;i++) {
			System.out.println("[ " + Arrays.toString(tbp[i]) + "]");
		}
	}



	public void multiply_seq() {
		
		for (int i = 0;i < n; i++ ) {
			for (int j = 0;j < n; j++) {
				for (int k = 0; k < n ; k++) {
					out[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		//printMatrix(out);
	}

	public void multiply_seq_transB() {
		double[][] newB = transpose(b);
		double[][] out = new double[n][n];
		for (int i = 0; i < n ; i++) {
			for (int j = 0; j < n ; j++) {
				for (int k = 0; k < n ;k++ ) {
					out[i][j] += a[i][k] * newB[j][k];
				}
			}
		}
		System.out.println("this is the result of the transposed :)");
		printMatrix(out);
	}

	public void multiply_seq_transA() {
		double[][] newA = transpose(a);
		double[][] out = new double[n][n];
		for (int i = 0; i < n ; i++) {
			for (int j = 0; j < n ; j++) {
				for (int k = 0; k < n ;k++ ) {
					out[i][j] += a[k][i] * newA[k][j];
				}
			}
		}
		System.out.println("this is the result of the transposed :)");
		printMatrix(out);
	}

	public void fillmeupdaddy(double[][] in) {
		int k = 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				in[i][j] = k++;
			}
		}
	}

	public double[][] transpose(double[][] in) {
		double[][] out = new double[n][n];
		for (int i = 0;i < n ;i++ ) {
			for (int j = 0; j < n; j++ ) {
				out[i][j] = in[j][i];
			}
		}
		printMatrix(out); 
		return out;
	}

	public void multiply_par() {
		out = new double[n][n];
		threads = 4;
		cb = new CyclicBarrier(threads + 1);

		for (int i = 0; i < threads; i++) {
			new Thread(new Worker(i)).start(); 
		}
		try {
			cb.await();
		}
		catch(Exception e) {return;}
		printMatrix(out);
	}


	public class Worker implements Runnable {
		int index;
		int start, end;

		public Worker (int ind) {
			index = ind;
			start = index * (n / threads);
			if (index == threads - 1) {
				end = start + n / threads + n % threads;
			}

			else {
				end = start + n / threads;
			}
			System.out.println(ind +  " " + start + " " + end + " " + (end - start));
		}

		public void run() {
			for (int i = start; i < end;i++ ) {
				for (int j = 0; j < n ; j++) {
					for (int k = 0;k < n ;k++ ) {
						out[i][j] += a[i][k] * b[k][j];
					}
				}
			}
			try {
				cb.await();
			}
			catch(Exception e) {return;}
		}
	}
}