import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;


public class Oblig4 {
    int threads;
    int seed;
    int n;
    int NUM_BIT = 8; // how many bits in a digit, should be 8-11
    int radixMin = 32; //the min range of when to use radix
    final Oblig4Precode.Algorithm  SEQ =  Oblig4Precode.Algorithm.SEQ;
    final Oblig4Precode.Algorithm  PARA =  Oblig4Precode.Algorithm.PARA;
    int[] original, a, b;
    CyclicBarrier mainBarrier;
    CyclicBarrier synch;
    int[][] allCount;
    int[] sumCount;
    int[] maxArr;

    public Oblig4 (int threads, int seed, int n) {
        this.threads = threads;
        this.seed = seed;
        this.n = n;
        original = Oblig4Precode.generateArray(n, seed);
        a = original.clone();
        b = original.clone();

        //System.out.println(Arrays.toString(original));
    }


    public static void main(String[] args) {
        int n, seed,threads;


        n = Integer.parseInt(args[0]);
        seed = 4;

        Oblig4 sort = new Oblig4(4,seed,n);
        long t1 = System.nanoTime();
        sort.radixSeq();
        double seqTime = (System.nanoTime() - t1) / 1000000.0;
        System.out.printf("time used: %f.4 ms\n", seqTime);


        sort.radixPara();
    }


    public void radixSeq() {
        //find max;
        //find bitmax
        int max = 0;
        a = original.clone();

        //no need to use findMaxSeq here when k == 1
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }

        int bitmax = 0;
        int temp = max;

        while(temp > 0) {
            temp = temp >> 1;
            bitmax++;
        }

        if (NUM_BIT > bitmax) {
            NUM_BIT = bitmax;
        }

        int numDigits = Math.max(1, bitmax / NUM_BIT);
        int[] bit = new int[numDigits];
        int rest = bitmax % NUM_BIT;

        //Divide the parts that we sort on equally.
        for (int i = 0; i < bit.length; i++ ) {
            bit[i] = bitmax / numDigits;
            if (rest-- > 0) bit[i]++;
        }

        System.out.println("max " + max + " bitmax " + bitmax);

        int shift = 0;
        int[] b = new int[n];
        for (int i = 0; i < bit.length;i++) {
            radixSeq(a, b,bit[i], shift);

            shift += bit[i];
            int[] tempArr = a;
            a = b;
            b = tempArr;
        }

        if ((bitmax / NUM_BIT) % 2 != 0) {
            System.arraycopy(a, 0, b, 0, a.length);
        }
        testSort(a);
    }

    public void radixSeq(int[] a, int[] b, int masklen, int shift) {
        int acumVal = 0;
        System.out.println(shift);
        int mask = (1 << masklen) - 1;
        int[] count = new int[mask + 1];
        for (int i = 0; i < a.length; i++) {
            count[(a[i] >> shift) & (mask)]++;
        }

        //System.out.println(Arrays.toString(count));

        int temp = 0;
        for (int i = 0; i < count.length; i++) {
            temp = count[i];
            count[i] = acumVal;
            acumVal += temp;
        }

        //System.out.println(Arrays.toString(count));

        for (int i = 0; i < a.length; i++) {
            b[count[(a[i] >> shift) & (mask)]++] = a[i];
        }
    }

    
    //tatt fra apendix a.
    static public void testSort(int [] a){
      for (int i = 0; i< a.length-1;i++) {
        if (a[i] > a[i+1]){
          System.out.println("SorteringsFEIL på: "+
          i +" a["+i+"]:"+a[i]+" > a["+(i+1)+"]:"+a[i+1]);
          return;
          }
        }
    }

    public void radixPara() {
        a = original.clone();
        threads = 4;
        allCount = new int[threads][];
        mainBarrier = new CyclicBarrier(threads + 1);
        synch = new CyclicBarrier(threads);
        maxArr = new int[threads];

        for (int i = 0; i < threads; i++) {
            new Thread(new RadixPara(i)).start();
        }

        try {
            mainBarrier.await();
        } catch (Exception e) {
            //TODO: handle exception
        }

        int max = 0;
        for (int i = 0; i < threads; i++) {
            if(maxArr[i] > max) max = maxArr[i];
        }

        System.out.println(max);
        //
    }

    public class RadixPara implements Runnable{
        int id;
        int seglenght;
        int start, end;

        public RadixPara(int id) {
            this.id = id;
            seglenght = n / threads;
            start = id * seglenght;
            end = id + seglenght;
            if (id == threads - 1) {
                end = n;
            }
        }

        public void run() {
            findMax();

            try {
                synch.await();
            } catch (Exception e) {int bitmax = 0;
                int temp = max;
        
                while(temp > 0) {
                    temp = temp >> 1;
                    bitmax++;
                }
        
                if (NUM_BIT > bitmax) {
                    NUM_BIT = bitmax;
                }
        
                int numDigits = Math.max(1, bitmax / NUM_BIT);
                int[] bit = new int[numDigits];
                int rest = bitmax % NUM_BIT;
        
                //Divide the parts that we sort on equally.
                for (int i = 0; i < bit.length; i++ ) {
                    bit[i] = bitmax / numDigits;
                    if (rest-- > 0) bit[i]++;
                }
        
                //TODO: handle exception
            }


            int max = 0;
            //who needs synchronisation anyway xd
            for (int i = 0; i < threads; i++) {
                if(maxArr[i] > max) max = maxArr[i];
            }

            radix(max);

            try {
                mainBarrier.await();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }

        public void findMax() {
            int max = 0;
            for (int i = start; i < end; i++) {
                if(a[i] > max) max = a[i];
            }
            maxArr[id] = max;
        }

        public radix(int max) {
            int bitmax = 0;
            int temp = max;
    
            while(temp > 0) {
                temp = temp >> 1;
                bitmax++;
            }
    
            if (NUM_BIT > bitmax) {
                NUM_BIT = bitmax;
            }
    
            int numDigits = Math.max(1, bitmax / NUM_BIT);
            int[] bit = new int[numDigits];
            int rest = bitmax % NUM_BIT;
    
            //Divide the parts that we sort on equally.
            for (int i = 0; i < bit.length; i++ ) {
                bit[i] = bitmax / numDigits;
                if (rest-- > 0) bit[i]++;
                
            }
            
            int shift = 0;
            for (int i = 0; i < bit.length; i++) {
                radix(a, b, bit[i], shift);

                

                try {
                    synch.await();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

        }

        radix(int[] a, int[] b, int masklen, int shift) {

        }

    }

}