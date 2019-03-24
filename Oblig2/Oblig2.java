import java.util.Arrays;
import java.util.concurrent.*;


public class Oblig2 {
	int n = 0;
	int seed;
	double[][] a;
	double[][] b;
	double[][] a_transposed;
	double[][] b_transposed;
	double[][] seq;
	double[][] seqA;
	double[][] seqB;
	double[][] para;
	double[][] paraA;
	double[][] paraB;

	
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
			ma.multiply_par(Oblig2Precode.Mode.PARA_NOT_TRANSPOSED);
			time[3][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			ma.multiply_par(Oblig2Precode.Mode.PARA_A_TRANSPOSED);
			time[4][i] = (System.nanoTime()-t)/1000000.0;
			
			t = System.nanoTime();
			ma.multiply_par(Oblig2Precode.Mode.PARA_B_TRANSPOSED);
			time[5][i] = (System.nanoTime()-t)/1000000.0;
			System.out.println("------ finished loop " + i +" -------");

		}

		for (int i = 0; i < 6; i++) {
			Arrays.sort(time[i]);
			System.out.println("i: " + i + " time: " + time[i][3]);
		}

		ma.writeMatrix();
		System.out.println("Same result for para and seq: " + ma.compare(ma.para, ma.seq));

	}

	public Oblig2(int n, int seed) {
		this.n = n;
		this.seed = seed;
		a = Oblig2Precode.generateMatrixA(seed, n);
		b = Oblig2Precode.generateMatrixB(seed, n);
		//fillMatrix(a);
		//fillMatrix(b);

		seq = new double[n][n];
		seqA = new double[n][n];
		seqB = new double[n][n];
		para = new double[n][n];
		para = new double[n][n];
		paraB = new double[n][n];
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

	public void writeMatrix() {
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, seq);
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_A_TRANSPOSED, seqA);
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_B_TRANSPOSED, seqB);
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, para);
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_A_TRANSPOSED, paraA);
		Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_B_TRANSPOSED, paraB);
	}


	public void multiply_seq() {
		seq = new double[n][n];

		for (int i = 0;i < n; i++ ) {
			for (int j = 0;j < n; j++) {
				for (int k = 0; k < n ; k++) {
					seq[i][j] += a[i][k] * b[k][j];
				}
			}
		}
	}

	public void multiply_seq_transB() {
		double[][] newB = transpose(b);
		seqB = new double[n][n];
		for (int i = 0; i < n ; i++) {
			for (int j = 0; j < n ; j++) {
				for (int k = 0; k < n ;k++ ) {
					seqB[i][j] += a[i][k] * newB[j][k];
				}
			}
		}
	}

	public void multiply_seq_transA() {
		double[][] newA = transpose(a);
		seqA = new double[n][n];
		for (int i = 0; i < n ; i++) {
			for (int j = 0; j < n ; j++) {
				for (int k = 0; k < n ;k++ ) {
					seqA[i][j] += newA[k][i] * b[k][j];
				}
			}
		}
		printMatrix(seqA);
	}

	public void fillMatrix(double[][] in) {
		int k = 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				in[i][j] = k++;
			}
		}
	}

	public double[][] transpose(double[][] in) {
		double[][] ret = new double[n][n];
		for (int i = 0;i < n ;i++ ) {
			for (int j = 0; j < n; j++ ) {
				ret[i][j] = in[j][i];
			}
		}
		//System.out.println("this is the result of the transposed :)");
		//printMatrix(out); 
		return ret;
	}

	public void multiply_par(Oblig2Precode.Mode mode) {
		threads = 4;
		cb = new CyclicBarrier(threads + 1);

		if (mode == Oblig2Precode.Mode.PARA_A_TRANSPOSED) {
			paraA = new double[n][n];
			a_transposed = transpose(a);
		}

		else if (mode == Oblig2Precode.Mode.PARA_B_TRANSPOSED) {
			paraB = new double[n][n];
			b_transposed = transpose(b);
		}

		else {
			para = new double[n][n];
		}

		for (int i = 0; i < threads; i++) {
			new Thread(new Worker(i, mode)).start(); 
		}
		try {
			cb.await();
		}
		catch(Exception e) {return;}
		//compare(out);
		//printMatrix(out);
	}

	public boolean compare(double[][] comp, double[][] correct) {
		int x = 0;
		for (int i = 0;i < n ; i++) {
			for (int j = 0; j < n; j++) {
				if (comp[i][j] - correct[i][j] > 0.0001 || correct[i][j] - comp[i][j] > 0.0001) {
					return false;
				}
			}
		}
		return true;
	} 


	public class Worker implements Runnable {
		int index;
		int start, end;
		Oblig2Precode.Mode mode;

		public Worker (int ind, Oblig2Precode.Mode which) {
			index = ind;
			start = index * (n / threads);
			this.mode = which;
			if (index == threads - 1) {
				end = start + n / threads + n % threads;
			}

			else {
				end = start + n / threads;
			}
			//System.out.println(ind +  " " + start + " " + end + " " + (end - start));
		}

		public void run() {
			if (mode == Oblig2Precode.Mode.PARA_NOT_TRANSPOSED) {
				para_multiply();
			}

			else if (mode == Oblig2Precode.Mode.PARA_A_TRANSPOSED) {
				para_multiply_transA();
			}

			else if (mode == Oblig2Precode.Mode.PARA_B_TRANSPOSED) {
				para_multiply_transB();
			}
		}

		public void para_multiply() {
			for (int i = start; i < end;i++ ) {
				for (int j = 0; j < n ; j++) {
					for (int k = 0;k < n ;k++ ) {
						para[i][j] += a[i][k] * b[k][j];
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
						paraA[i][j] += a_transposed[k][i] * b[k][j];
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
						paraB[i][j] += a[i][k] * b_transposed[j][k];
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