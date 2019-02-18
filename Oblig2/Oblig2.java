import java.util.Arrays;

public class Oblig2 {
	int n = 0;
	double[][] a;
	double[][] b;
	double[][] out;
	public static void main(String[] args) {
		Oblig2 ma = new Oblig2(2, 32);
		ma.multiply();
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
	}

	public void printMatrix(double[][] tbp) {
		System.out.println(" ");
		for (int i = 0;i < n ;i++) {
			System.out.println("[ " + Arrays.toString(tbp[i]) + "]");
		}
	}



	public void multiply() {
		
		for (int i = 0;i < n; i++ ) {
			for (int j = 0;j < n; j++) {
				for (int k = 0; k < n ; k++) {
					out[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		printMatrix(out);
	}

	public void fillmeupdaddy(double[][] in) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				in[i][j] = i * j + 1;
			}
		}
	}

}