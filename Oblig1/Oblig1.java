import java.util.Random;
import java.util.Arrays;
public class Oblig1 {
	public static void main(String[] args) {
		System.out.println("Hello");
		task1();
	}


	public static void task1() {
		int n = 25;
		int[] a = new int[n];
		int k = 5;
		Random random = new Random();


		System.out.println("");

		for (int i = 0;i < n ;i++ ) {
			a[i] = random.nextInt(n);
		}

		System.out.println("before: " + Arrays.toString(a));

		insertSort(a, k);

		System.out.println("after: " + Arrays.toString(a));


		int tmp;
		for (int j = k; j < a.length; j ++) {
			if (a[j] > a[k - 1]) {
				tmp = a[j];
				a[j] = a[k - 1];
				a[k - 1] = tmp;
				insertSort(a, k);
			}
		}

		System.out.println("after: " + Arrays.toString(a));

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
}