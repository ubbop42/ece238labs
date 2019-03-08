import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class lab2 {
    public static void main(String[] args) throws FileNotFoundException {
        double t = 1000; //time
        int n = 20; //nodes
        double a = 12; //rate
        double r = 1000000; //speedlan
        double l = 1500; //length
        double d = 10; //distance
        double s = 200000000; //propspeed

        persistant(a,t,r,l,d,s,n);
    }

    public static void persistant(double a, double t, double r, double l, double d , double s,int n) {
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n);
        int total = 0;

        // 
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

        int[] collisionCounters = new int[n];
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
            boolean collisionDetected = false;
            transmitted++;
            currentTime = nodes.get(currNode).get(0);

            for(int i = 0; i < n; i++){
                ArrayList<Double> currentNode = nodes.get(i);
                if(currentNode.isEmpty()) continue;
                int delta = Math.abs(i-currNode);
                if(delta == 0) continue; 
                double dangertime = currentTime + delta*(tProp);

                // Something
                if(currentNode.get(0) < dangertime){
                    collisionCounters[i]++;
                    double backOffTime = bitTime * generateRandomBackoff((int)Math.pow(2,collisionCounters[i]));
                    //if collision count exceeds limit
                    if(collisionCounters[i]<=10){
                        collisionDetected = true;
                        transmitted++;
                        currentNode.set(0, (currentTime + backOffTime));
                        for (int j = 1; j < currentNode.size(); j++) {
                            if(currentNode.get(j) < (currentTime + backOffTime)){
                                currentNode.set(j, (currentTime + backOffTime));
                                transmitted++;
                            }
                            else{
                                break;
                            }
                        }
                    } else {
                        collisionCounters[i] = 0;
                        currentNode.remove(0);
                        dropped++;
                    }
                }
            }
            if(collisionDetected){
                collisionCounters[currNode]++;
                double backOffTime = bitTime * generateRandomBackoff((int)Math.pow(2,collisionCounters[currNode]));
                if(collisionCounters[currNode]<10){
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
                    collisionCounters[currNode] = 0;
                    nodes.get(currNode).remove(0);
                    dropped++;
                }
            } else{
                collisionCounters[currNode] = 0;
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
    
    public static int generateRandomBackoff(int upper) {
        Random rand = new Random();
        int randomNum = rand.nextInt(upper);
        return randomNum;
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