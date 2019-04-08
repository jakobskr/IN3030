import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collections;


public class Oblig4 {
    int threads;
    int seed;
    int n;
    int NUM_BIT = 8; // how many bits in a digit, should be 8-11
    int radixMin = 32; //the min range of when to use radix
    final Oblig4Precode.Algorithm  SEQ =  Oblig4Precode.Algorithm.SEQ;
    final Oblig4Precode.Algorithm  PARA =  Oblig4Precode.Algorithm.PARA;
    int[] a, b, paraA;
    final int[] original;
    CyclicBarrier mainBarrier;
    CyclicBarrier synch;
    int[][] allCount;
    int[] globalCount;
    int[] maxArr;
    int paraMax = 0;
    ReentrantLock maxLock = new ReentrantLock();

    public Oblig4 (int threads, int seed, int n) {
        this.threads = threads;
        this.seed = seed;
        this.n = n;
        original = Oblig4Precode.generateArray(n, seed);
        a = original.clone();
        paraA = original.clone();
        b = new int[n];

        //System.out.println(Arrays.toString(original));
    }


    public static void main(String[] args) {
        int n, seed,threads;

        if(args.length < 2) {
            System.exit(-1);
        }

        n = Integer.parseInt(args[0]);
        seed = Integer.parseInt(args[1]);
       
        Oblig4 sort = new Oblig4(4,seed,n);
        long t1;

        for (int i = 0; i < 7; i++) {
           t1 = System.nanoTime();
           sort.radixSeq();
           double seqTime = (System.nanoTime() - t1) / 1000000.0;
           System.out.printf("seq time used: %f.4 ms\n", seqTime);
           Arrays.fill(sort.b,0);
           t1 = System.nanoTime();
           sort.radixPara();
           double paraTime = (System.nanoTime() - t1) / 1000000.0;
           System.out.printf("para time used: %f.4 ms\n", paraTime);
           Arrays.fill(sort.b,0);

           for (int j = 0; j < n; j++) {
                sort.a[j] = sort.original[j];
                sort.paraA[j] = sort.original[j];
           }
        }
    }


    public void radixSeq() {
        //find max;
        //find bitmax
        int max = 0;
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


        int shift = 0;
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
    }

    public void radixSeq(int[] a, int[] b, int masklen, int shift) {
        int acumVal = 0;
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
    public void testSort(int [] a){
        int[] comp = original.clone();
        Arrays.sort(comp);
      for (int i = 0; i< a.length;i++) {
        if (a[i] != comp[i]){
          System.out.println("SorteringsFEIL på: "+
          i +" a["+i+"] = " + a[i] + " fasit[" + i + "]: " + comp[i]);
            return;
          }
        }
    }

    public void radixPara() {
        paraMax = a[n / 2];
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
        //
        //System.out.println(Arrays.toString(a));

    }


    public void updateMax(int x) {
        maxLock.lock();

        try {
            if(x > paraMax) paraMax = x;
        }

        finally {
            maxLock.unlock();
        }

    }    

    public class RadixPara implements Runnable{
        int id;
        int seglenght;
        int start, end;
        int[] digitOffset;
        int[] count;

        public RadixPara(int id) {
            this.id = id;
            seglenght = n / threads;
            start = id * seglenght;
            end = start + seglenght;
            if (id == threads - 1) {
                end = n;
            }

            //System.out.println(id + " " + start + " " + end);
        }

        public void run() {
            findMax();

            try {
                synch.await();
            } catch (Exception e) {}

            //who needs synchronisation anyway xd. Edit: i ended up using synchronisation xd

            

            radix();

            try {
                mainBarrier.await();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }

        public void findMax() {
            int max = 0;
            if(paraA == null) System.out.println("reee");
            for (int i = start; i < end; i++) {
                if(paraA[i] > max) max = paraA[i];
            }
            if(max > paraMax) {
                updateMax(max);
            }
        }

        public void radix() {
            int bitmax = 0;
            int temp = paraMax;
    

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
            int[] tempArr;
            int shift = 0;
            for (int i = 0; i < bit.length; i++) {

                radix(bit[i], shift);
                //System.out.println(id + " " + bit[i] + " " + shift + " " + bit.length);
                shift += bit[i];
                try {
                    synch.await();
                } catch (Exception e) {
                    //TODO: handle exception
                }

                if(id == 0 ) {
                    tempArr = a;
                    a = b;
                    b = tempArr;
                }
                try {
                    synch.await();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

            
          
        }

        public void radix(int masklen, int shift) {
            int mask = (1 << masklen) - 1;
            count = new int[mask + 1];
            digitOffset = new int[mask + 1];


            for (int i = start; i < end; i++) {
                count[(paraA[i] >> shift) & (mask)]++;
            }

            allCount[id] = count;


            try {
                synch.await();
            } catch (Exception e) {
                //TODO: handle exception
            }

            int sumVal = 0;
            for (int i = 0; i < digitOffset.length; i++) {
                sumVal = 0;
                for (int j = 0; j < i;j++) {
                    for (int t = 0; t < threads; t++) {
                        sumVal += allCount[t][j];
                    }
                }

                for(int r = 0; r < id; r ++) {
                    sumVal += allCount[r][i];
                }
                digitOffset[i] = sumVal;
            }

            
            for (int i = start; i < end; i++) {
                b[digitOffset[(paraA[i] >> shift) & (mask)]++] = paraA[i];
            }

        }

    }

}