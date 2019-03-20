import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class Oblig3 {
	Lock laas =	new ReentrantLock();
	Lock utlaas = new ReentrantLock();
	public int n = 100;
	public Oblig3Precode writer;
	public int threads = 4;
	TreeMap<Long, ArrayList<Long>> seqFactors = new TreeMap<Long, ArrayList<Long>>(); 
	TreeMap<Long, ArrayList<Long>> paraFactors = new TreeMap<Long, ArrayList<Long>>(); 
	CyclicBarrier cb;
	byte[] byteArray;

	Long[] base = new Long[100];

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Correct usage: java Oblig3 [N] {Threads}");
			System.exit(0);			
		}		
		Long time1, time2;
		Double t1 = 5.0;
		Double t2 = 5.0;
		Oblig3 ob3 = new Oblig3(Integer.parseInt(args[0]));
		int n = ob3.n;
		SequentialSieve seqSieve = new SequentialSieve(n);
		int[] seqPrims = {1};
		int[] paraPrims = {1};

		for (int i = 0; i < 1; i++) {
			time2 = System.nanoTime();
			paraPrims = ob3.paraEra();
			t2 = (System.nanoTime()-time2)/1000000.0;
		
			time1 = System.nanoTime();
			seqPrims = seqSieve.findPrimes();
			t1 = (System.nanoTime()-time1)/1000000.0;

			System.out.printf("seq time: %f para time: %f \n",t1,t2);
		}
		
		
		//ob3.writeFactors();

		//System.out.println(Arrays.toString(seqPrims));
		//System.out.println(Arrays.toString(paraPrims));
		System.out.println("seqLen " + seqPrims.length + " paraLen " + paraPrims.length);
		System.out.println("We found the same primes: " +  ob3.comparePrimes(seqPrims, paraPrims));
		System.out.printf("seq time: %f para time: %f \n",t1,t2);

		double t3, t4;
		Long time3 = System.nanoTime();
		ob3.paraFactor(seqPrims);
		t3 = (System.nanoTime()-time3)/1000000.0;

		time3 = System.nanoTime();
		ob3.seqFactor(seqPrims);
		t4 = (System.nanoTime()-time3)/1000000.0;




		//ob3.printFactors(ob3.paraFactors);
		//ob3.printFactors(ob3.seqFactors);
		System.out.println("Ya boi Yeese is in the housEE " + ob3.compareFactors());
		System.out.println("paraTime " + t3 + " seqTime " + t4);

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
					add_seq_Factor(org, primes[pc]);
					modified = modified / primes[pc];
				}

				else {
					pc++;
					continue;
				}
			}

			if (modified != 1) {
				add_seq_Factor(org, modified);	
			}	
		}
	}

	public void add_seq_Factor(long base, long factor) {
        
        Long longObj = new Long(base);
        
        if(!seqFactors.containsKey(longObj))
            seqFactors.put(longObj, new ArrayList<Long>());

        //System.out.printf("Adding %d to %d\n",factor, base);
        
        seqFactors.get(longObj).add(factor);
        
    }

	public boolean comparePrimes(int[] a , int[]b ) {
		if(a.length != b.length) return false;

		for (int i = 0; i < a.length; i++) {
			if(a[i] != b[i])return false;
		}

		return true;
	}


	public void printFactors(TreeMap<Long, ArrayList<Long>> factors) {
		for(Map.Entry<Long, ArrayList<Long>> entry : factors.entrySet()) {
	   
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


	public boolean compareFactors() {
		Long temp;
		ArrayList<Long> seq, prim;

		for (int i = n*n - 100; i < n * n; i++) {
			temp = new Long(i);

			seq = seqFactors.get(temp);
			prim = paraFactors.get(temp);

			if(seq == null) {
				System.out.println("seq null");
			}

			if(prim == null) {
				System.out.println("prim null " + i);
			}


			if(paraFactors.get(temp).size() != seqFactors.get(temp).size()) {
				System.out.println("different factor size");
				return false;
			}


			seq = seqFactors.get(temp);
			prim = seqFactors.get(temp);
			Collections.sort(prim);
			Collections.sort(seq);
			for (int j = 0; j < prim.size(); j++) {
				if(seq.get(j) != prim.get(j)) {
					System.out.println("Different factors for: " + i);
					return false;
				}
			}
		}


		return true;
	}


	/**
	 *  #####  #######
	 *  #   #  #
	 * 	####   #####
	 * 	#	   #
	 * 	#      ####### 
	 */
	public int[] paraEra() {
		threads = 4;
		int cells = n / 16 + 1;
		byteArray = new byte[cells];
		ArrayList<Integer> primes = new ArrayList<Integer>();
		int currentPrime = 3;
		int squareRootN = (int) Math.sqrt(n);

		while(currentPrime != 0 && currentPrime <= squareRootN) {
			primes.add(currentPrime);
			traverse(currentPrime, squareRootN);
			currentPrime = findNextPrime(currentPrime + 2, squareRootN);
		}
		
		cb = new CyclicBarrier(threads + 1);
		int segLenght = (byteArray.length - squareRootN / 16) / threads;
		EraThread[] era = new EraThread[threads];
		for (int i = 0; i < threads; i++) {
			era[i] = new EraThread(i, segLenght, primes);
			new Thread(era[i]).start();
		}


		try {
			cb.await();
		}

		catch(Exception e) {System.exit(-1);}



		int counter = 1 + primes.size();

		for (int i = 0; i < threads; i++) {
			counter += era[i].ps;
		}

	/* 	  for (int i = 3; i < n; i += 2) {
			if(isPrime(i)) {
				counter++;
			}
		} */
  
		int ret[] = new int[counter];
        ret[0] = 2;

        currentPrime = 3;
        for (int i = 1; i < counter; i++) {
            ret[i] = currentPrime;
            currentPrime = findNextPrime(currentPrime+2,n);
		} 
		return ret;
	}

	void traverse(int p, int high) {
        for (int i = p*p; i <= high; i += p * 2) {
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
        for (int i = startAt; i <= high; i += 2) {
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
		int ps = 0;
		public EraThread(int id, int segLenght, ArrayList<Integer> primes) {
			this.id = id;
			this.segLenght = segLenght;
			this.primes = primes;

			if(id == 0) {
				this.intLow = (int) Math.sqrt(n) + 1;
				if(intLow % 2 == 0) intLow++; 
				low = 0;
			}

			else {
				this.low = segLenght * id;
				intLow = 16 * low + 1;	

			}

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
			//System.out.printf("id %d low %d high %d lowInt %d highInt %d segLenght %d\n", id, low, high, intLow, intHigh, segLenght);



			for (Integer p : primes) {
				par_traverse(p, intLow, intHigh);
			}

			for (int i = intLow; i <= intHigh; i+= 2) {
				if(isPrime(i))ps++;
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
			
			int t = 0;
			if(low % p == 0) t = low; 
			else {	
			t = low + (p - (low % p));
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

		/**
	 *  #####  #######
	 *  #   #  #
	 * 	####   #####
	 * 	#	   #
	 * 	#      #
	 */

	public void paraFactor(int[] primes) {
		threads = 4;
		
		for (int i = n * n - 100; i < n * n; i++) {
			Long l = new Long(i);
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(l);
			paraFactors.put(l,al);
		}

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

		//remove leading 1's if there are any :)
		for(Map.Entry<Long, ArrayList<Long>> entry : paraFactors.entrySet()) {
			if(entry.getValue().get(0) == 1) entry.getValue().remove(0);
		}

	}

	public class Worker implements Runnable{
		int id;
		int[] primes;

		public Worker(int id, int[] primes) {
			this.id = id;
			this.primes = primes;  
		}

		public void addFactor(long base, long factor) {
			laas.lock();
			
			try {

				Long longObj = new Long(base);
 /*        
       			if(!paraFactors.containsKey(longObj)) {
					paraFactors.put(longObj, new ArrayList<Long>());
					paraFactors.get(longObj).add((long) base / factor);
					paraFactors.get(longObj).add(factor);
				   } */

				paraFactors.get(longObj).add(factor);
				paraFactors.get(longObj).set(0, (long) paraFactors.get(longObj).get(0) / factor);
				
				
			}
 
			finally {laas.unlock();}
        	
        	
    	}

		public void run() {
			/* String print = id + ": ";
			for (int i = id; i < primes.length ; i = i + threads) {
				print += primes[i] + " ";
			}
			System.out.println(print);
 */

			for (int i = n * n - 100; i < n * n; i++) {
				long orig = i;
				long modified = i;
				int pc = id;
				while(modified != 1 && pc < primes.length) {

					if (modified % primes[pc] == 0) {
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
