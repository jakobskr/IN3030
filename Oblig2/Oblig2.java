import java.util.Arrays;
import java.util.concurrent.*;


public class Oblig2 {
	int n = 0;
	double[][] a;
	double[][] b;
	double[][] out;
	double[][] correct;
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
			System.out.println("------ started loop " + i +" --------");
			//stantad seq
			t = System.nanoTime();
			ma.multiply_seq();
			time[0][i] = (System.nanoTime()-t)/1000000.0;
 
			//seq a transposed
			t = System.nanoTime();
			ma.multiply_seq_transA();
			time[1][i] = (System.nanoTime()-t)/1000000.0;
			
			//seq b transposed
			t = System.nanoTime();
			ma.multiply_seq_transB();
			time[2][i] = (System.nanoTime()-t)/1000000.0;
			
			//para standard
			t = System.nanoTime();
			ma.multiply_par(0);
			time[3][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			ma.multiply_par(1);
			time[4][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			ma.multiply_par(2);
			time[5][i] = (System.nanoTime()-t)/1000000.0;
			System.out.println("------ finished loop " + i +" -------");

		}

		for (int i = 0; i < 6; i++) {
			Arrays.sort(time[i]);
			System.out.println("i: " + i + " time: " + time[i][4]);
		}


	}

	public Oblig2(int n, int seed) {
		this.n = n;
		a = Oblig2Precode.generateMatrixA(seed, n);
		b = Oblig2Precode.generateMatrixB(seed, n);
		//fillmeupdaddy(a);
		//fillmeupdaddy(b);

		out = new double[n][n];
		printMatrix(a);
		printMatrix(b);
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
		printMatrix(out);
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
		printMatrix(out);
	}

	public void multiply_seq_transA() {
		double[][] newA = transpose(a);
		double[][] out = new double[n][n];
		for (int i = 0; i < n ; i++) {
			for (int j = 0; j < n ; j++) {
				for (int k = 0; k < n ;k++ ) {
					out[i][j] += newA[k][i] * b[k][j];
				}
			}
		}
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
		//System.out.println("this is the result of the transposed :)");
		//printMatrix(out); 
		return out;
	}

	public void multiply_par(int which) {
		out = new double[n][n];
		threads = 4;
		cb = new CyclicBarrier(threads + 1);

		if (which == 1) {
			a_transposed = transpose(a);
		}

		else if (which == 2) {
			b_transposed = transpose(b);
		}

		for (int i = 0; i < threads; i++) {
			new Thread(new Worker(i, which)).start(); 
		}
		try {
			cb.await();
		}
		catch(Exception e) {return;}
		printMatrix(out);
	}


	public class Worker implements Runnable {
		int index;
		int start, end, which;

		public Worker (int ind, int which) {
			index = ind;
			start = index * (n / threads);
			this.which = which;
			if (index == threads - 1) {
				end = start + n / threads + n % threads;
			}

			else {
				end = start + n / threads;
			}
			//System.out.println(ind +  " " + start + " " + end + " " + (end - start));
		}



		public void run() {
			if (which == 0) {
				para_multiply();
			}

			else if (which == 1) {
				para_multiply_transA();
			}

			else if (which == 2) {
				para_multiply_transB();
			}
		}

		public void para_multiply() {
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

		public void para_multiply_transA() {
			for (int i = start; i < end;i++ ) {
				for (int j = 0; j < n ; j++) {
					for (int k = 0;k < n ;k++ ) {
						out[i][j] += a_transposed[k][i] * b[k][j];
					}
				}
			}
			try {
				cb.await();
			}
			catch(Exception e) {return;}
		}

		public void para_multiply_transB() {
			for (int i = start; i < end;i++ ) {
				for (int j = 0; j < n ; j++) {
					for (int k = 0;k < n ;k++ ) {
						out[i][j] += a[i][k] * b_transposed[j][k];
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