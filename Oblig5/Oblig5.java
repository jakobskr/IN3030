
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
        if(nextRight != -1) {
            newRec(maxPos, minPos, nextRight, rightPoints, koHyll);
        }
    
        koHyll.add(minPos);

        if(nextLeft != -1) {
            newRec(minPos,maxPos,nextLeft,leftPoints,koHyll);
        }
        
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


        if (next != -1) {
            sekvRek(p3, p2, next, points, koHyll);
        }
        points = null;


    }

    public void newRec(int p1, int p2,int p3, IntList m, IntList koHyll) {

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
               
                if(d < 0) rightPoints.add(m.get(i));

                else if(d == 0 && isBetween(p1,p3,m.get(i))) {
                    rightPoints.add(m.get(i));
                    if(nextRight == -1) nextRight = m.get(i);
                }

                if (d < furthestR) {
                    nextRight = m.get(i);
                    furthestR = d;
                }
            }
                
            double dl = (double) ((al * x[m.get(i)] + bl * y[m.get(i)] + cl));
            
            if (dl <= 0) {

                if(dl < 0) leftPoints.add(m.get(i));

                else if(dl == 0 && isBetween(p3,p2,m.get(i))) {
                    leftPoints.add(m.get(i));
                    if(nextLeft == -1) nextLeft = m.get(i);
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