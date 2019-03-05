import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class lab2 {
    public static void main(String[] args) throws FileNotFoundException {
        double t = 1000; //time
        int n = 20; //nodes
        double a = 10; //rate
        double r = 1000000; //speedlan
        double l = 1500; //length
        double d = 10; //distance
        double s = 200000000; //propspeed

        perstsitance(a,t,r,l,d,s,n);
    }

    public static void perstsitance(double a, double t, double r, double l, double d , double s,int n) {
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n);

        for(int i = 0; i < n; i++){
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                node.add(currentTime);
            }
            nodes.add(node);
        }

        int[] collisiionCounters = new int[n];
        int[] transmittedCounters = new int[n];
        double tProp = d/s;
        double tTrans = l/r;
        double bitTime = 1/r;
        double currentTime = 0.0;
        while(true){
            int currNode = getNextNode(nodes,n);
            currentTime = nodes.get(currNode).get(0);
            for(int i = 0; i < n ;i++){
                int delta = Math.abs(i-currNode);
                double dangertime = currentTime + delta*(tProp);
                for (int j = 0;;j++) {
                    if(nodes.get(i).get(j)<dangertime){
                        collisiionCounters[i]++;
                        if(collisiionCounters[i]<10){
                            double backOffTime = 512 * bitTime + generateRandomBackoff(Math.pow(2,collisiionCounters[i])-1);
                            nodes.get(i).set(j, (nodes.get(i).get(j)+ backOffTime));
                        }
                    }
                    else break;
                }
            } 

        }


    }

    // public static double[] nonPerstsitance(double a, double t, double r, double l, double d , double s) {
    

    // }

    public static double generateRandomAlpha(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }
    
    public static int generateRandomBackoff(double upper) {
        return (int)(Math.random() * (upper - 0));
    }

    public static int getNextNode(ArrayList<ArrayList<Double>> nodes, int n) {  
        double min = nodes.get(0).get(0);
        int index = 0;
        for(int i = 1; i < n; i++){
            double time = nodes.get(i).get(0);
                if(time < min){
                    min = time;
                    index = i;
                }
        }
        System.out.println(index);
        return index;
    }
}

class nodeComp implements Comparator<ArrayList<Double>> {

    @Override
    public int compare(ArrayList<Double> n1, ArrayList<Double> n2) {
        if (n1.get(0) > n2.get(0)) {
            return 1;
        } else {
            return -1;
        }
    }
}