
import java.util.Arrays;

public class Oblig5 {
    int MIN_X = 1;
    int MAX_X = 100;
    int MIN_Y = 1, MAX_Y = 100;
    int calls = 0;
    int n;
    int threads;
    int[] x, y;

    public Oblig5(int n, int[] x, int[] y) {
        this.n = n;
        threads = 4;
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

        //System.out.println(distance(3, 4, 7, 8, 1, 1) + " "  + distance(7, 8, 3, 4, 1, 1));
        IntList koHyll = new IntList(15);
        IntList points = new IntList(4);

        int maxPos = 0;
        int minPos = 0;
        int p3 = 0;
        for (int i = 1; i < n; i++) {
            if (x[i] > x[maxPos])
                maxPos = i;
            if (x[i] < x[minPos])
                minPos = i;
        }
        koHyll.add(maxPos);
        double furthest = 0.1;
        int a = y[maxPos] - y[minPos];
        int b = x[minPos] - x[maxPos];
        int c = y[minPos] * x[maxPos] - y[maxPos] * x[minPos];

        for (int i = 0; i < n; i++) {
            if (i == maxPos || i == minPos)
                continue;
            double d = (double) ((a * x[i] + b * y[i] + c));
            
            if (d <= 0) {
                points.add(i);

                if (d < furthest) {
                    p3 = i;
                    furthest = d;
                }
            }
            
        }

        //System.out.println("the points " + points);
        //System.out.println("hjelp " + points);
        //System.out.printf("%d %d %d \n", maxPos, minPos, p3);
        try {
            sekvRek(maxPos, minPos, p3, points, koHyll);

        } catch (OutOfMemoryError e) {
            System.out.println(calls);
            System.exit(0);
        }
        
       
        koHyll.add(minPos);
        points = new IntList(10);
        p3 = 0;
        furthest = 0.1;
        a = y[minPos] - y[maxPos];
        b = x[maxPos] - x[minPos];
        c = y[maxPos] * x[minPos] - y[minPos] * x[maxPos];

        for (int i = 0; i < n; i++) {
            if (i == maxPos || i == minPos) continue;
                
                double d = (double) ((a * x[i] + b * y[i] + c));
                if (d <= 0) {
                points.add(i);

                if (d < furthest) {
                    p3 = i;
                    furthest = d;
                }
            }
            
        }

        //System.out.println("heck me up " + points);
        sekvRek(minPos,maxPos,p3,points,koHyll);
        

        MAX_X = x[0];
        MIN_X = x[0];
        MAX_Y = y[0];
        MIN_Y = y[0];
        for (int i = 0; i < n; i++) {
            if(x[i] < MIN_X) MIN_X = x[i];
            if(x[i] > MAX_X) MAX_X = x[i];
            if(y[i] < MIN_Y) MIN_Y = y[i];
            if(y[i] > MAX_Y) MAX_Y = y[i];

        }
        
        System.out.println(koHyll);

        if(n < 100000 ) {
            new TegnUt(this, koHyll);
        }
        
        System.out.println("heckin' " + calls + " calls");
    }

    public void sekvRek(int p1, int p2, int p3, IntList m, IntList koHyll) {
        calls++;
        //System.out.println(p1 + " " + p2 + " " + p3 +  " " +  m);
        if (m.len < 1) {
            //System.out.println("added " + p3);
            koHyll.add(p3);
            return;
        }

        //System.out.printf("rec: %d %d %d \n", p1, p2 , p3);
        IntList points = new IntList(3);

        //right side

        int next = -1;
        double furthest = 0.0;

        for (int i = 0; i < m.len; i++) {
            if (m.get(i) == p3 || m.get(i) == p1 || m.get(i) == p2) continue;
            
            double d = distance(x[p1], y[p1], x[p3], y[p3], x[m.get(i)], y[m.get(i)]);

            if (d <= 0) {

                if(d < 0) points.add(m.get(i));

                else if(d == 0 && isBetween(p1,p3,m.get(i))) {
                    points.add(m.get(i));
                    if(next == -1) next = m.get(i);
                }

                if (d < furthest) {
                    next = m.get(i);
                    furthest = d;
                }
            }

           
        }

        /* //we only found points on line, now we how to check if the given points are between p1-p3;
        if (furthest == 0) {
            points = findPointsBetween(p1, p3, points);
            if (points.len != 0) {
                next = points.get(0);

            }
        } */

        if (next != -1) {
            sekvRek(p1, p3, next, points, koHyll);
        }

        points = new IntList(3);
        koHyll.add(p3);

        //left side
        next = -1;
        furthest = 0.0;
        for (int i = 0; i < m.len; i++) {
            
            if (m.get(i) == p3 || m.get(i) == p1 || m.get(i) == p2) continue;
            double d = distance(x[p3], y[p3], x[p2], y[p2], x[m.get(i)], y[m.get(i)]);
            
            if (d <= 0) {

                if(d < 0) points.add(m.get(i));

                else if(d == 0 && isBetween(p3,p2,m.get(i))) {
                    points.add(m.get(i));
                    if(next == -1) next = m.get(i);
                }

                if (d < furthest) {
                    next = m.get(i);
                    furthest = d;
                }
            }

           
        }


        /* //lets the right recursive handle straight lines : ), i have noe idea what is wrong MonkaS
        if(furthest == 0 ) {
            points = findPointsBetween(p2, p3, points);
            if (points.len == 0) {
                return;
            }
            next = points.get(0);
        }
        */


        if (next != -1) {
            sekvRek(p3, p2, next, points, koHyll);
        }
        points = null;


    }

    public boolean isBetween(int p1, int p2,int i) {
        int p1x = x[p1]; int p1y = y[p1];
        int p2x = x[p2]; int p2y = y[p2];
        int ix = x[i]; int iy = y[i];

        if(p1 == 38 || p2 == 38) {
            //System.out.printf("%d %d %d %b %b\n",p1,p2,i,ix == p1x && p1x < ix && ix < p2x || p1x > ix && ix > p2x,p1y < iy && iy < p2y || p1y > iy && iy > p2y);
        }
            
        if(p1x < ix && ix < p2x || p1x > ix && ix > p2x) {
            return true;
        }

        if(p1y < iy && iy < p2y || p1y > iy && iy > p2y) {
            return true;
        }
        return false;
    }

    public IntList findPointsBetween(int p1, int p2, IntList m) {
        IntList points = new IntList(3);
        int p1x = x[p1]; int p1y = y[p1];
        int p2x = x[p2]; int p2y = y[p2];
        
        for (int i = 0; i < m.len; i++) {
            int ix = x[m.get(i)]; int iy = y[m.get(i)];
            
            if(ix == p1x && ((iy < p1y && iy > p2y) || (iy > p1y && iy < p2y))) {
                points.add(m.get(i));
            }

            if(iy == p1y && ((ix < p1x && ix > p2x) || (ix > p1x && ix < p2x))) {
                points.add(m.get(i));
            }


        }
        return points;
    }

    public static void main(String[] args) {

        if(args.length < 1) {return;}

        
        

        int n = Integer.parseInt(args[0]);
        NPunkter17 p = new NPunkter17(n);
        int[] x = new int[n];
        int[] y = new int[n];
        p.fyllArrayer(x, y);


        Oblig5 task = new Oblig5(n, x, y);

        System.out.println("dist is " + task.distance(12,12,6,18,9,15));

        task.sekvMetode();

        //System.out.println(task.printPoints());
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