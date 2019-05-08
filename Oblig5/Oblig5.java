
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

public class Oblig5 {
    int MIN_X = 1;
    int MAX_X = 100;
    int MIN_Y = 1, MAX_Y = 100;
    int calls = 0;
    int n;
    int threads;
    int[] x, y;
    IntList[] paraPoints;
    IntList paraList, sekList;
    int[] arrMin;
    int[] arrMax;
    int paraMin;
    int paraMax;

    CyclicBarrier cb;
    int test = 0;

    public Oblig5(int n, int[] x, int[] y, int threads) {
        this.n = n;
        this.threads = threads;
        this.x = x;
        this.y = y;

    }

    public String printPoints() {
        String ret = "";

        for (int i = 0; i < n; i++) {
            ret += "<" + x[i] + "," + y[i] + "> ";
        }

        return ret;
    }

    // this is a bad way of doing things, since i calculate the line formel to many
    // times xd. has to change at some point!!!!!

    public double sekvMetode() {

        // System.out.println(distance(3, 4, 7, 8, 1, 1) + " " + distance(7, 8, 3, 4, 1,
        // 1));
        long t1 = System.nanoTime();
        IntList koHyll = new IntList(15);
        IntList rightPoints = new IntList(4);
        IntList leftPoints = new IntList(4);

        int maxPos = 0;
        int minPos = 0;
        int nextLeft = -1;
        int nextRight = -1;

        for (int i = 1; i < n; i++) {
            if (x[i] > x[maxPos])
                maxPos = i;
            if (x[i] < x[minPos])
                minPos = i;
        }

        double furthestL = 0.1;
        double furthestR = 0.1;
        int ar = y[maxPos] - y[minPos];
        int br = x[minPos] - x[maxPos];
        int cr = y[minPos] * x[maxPos] - y[maxPos] * x[minPos];

        int al = y[minPos] - y[maxPos];
        int bl = x[maxPos] - x[minPos];
        int cl = y[maxPos] * x[minPos] - y[minPos] * x[maxPos];

        for (int i = 0; i < n; i++) {
            if (i == maxPos || i == minPos)
                continue;
            double d = (double) ((ar * x[i] + br * y[i] + cr));

            if (d <= 0) {
                rightPoints.add(i);

                if (d < furthestR) {
                    nextRight = i;
                    furthestR = d;
                }
            }

            double dl = (double) ((al * x[i] + bl * y[i] + cl));

            if (dl <= 0) {
                leftPoints.add(i);

                if (dl < furthestL) {
                    nextLeft = i;
                    furthestL = dl;
                }
            }

        }

        koHyll.add(maxPos);
        if (nextRight != -1) {
            newRec(maxPos, minPos, nextRight, rightPoints, koHyll);
        }

        koHyll.add(minPos);

        if (nextLeft != -1) {
            newRec(minPos, maxPos, nextLeft, leftPoints, koHyll);
        }

        long time2 = System.nanoTime();

        MAX_X = x[0];
        MIN_X = x[0];
        MAX_Y = y[0];
        MIN_Y = y[0];

        for (int i = 0; i < n; i++) {
            if (x[i] < MIN_X)
                MIN_X = x[i];
            if (x[i] > MAX_X)
                MAX_X = x[i];
            if (y[i] < MIN_Y)
                MIN_Y = y[i];
            if (y[i] > MAX_Y)
                MAX_Y = y[i];

        }

        if (n < 500) {
            System.out.println("sekv kohyll: " + koHyll + " " + koHyll.len);
            new TegnUt(this, koHyll);
        }

        else {
            System.out.println("sekv len: " + koHyll.len);
        }


        sekList = koHyll;
        return (double) (time2 - t1) / 1000000.0;

    }

    public void newRec(int p1, int p2, int p3, IntList m, IntList koHyll) {

        int ar = y[p1] - y[p3];
        int br = x[p3] - x[p1];
        int cr = y[p3] * x[p1] - y[p1] * x[p3];

        int al = y[p3] - y[p2];
        int bl = x[p2] - x[p3];
        int cl = y[p2] * x[p3] - y[p3] * x[p2];

        IntList rightPoints = new IntList(3);
        IntList leftPoints = new IntList(3);
        int nextLeft = -1;
        int nextRight = -1;
        double furthestR = 0;
        double furthestL = 0;

        for (int i = 0; i < m.len; i++) {
            if (m.get(i) == p1 || m.get(i) == p2 || m.get(i) == p3) {
                continue;
            }

            double d = (double) ((ar * x[m.get(i)] + br * y[m.get(i)] + cr));
            if (d <= 0) {

                if (d < 0)
                    rightPoints.add(m.get(i));

                else if (d == 0 && isBetween(p1, p3, m.get(i))) {
                    rightPoints.add(m.get(i));
                    if (nextRight == -1)
                        nextRight = m.get(i);
                }

                if (d < furthestR) {
                    nextRight = m.get(i);
                    furthestR = d;
                }
            }

            double dl = (double) ((al * x[m.get(i)] + bl * y[m.get(i)] + cl));

            if (dl <= 0) {

                if (dl < 0)
                    leftPoints.add(m.get(i));

                else if (dl == 0 && isBetween(p3, p2, m.get(i))) {
                    leftPoints.add(m.get(i));
                    if (nextLeft == -1)
                        nextLeft = m.get(i);
                }

                if (dl < furthestL) {
                    nextLeft = m.get(i);
                    furthestL = dl;
                }
            }

        }

        if (nextRight != -1) {
            newRec(p1, p3, nextRight, rightPoints, koHyll);
        }

        koHyll.add(p3);

        if (nextLeft != -1) {
            newRec(p3, p2, nextLeft, leftPoints, koHyll);
        }

    }

    public boolean isBetween(int p1, int p2, int i) {
        int p1x = x[p1];
        int p1y = y[p1];
        int p2x = x[p2];
        int p2y = y[p2];
        int ix = x[i];
        int iy = y[i];

        if (p1x < ix && ix < p2x || p1x > ix && ix > p2x) {
            return true;
        }

        if (p1y < iy && iy < p2y || p1y > iy && iy > p2y) {
            return true;
        }
        return false;
    }

    public double paraKohyll() {

        // System.out.println("Here we heckin' go");

        long time1 = System.nanoTime();
        paraPoints = new IntList[threads];
        IntList leftPoints = new IntList(3);
        IntList rightPoints = new IntList(3);
        IntList koHyll = new IntList(3);
        cb = new CyclicBarrier(threads + 1);

        arrMax = new int[threads];
        arrMin = new int[threads];

        int maxPos = 0;
        int minPos = 0;
        int nextLeft = -1;
        int nextRight = -1;


        for (int i = 0; i < threads; i++) {
            new Thread(new Worker1(i)).start();
        }

        try {
            cb.await();
        } catch (Exception e) {
            // TODO: handle exception
        }

        IntList m = new IntList(6);
        for(int i = 0; i < threads; i++) {
        	m.append(paraPoints[i]);
        	if(x[arrMax[i]] > x[maxPos]) maxPos = arrMax[i]; 
        	if(x[arrMin[i]] < x[minPos]) minPos = arrMin[i];

        }


        double furthestL = 0.1;
        double furthestR = 0.1;
        int ar = y[maxPos] - y[minPos];
        int br = x[minPos] - x[maxPos];
        int cr = y[minPos] * x[maxPos] - y[maxPos] * x[minPos];

        int al = y[minPos] - y[maxPos];
        int bl = x[maxPos] - x[minPos];
        int cl = y[maxPos] * x[minPos] - y[minPos] * x[maxPos];

        for (int i = 0; i < m.len; i++) {
            if (m.get(i) == maxPos || m.get(i) == minPos)
                continue;
            double d = (double) ((ar * x[m.get(i)] + br * y[m.get(i)] + cr));

            if (d <= 0) {
                rightPoints.add(m.get(i));

                if (d < furthestR) {
                    nextRight = m.get(i);
                    furthestR = d;
                }
            }

            double dl = (double) ((al * x[m.get(i)] + bl * y[m.get(i)] + cl));

            if (dl <= 0) {
                leftPoints.add(m.get(i));

                if (dl < furthestL) {
                    nextLeft = m.get(i);
                    furthestL = dl;
                }
            }

        }

        koHyll.add(maxPos);
        if (nextRight != -1) {
            newRec(maxPos, minPos, nextRight, rightPoints, koHyll);
        }

        koHyll.add(minPos);

        if (nextLeft != -1) {
            newRec(minPos, maxPos, nextLeft, leftPoints, koHyll);
        }


        
        double ret = (System.nanoTime() - time1) / 1000000.0;





        if (n < 500) {
            System.out.println("para kohyll: " + koHyll + " " + koHyll.len);
            //
            //
            new TegnUt(this, koHyll);

        }

        else {
            System.out.println("para len: " + koHyll.len);
        }

        paraList = koHyll;
        return ret;

    }

    public class Worker1 implements Runnable {
        int id;
        int start;
        int end;
        IntList localKohyll = new IntList(10);

        public Worker1(int id) {
            this.id = id;
            start = (n / threads) * id;
            if (id == threads - 1) {
                end = n;
            }

            else {
                end = start + n / threads;
            }

            // System.out.println(id + " " + start + " " + end);
        }

        public void run() {

        IntList koHyll = new IntList(15);
        IntList rightPoints = new IntList(4);
        IntList leftPoints = new IntList(4);

        int maxPos = 0;
        int minPos = 0;
        int nextLeft = -1;
        int nextRight = -1;

        for (int i = 1; i < n; i++) {
            if (x[i] > x[maxPos])
                maxPos = i;
            if (x[i] < x[minPos])
                minPos = i;
        }

        double furthestL = 0.1;
        double furthestR = 0.1;
        int ar = y[maxPos] - y[minPos];
        int br = x[minPos] - x[maxPos];
        int cr = y[minPos] * x[maxPos] - y[maxPos] * x[minPos];

        int al = y[minPos] - y[maxPos];
        int bl = x[maxPos] - x[minPos];
        int cl = y[maxPos] * x[minPos] - y[minPos] * x[maxPos];

        for (int i = start; i < end; i++) {
            if (i == maxPos || i == minPos)
                continue;
            double d = (double) ((ar * x[i] + br * y[i] + cr));

            if (d <= 0) {
                rightPoints.add(i);

                if (d < furthestR) {
                    nextRight = i;
                    furthestR = d;
                }
            }

            double dl = (double) ((al * x[i] + bl * y[i] + cl));

            if (dl <= 0) {
                leftPoints.add(i);

                if (dl < furthestL) {
                    nextLeft = i;
                    furthestL = dl;
                }
            }

        }

        koHyll.add(maxPos);
        if (nextRight != -1) {
            newRec(maxPos, minPos, nextRight, rightPoints, koHyll);
        }

        koHyll.add(minPos);

        if (nextLeft != -1) {
            newRec(minPos, maxPos, nextLeft, leftPoints, koHyll);
        }


        paraPoints[id] = koHyll;
        arrMax[id] = maxPos;
        arrMin[id] = minPos;


            try {
                cb.await();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }



    public static void main(String[] args) {

        if (args.length < 2) {
            return;
        }

        int n = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        NPunkter17 p = new NPunkter17(n);
        int[] x = new int[n];
        int[] y = new int[n];
        p.fyllArrayer(x, y);

        Oblig5 task = new Oblig5(n, x, y, threads);
        int runs = 7;
        double[] para = new double[runs];
        double[] sekv = new double[runs];;

        for (int i = 0; i < runs; i++) {
            sekv[i] = task.sekvMetode();
            para[i] = task.paraKohyll();
        }

        Arrays.sort(para); Arrays.sort(sekv);

        System.out.println("same points in kohyll: " + same(task.paraList, task.sekList));
       
        System.out.printf("Time for finding Kohyll of %d points using %d threads\nsekvTime: %.2fms paraTime: %.2fms speedup: %.4f\n", n, threads ,sekv[runs/2], para[runs/2], (double) sekv[runs/2] / para[runs/2] ); 

        System.out.println(Arrays.toString(para));
    }


    public static boolean same(IntList a, IntList b) {
        if (a.len != b.len) {
            return false;
        }

        for (int i = 0; i < a.len; i++) {
            if (a.get(i) != b.get(i))
                return false;
        }

        return true;
    }
}