
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

public class Oblig5 {
    IntList sekList, paraList;
    int MIN_X = 1;
    int MAX_X = 100;
    int MIN_Y = 1, MAX_Y = 100;
    int calls = 0;
    int n;
    int threads;
    int[] x, y;
    IntList[][] paraPoints;
    int[][] paraFurthest;
    int[] arrMin;
    int[] arrMax;
    double[][] paraDistance;
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
        //IntList koHyll = new IntList(15);
        IntList leftKohyll = new IntList(3);
        IntList rightKohyll = new IntList(3);
        IntList localKohyll = new IntList(3);
        cb = new CyclicBarrier(threads + 1);

        paraPoints = new IntList[threads][];
        paraFurthest = new int[threads][];
        paraDistance = new double[threads][];
        arrMax = new int[threads];
        arrMin = new int[threads];

        int maxPos = 0;
        int minPos = 0;

        Thread t1 = null;
        Thread t2 = null;

        for (int i = 0; i < threads; i++) {
            new Thread(new Worker1(i)).start();
        }

        try {
            cb.await();
        } catch (Exception e) {
            // TODO: handle exception
        }

        paraMin = arrMin[0];
        paraMax = arrMax[0];

        IntList rightPoints = new IntList(10);
        IntList leftPoints = new IntList(10);


        for (int i = 0; i < threads; i++) {
            if (x[arrMax[i]] > x[paraMax])
                paraMax = arrMax[i];
            if (x[arrMin[i]] < x[paraMin])
                paraMin = arrMin[i];
        }

        maxPos = paraMax;
        minPos = paraMin;

        try {
            cb.await();
            cb.await();
        } catch (Exception e) {
            // TODO: handle exception
        }

        int nextRight = paraFurthest[0][0];
        int nextLeft = paraFurthest[0][1];
        double dr = paraDistance[0][0];
        double dl = paraDistance[0][1];


        for (int i = 0; i < threads; i++) {
            rightPoints.append(paraPoints[i][0]);
            leftPoints.append(paraPoints[i][1]);
            if (paraFurthest[i][0] != -1) {

                if (paraDistance[i][0] < dr) {
                    nextRight = paraFurthest[i][0];
                    dr = paraDistance[i][0];
                }
            }

            if (paraFurthest[i][1] != -1) {

                if (paraDistance[i][1] < dl) {
                    nextLeft = paraFurthest[i][1];
                    dl = paraDistance[i][1];
                }
            }
        }

        rightKohyll.add(maxPos);

        Worker2 rightThread = null;
        Worker2 leftThread = null;
        int rightDepth = threads / 2 + threads % 2;
        int leftDepth = threads / 2;
        

       

        if (nextRight != -1) {

            if (rightDepth > 0) {
                rightThread = new Worker2(rightDepth - 1, maxPos, minPos, nextRight, rightPoints);
                t1 = new Thread(rightThread);
                t1.start();
            }

            else {
                newRec(maxPos, minPos, nextRight, rightPoints, rightKohyll);
            }

        }

        leftKohyll.add(minPos);

        if (nextLeft != -1) {
            if (leftDepth > 0) {
                leftThread = new Worker2(rightDepth - 1, minPos, maxPos, nextLeft, leftPoints);
                t2 = new Thread(leftThread);
                t2.start();
            }

            else {
                newRec(minPos, maxPos, nextLeft, leftPoints, leftKohyll);
            }
        }

        localKohyll.append(rightKohyll);

        if (t1 != null) {
            try {
                t1.join();
                localKohyll.append(rightThread.localKohyll);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        localKohyll.append(leftKohyll);

        if (t2 != null) {
            try {
                // System.out.println("beep booop " + leftThread.localKohyll);
                t2.join();
                localKohyll.append(leftThread.localKohyll);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        double ret = (System.nanoTime() - time1) / 1000000.0;
        // System.out.println("para used: " + (System.nanoTime() - time1) / 1000000.0 +
        // " ms");

        paraList = localKohyll;
        if (n < 500) {
            System.out.println("para kohyll: " + localKohyll + " " + localKohyll.len);
            new TegnUt(this, localKohyll);

        }

        else {
            System.out.println("lenght para: " + localKohyll.len);
        }

        return ret;

    }

    public class Worker1 implements Runnable {
        int id;
        int start;
        int end;

        public Worker1(int id) {
            this.id = id;
            start = (n / threads) * id;
            if (id == threads - 1) {
                end = n;
            }

            else {
                end = start + n / threads;
            }

        }

        public void run() {
            int maxPos = start;
            int minPos = start;

            for (int i = start; i < end; i++) {
                if (x[i] > x[maxPos])
                    maxPos = i;
                if (x[i] < x[minPos])
                    minPos = i;
            }

            arrMax[id] = maxPos;
            arrMin[id] = minPos;

            try {
                cb.await();
            } catch (Exception e) {
                // TODO: handle exception
            }



            try {
                cb.await();
            } catch (Exception e) {
                // TODO: handle exception
            }


            //System.out.println(id + " " + paraMax + " " + paraMin +  " help ");

            maxPos = paraMax;
            minPos = paraMin;

            //0 is right 1 is left;
            IntList[] points = new IntList[2];
            int[] furthestPoints = new int[2];
            double[] distance = new double[2];

            IntList rightPoints = new IntList(3);
            IntList leftPoints = new IntList(3);
            int nextRight = -1;
            int nextLeft = -1;
            double furthestL = 0;
            double furthestR = 0;
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

            distance[0] = furthestR;
            distance[1] = furthestL;
            points[0] = rightPoints;
            points[1] = leftPoints;
            furthestPoints[0] = nextRight;
            furthestPoints[1] = nextLeft;
            paraDistance[id] = distance;
            paraFurthest[id] = furthestPoints;
            paraPoints[id] = points;
            
            try {
                cb.await();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }//ends run

    }

    public class Worker2 implements Runnable {
        IntList localKohyll = new IntList(4);
        IntList leftList = new IntList(3);
        IntList rightList = new IntList(3);
        Worker2 leftThread, rightThread;
        int leftDepth, rightDepth;
        int p1, p2, p3;
        IntList m;
        Thread t1 = null;
        Thread t2 = null;
        int id = 0;

        public Worker2(int depth, int p1, int p2, int p3, IntList m) {
            this.id = test++;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.m = m;
            leftDepth = depth / 2;
            rightDepth = (depth / 2) + depth % 2;

        }

        public void run() {
            // System.out.println("thread: " + id + " " + rightDepth + " " + leftDepth + " "
            // + p1 + " " + p2 + " " + p3 + " " + m);
            paraRec(p1, p2, p3, m);
        }

        public void paraRec(int p1, int p2, int p3, IntList m) {
            int ar = y[p1] - y[p3];
            int br = x[p3] - x[p1];
            int cr = y[p3] * x[p1] - y[p1] * x[p3];

            int al = y[p3] - y[p2];
            int bl = x[p2] - x[p3];
            int cl = y[p2] * x[p3] - y[p3] * x[p2];

            IntList rightPoints = new IntList(3);
            IntList leftPoints = new IntList(3);
            IntList rightKohyll = new IntList(3);
            IntList leftKohyll = new IntList(3);
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
                if (rightDepth > 0) {
                    rightThread = new Worker2(rightDepth - 1, p1, p3, nextRight, rightPoints);
                    t1 = new Thread(rightThread);
                    t1.start();
                }

                else {
                    newRec(p1, p3, nextRight, rightPoints, rightKohyll);
                }
            }

            leftKohyll.add(p3);

            if (nextLeft != -1) {
                if (leftDepth > 0) {
                    leftThread = new Worker2(leftDepth - 1, p3, p2, nextLeft, leftPoints);
                    t2 = new Thread(leftThread);
                    t2.start();
                }

                else {
                    newRec(p3, p2, nextLeft, leftPoints, leftKohyll);
                    // System.out.println("id " + id + " " + nextLeft + " " + leftKohyll);
                }
            }

            localKohyll.append(rightKohyll);

            if (t1 != null) {

                try {
                    t1.join();
                    localKohyll.append(rightThread.localKohyll);

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            localKohyll.append(leftKohyll);

            if (t2 != null) {
                try {
                    t2.join();
                    localKohyll.append(leftThread.localKohyll);
                } catch (Exception e) {
                    // TODO: handle exception
                }
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
        double[] sekv = new double[runs];


        for (int i = 0; i < runs; i++) {
            sekv[i] = task.sekvMetode();
            para[i] = task.paraKohyll();
        }

        Arrays.sort(para);
        Arrays.sort(sekv);

        System.out.println("same points in kohyll: " + same(task.paraList,task.sekList));
         System.out.printf(
                "Time for finding Kohyll of %d points using %d threads:\nsekvTime: %.2fms paraTime: %.2fms speedup: %.4f\n",
                n, threads, sekv[runs / 2], para[runs / 2], (double) sekv[runs / 2] / para[runs / 2]);
    }

    public static boolean same(IntList a, IntList b) {
        if (a.len != b.len) {return false;}

        for (int i = 0; i < a.len; i++) {
            if(a.get(i) != b.get(i)) return false;
        }

        return true;
    }
}