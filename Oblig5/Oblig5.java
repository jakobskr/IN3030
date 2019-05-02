
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
    CyclicBarrier cb;
    int test = 0;

    public Oblig5(int n, int[] x, int[] y) {
        this.n = n;
        threads = 6;
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
    public double distance(int x1, int y1, int x2, int y2, int px, int py) {
        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2 * x1 - y1 * x2;

        double d = (double) ((a * px + b * py + c) / (double) Math.sqrt(a * a + b * b));

        return d;
    }

    public void sekvMetode() {

        // System.out.println(distance(3, 4, 7, 8, 1, 1) + " " + distance(7, 8, 3, 4, 1,
        // 1));
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

        System.out.println("sekv kohyll: "  + koHyll + " " + koHyll.len);

        if (n < 100000) {
            new TegnUt(this, koHyll);
        }

        koHyll = null;

        //System.out.println("heckin' " + calls + " calls");
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

        if (p1 == 38 || p2 == 38) {
            // System.out.printf("%d %d %d %b %b\n",p1,p2,i,ix == p1x && p1x < ix && ix <
            // p2x || p1x > ix && ix > p2x,p1y < iy && iy < p2y || p1y > iy && iy > p2y);
        }

        if (p1x < ix && ix < p2x || p1x > ix && ix > p2x) {
            return true;
        }

        if (p1y < iy && iy < p2y || p1y > iy && iy > p2y) {
            return true;
        }
        return false;
    }

    public void paraKohyll()  {
        System.out.println("Here we heckin' go");
        IntList koHyll = new IntList(15);
        IntList leftKohyll = new IntList(3);
        IntList rightKohyll = new IntList(3);
        IntList localKohyll = new IntList(3);

        int maxPos = 0;
        int minPos = 0;
        int nextLeft = -1;
        int nextRight = -1;

        Thread t1 = null;
        Thread t2 = null;

        for (int i = 1; i < n; i++) {
            if (x[i] > x[maxPos])
                maxPos = i;
            if (x[i] < x[minPos])
                minPos = i;
        }

        IntList rightPoints = new IntList(3);
        IntList leftPoints = new IntList(3);
        double furthestL = 0.1;
        double furthestR = 0.1;
        int ar = y[maxPos] - y[minPos];
        int br = x[minPos] - x[maxPos];
        int cr = y[minPos] * x[maxPos] - y[maxPos] * x[minPos];

        int al = y[minPos] - y[maxPos];
        int bl = x[maxPos] - x[minPos];
        int cl = y[maxPos] * x[minPos] - y[minPos] * x[maxPos];

        threads = 4;
        int leftDepth = threads / 2;
        int rightDepth = threads / 2 + threads % 2;

        Worker2 leftThread = null; Worker2 rightThread = null;
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

        rightKohyll.add(maxPos);
        if (nextRight != -1) {

            if (rightDepth > 0) {
                rightThread = new Worker2(rightDepth - 1, maxPos, minPos ,nextRight, rightPoints);
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

        if((leftDepth == 0 && rightDepth == 0) || (t1 == null) && (t2 == null)) {

            localKohyll.append(rightKohyll);
            localKohyll.append(leftKohyll);
        }

        if(t1 != null || t2 != null) {
            localKohyll.append(rightKohyll);

            if(t1 != null) {
                try {
                    t1.join();
                    localKohyll.append(rightThread.localKohyll);

                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

            localKohyll.append(leftKohyll);

            if(t2 != null) {
                try {
                    //System.out.println("beep booop " + leftThread.localKohyll);
                    t2.join();
                    localKohyll.append(leftThread.localKohyll);
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

        }

        System.out.println("para kohyll: " + localKohyll + " " + localKohyll.len);
        new TegnUt(this, localKohyll);

    }

    public class Worker2 implements Runnable {
        IntList localKohyll = new IntList(4);
        IntList leftList = new IntList(3);
        IntList rightList = new IntList(3);
        Worker2 leftThread, rightThread;
        int leftDepth, rightDepth;
        int p1, p2, p3;
        IntList m;
        Thread t1 = null; Thread t2 = null;
        int id = 0;

        public Worker2(int depth, int p1, int p2, int p3, IntList m) {
            this.id = test++;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.m = m;
            leftDepth = depth / 2 ;
            rightDepth = (depth / 2) + depth % 2;

        }

        public void run() {
            //System.out.println("thread: " + id + " " + rightDepth + " " + leftDepth + " " + p1 + " " + p2 + " " + p3 + " " + m);
            paraRec(p1, p2, p3 , m);
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
                if(rightDepth > 0 ) {
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
                if (leftDepth > 0 ) {
                    leftThread = new Worker2(leftDepth - 1, p3, p2, nextLeft, leftPoints);
                    t2 = new Thread(leftThread);
                    t2.start();
                }

                else {
                    newRec(p3, p2, nextLeft, leftPoints, leftKohyll);
                    //System.out.println("id " + id + " " + nextLeft + " " + leftKohyll);
                }
            }

            if((leftDepth == 0 && rightDepth == 0) || (t1 == null) && (t2 == null)) {
                rightKohyll.append(leftKohyll);
                localKohyll.append(rightKohyll);

            }



            else {
                if(t1 != null || t2 != null) {

                    localKohyll.append(rightKohyll);


                    if(t1 != null) {

                        try {
                            t1.join();
                            localKohyll.append(rightThread.localKohyll);
                            
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                    }

                    localKohyll.append(leftKohyll);


                    if(t2 != null) {
                        try {
                            t2.join();
                            localKohyll.append(leftThread.localKohyll);
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                    }

                    
                }
            }

            //System.out.println("Thread localKohyll " + id + " " + localKohyll);

        }

    }

    public static void main(String[] args)  {

        if (args.length < 1) {
            return;
        }

        int n = Integer.parseInt(args[0]);
        NPunkter17 p = new NPunkter17(n);
        int[] x = new int[n];
        int[] y = new int[n];
        p.fyllArrayer(x, y);

        Oblig5 task = new Oblig5(n, x, y);

        System.out.println("dist is " + task.distance(12, 12, 6, 18, 9, 15));

        task.sekvMetode();
        task.paraKohyll();

        // System.out.println(task.printPoints());
        // correctKohyllForced();

    }

    public static void correctKohyllForced() {

        int n = 15;
        NPunkter17 p = new NPunkter17(n);
        int[] x = new int[n];
        int[] y = new int[n];
        p.fyllArrayer(x, y);

        Oblig5 task = new Oblig5(n, x, y);
        System.out.println(task.printPoints());
        task.MIN_X = 0;
        task.MAX_X = 10;
        task.MIN_Y = 0;
        task.MAX_Y = 10;

        IntList ko = new IntList(7);

        ko.add(6);

        TegnUt tu = new TegnUt(task, ko);
    }
}