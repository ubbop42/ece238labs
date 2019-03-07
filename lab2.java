import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class lab2 {
    public static void main(String[] args) throws FileNotFoundException {
        double t = 1000; //time
        int n = 60; //nodes
        double a = 5; //rate
        double r = 1000000; //speedlan
        double l = 1500; //length
        double d = 10; //distance
        double s = 200000000; //propspeed

        perstsitance(a,t,r,l,d,s,n);
    }

    public static void perstsitance(double a, double t, double r, double l, double d , double s,int n) {
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n);
        int total = 0;

        for(int i = 0; i < n; i++){
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                node.add(currentTime);
                total++;
            }
            nodes.add(node);
        }

        int[] collisiionCounters = new int[n];
        int success = 0;
        int dropped = 0;
        int transmitted = 0;
        double tProp = d/s;
        double tTrans = l/r;
        double bitTime = (512.0)/r;
        double currentTime = 0.0;
        while(true){
            int currNode = getNextNode(nodes,n);
            if(currNode == -1) break;
            boolean collissionDetected = false;
            transmitted++;
            currentTime = nodes.get(currNode).get(0);
            for(int i = 0; i < n ;i++){
                if(nodes.get(i).isEmpty()) continue;
                int delta = Math.abs(i-currNode);
                if(delta == 0) continue; 
                double dangertime = currentTime + delta*(tProp);
                if(nodes.get(i).get(0) < dangertime){
                    collissionDetected = true;
                    collisiionCounters[i]++;
                    transmitted++;
                    double backOffTime = bitTime * generateRandomBackoff(Math.pow(2,collisiionCounters[i])-1);
                    if(collisiionCounters[i]<=10){
                        nodes.get(i).set(0, (currentTime + backOffTime));
                        for (int j = 1; j < nodes.get(i).size() ;j++) {
                            if(nodes.get(i).get(j) < dangertime){
                                nodes.get(i).set(j, (currentTime + backOffTime));
                                transmitted++;
                            }
                            else{
                                break;
                            }
                        }
                    } else {
                        collisiionCounters[i] = 0;
                        nodes.get(i).remove(0);
                        dropped++;
                    }
                }
            }
            if(collissionDetected){
                collisiionCounters[currNode]++;
                double backOffTime = bitTime + generateRandomBackoff(Math.pow(2,collisiionCounters[currNode])-1);
                if(collisiionCounters[currNode]<10){
                    nodes.get(currNode).set(0, (currentTime + backOffTime));
                    for (int j = 1; j < nodes.get(currNode).size() ;j++) {
                        if(nodes.get(currNode).get(j) < currentTime + backOffTime){
                            nodes.get(currNode).set(j, (currentTime + backOffTime));
                        }
                        else{
                            break;
                        }
                    }
                } else {
                    collisiionCounters[currNode] = 0;
                    nodes.get(currNode).remove(0);
                    dropped++;
                }
            } else{
                collisiionCounters[currNode] = 0;
                nodes.get(currNode).remove(0);
                success++;

                for(int i = 0; i < n ;i++){
                    if(nodes.get(i).isEmpty()) continue;
                    int delta = Math.abs(i-currNode);
                    double busyTime = currentTime + delta*(tProp) + tTrans;
                    for (int j = 0; j < nodes.get(i).size() ;j++) {
                        if(nodes.get(i).get(j) < busyTime){
                            nodes.get(i).set(j, busyTime);
                        }
                        else{
                            break;
                        }
                    }
                }
            }
        }
        System.out.println(dropped);
        System.out.println(transmitted);
        System.out.println(success);
        System.out.println(total);
        double efficiency = ((double)success/((double)transmitted));
        System.out.println(efficiency);
    }

    // public static double[] nonPerstsitance(double a, double t, double r, double l, double d , double s) {
    

    // }

    public static double generateRandomAlpha(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }
    
    public static int generateRandomBackoff(double upper) {
        // System.out.println((int)upper +"*"+(int)(Math.random() * (upper - 0)));
        return (int)(Math.random() * (upper - 0));
    }

    public static int getNextNode(ArrayList<ArrayList<Double>> nodes, int n) {  
        double min = Double.MAX_VALUE;
        int index = -1;
        for(int i = 0; i < n; i++){
            if(nodes.get(i).isEmpty()) continue;
            double time = nodes.get(i).get(0);
                if(time < min){
                    min = time;
                    index = i;
                }
        }
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