import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;


public class Oblig3 {
	public int n = 100;
	public Oblig3Precode writer;
	public int threads = 1;
	TreeMap<Long, LinkedList<Long>> paraFactors = new TreeMap<Long, LinkedList<Long>>(); 
	CyclicBarrier cb;
	byte[] byteArray;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Correct usage: java Oblig3 [N] {Threads}");
			System.exit(0);			
		}		
		Long time1, time2;
		Double t1, t2;
		Oblig3 ob3 = new Oblig3(Integer.parseInt(args[0]));
		int n = ob3.n;
		SequentialSieve seqSieve = new SequentialSieve(n);


		time2 = System.nanoTime();
		int[] paraPrims = ob3.paraEra();
		t2 = (System.nanoTime()-time2)/1000000.0;


		time1 = System.nanoTime();
		int[] seqPrims = seqSieve.findPrimes();
		t1 = (System.nanoTime()-time1)/1000000.0;
		
		//ob3.seqFactor(seqPrims);
		//ob3.writeFactors();
		//ob3.paraFactor(seqPrims);

		System.out.println(Arrays.toString(seqPrims));
		System.out.println(Arrays.toString(paraPrims));

		System.out.println("We found the same primes: " +  ob3.comparePrimes(seqPrims, paraPrims));
		System.out.printf("seq time: %f para time: %f \n",t1,t2);

	}

	public Oblig3(int n) {
		this.n = n;
		writer = new Oblig3Precode(n);
	} 

	public void writeFactors() {
		writer.writeFactors();
	}

	//TODO edit this to store factors locally first.
	public void seqFactor(int[] primes) {
		for (long i = n*n - 100; i < n * n; i++) {
			long org = i;
			long modified = i;
			int pc = 0;
			
			while(modified != 1 && pc < primes.length) {
				if (modified % primes[pc] == 0) {
					writer.addFactor(org, primes[pc]);
					modified = modified / primes[pc];
				}

				else {
					pc++;
					continue;
				}
			}

			if (modified != 1) {
				writer.addFactor(i, modified);	
			}	
		}
	}

	public boolean comparePrimes(int[] a , int[]b ) {
		if(a.length != b.length) return false;

		for (int i = 0; i < a.length; i++) {
			if(a[i] != b[i])return false;
		}

		return true;
	}

	public int[] paraEra() {
		threads = 8;
		int cells = n / 16 + 1;
		byteArray = new byte[cells];
		ArrayList<Integer> primes = new ArrayList<Integer>();
		//finding first primes
		int currentPrime = 3;
		int squareRootN = (int) Math.sqrt(n);

		while(currentPrime != 0 && currentPrime <= squareRootN) {
			primes.add(currentPrime);
			traverse(currentPrime, squareRootN);
			currentPrime = findNextPrime(currentPrime + 2, squareRootN);
		}
		
		cb = new CyclicBarrier(threads + 1);
		int segLenght = byteArray.length / threads;
		for (int i = 0; i < threads; i++) {
			new Thread(new EraThread(i, segLenght, primes)).start();
		}

		try {
			cb.await();
		}

		catch(Exception e) {System.exit(-1);}

		int counter = 0;
		for (int i = 3; i < n; i += 2) {
			if(isPrime(i))counter++;
		}


		int ret[] = new int[counter + 1];
        ret[0] = 2;

        currentPrime = 3;
        for (int i = 1; i < counter; i++) {
            ret[i] = currentPrime;
            currentPrime = findNextPrime(currentPrime+2,n);
        }
		return ret;
	}

	void traverse(int p, int high) {
        for (int i = p*p; i < high; i += p * 2) {
            flip(i);
        }
	}
	
	void flip(int i) {
        if (i % 2 == 0) {
            return;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        byteArray[byteCell] |= (1 << bit);
    }


	int findNextPrime(int startAt, int high) {
        for (int i = startAt; i < high; i += 2) {
            if(isPrime(i)) {
                return i;
            }
        }
        return 0;
    }

	boolean isPrime(int i) {
        if((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        return (byteArray[byteCell] & (1 << bit)) == 0;
	}


	
	public class EraThread implements Runnable {
		int id, low, high, segLenght, intLow, intHigh;
		ArrayList<Integer> primes;

		public EraThread(int id, int segLenght, ArrayList<Integer> primes) {
			this.id = id;
			this.segLenght = segLenght;
			this.primes = primes;
			this.low = segLenght * id;
			intLow = 16 * low + 1;

			if(id == threads -1) {
				high = byteArray.length;
				intHigh = n;
			}

			else {
				this.high = low + segLenght;
				intHigh = 16 * (high - 1) + 15;
			}

		}

		

		public void run() {
			//System.out.printf("id %d low %d high %d lowInt %d highInt %d\n", id, low, high, intLow, intHigh);

			for (Integer p : primes) {
				par_traverse(p, intLow, intHigh);
			}

			try {
				cb.await();
			}
			catch(Exception e) {return;}
		}


		//finds the first valid start point to be crossed out
		//need help with some math
		//low is the lowest int value represented in the byte array that the thread holds, same for high
		//
		public void par_traverse(int p, int low, int high) {
			if(p * p > high)return;			
			int t;
			if((low%p == 0) && (low != p) && (low%2 != 0)) t = low; 
			else {	
			t = (low + p) / p * p;
			if (t % 2 == 0) t += p; 
			}
			int byteCell;
       		int bit;
			for (int i = t; i <= high; i+= p * 2) {
				byteCell = i / 16;
				bit = (i / 2) % 8;
				byteArray[byteCell] |= (1 << bit);
			}
		}
	}


	public void paraFactor(int[] primes) {
		threads = 4;


		/* TODO
		find first M primes, for M*M < N
		then give the list of primes to the threads
		and then let them go through a subsegment of the array and flip the bytes */

		cb = new CyclicBarrier(threads + 1);

		for (int i = 0;i < threads ; i++) {
			new Thread(new Worker(i, primes)).start();
		}

		try {
			cb.await();
		}
		catch(Exception e) {return;}

		 for(Map.Entry<Long, LinkedList<Long>> entry : paraFactors.entrySet()) {
       
                // Starting a new line with the base
                System.out.print(entry.getKey() + " : ");
                
                
                // Sort the factors
                Collections.sort(entry.getValue());
                
                // Then print the factors
                String out = "";
                for(Long l : entry.getValue())
                    out += l + "*";
                
                // Removing the trailing '*'
                System.out.println(out.substring(0, out.length()-1));
                
                
            }

	}




	public class Worker implements Runnable{
		int id;
		int[] primes;

		public Worker(int id, int[] primes) {
			this.id = id;
			this.primes = primes;  
		}

		public synchronized void addFactor(long base, long factor) {
        
        	Long longObj = new Long(base);
        
       		if(!paraFactors.containsKey(longObj))
            	paraFactors.put(longObj, new LinkedList<Long>());

        		//System.out.printf("Adding %d to %d\n",factor, base);
        
        		paraFactors.get(longObj).add(factor);
        	
    	}

		public void run() {
			String print = id + ": ";
			for (int i = id; i < primes.length ; i = i + threads) {
				print += primes[i] + " ";
			}
			System.out.println(print);


			for (int i = n * n - 10; i < n * n; i++) {
				long orig = i;
				long modified = i;
				int pc = id;
				while(modified != 1 && pc < primes.length) {

					if (modified % primes[pc] == 0) {
						System.out.println(id + " " + orig + " " + primes[pc]);
						addFactor(orig, primes[pc]);
						modified = modified / primes[pc];
					}

					else {
						pc += threads;
					}

				}
			}

			try {
				cb.await();
			}
			catch(Exception e) {return;}
		}

		public void factorize() {
			return;
		}

	}

}