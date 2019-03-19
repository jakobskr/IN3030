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


	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Correct usage: java Oblig3 [N] {Threads}");
			System.exit(0);			
		}		

		Oblig3 ob3 = new Oblig3(Integer.parseInt(args[0]));
		int n = ob3.n;
		SequentialSieve seqSieve = new SequentialSieve(n);
		int[] seqPrims = seqSieve.findPrimes();
		System.out.println(Arrays.toString(seqPrims));
		//ob3.seqFactor(seqPrims);
		//ob3.writeFactors();
		ob3.paraFactor(seqPrims);
	}

	public Oblig3(int n) {
		this.n = n;
		writer = new Oblig3Precode(n);
	} 

	public void writeFactors() {
		writer.writeFactors();
	}

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


	public void paraFactor(int[] primes) {
		threads = 4;

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

	}

}