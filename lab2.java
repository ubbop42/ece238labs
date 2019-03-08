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

        System.out.printf("\nPersistant:\n");
        persistant(a,t,r,l,d,s,n);
        System.out.printf("\n\nNon Persistant:\n");
        nonPersistant(a,t,r,l,d,s,n);
    }

    public static void persistant(double a, double t, double r, double l, double d , double s,int n) {
        
        // List of nodes, where each node is a queue of packets
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n); 

        // For each node, generate arrival packets
        for(int i = 0; i < n; i++){
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                node.add(currentTime);
            }
            nodes.add(node);
        }

        int[] collisionCounters = new int[n];	// Collision counter for each node
        int successCount = 0;					// Count of successfully transmitted packets
        int droppedCount = 0;					// Count of dropped packets
        int transmissionAttempts = 0;			// count of attemped transmissions
        double tProp = d/s;						// Propegation delay between two adjacent nodes
        double tTrans = l/r;					// Transmission delay for a packet
        double bitTime = (512.0)/r;				// bit time
        double currentTime = 0.0;				// current simulation time
        while(true){
            int currNode = getNextNode(nodes,n); 
            if(currNode == -1) break;
            boolean collisionDetected = false;
            transmissionAttempts++;
            ArrayList<Double> transmittingNode = nodes.get(currNode); // Node sending a packet
            currentTime = transmittingNode.get(0);	// timestamp of next packet

            // Loop through nodes to detect collisions
            for(int i = 0; i < n; i++){
                ArrayList<Double> currentNode = nodes.get(i);
                if(i == currNode || currentNode.isEmpty()) continue;
                int delta = Math.abs(i-currNode); // distance between transmitting node and current node
                double dangertime = currentTime + delta*(tProp);

                // Check for collision
                if(currentNode.get(0) < dangertime){
                    collisionCounters[i]++;
                    double backOffTime = bitTime * generateRandomBackoff(collisionCounters[i]);
                    
                    //Check if collision count exceeds limit
                    if(collisionCounters[i]<=10){
                        collisionDetected = true;
                        transmissionAttempts++;
                        currentNode.set(0, (currentTime + backOffTime));
                        
                        // Delay other packets in the queue that arrive during backoff
                        for (int j = 1; j < currentNode.size(); j++) {
                            if(currentNode.get(j) < (currentTime + backOffTime)){
                                currentNode.set(j, (currentTime + backOffTime));
                            }
                            else{
                                break;
                            }
                        }
                    } else {
                    	// Collision count exceeds limit; drop packet and reset
                        collisionCounters[i] = 0;
                        currentNode.remove(0);
                        droppedCount++;
                    }
                }
            }

            // Handle collision
            if(collisionDetected){
                collisionCounters[currNode]++;	//increment counter
                double backOffTime = bitTime * generateRandomBackoff(collisionCounters[currNode]); // set backoff
                if(collisionCounters[currNode] < 10){
                	double waitTime = currentTime + backOffTime;
                    transmittingNode.set(0, waitTime);
                    // Delay other packets in the queue that arrive during backoff
                    for (int j = 1; j < transmittingNode.size(); j++) {
                        if(transmittingNode.get(j) < waitTime){
                            transmittingNode.set(j, waitTime);
                        }
                        else{
                            break;
                        }
                    }
                } else {
                    // Collision count exceeds limit; drop packet and reset
                    collisionCounters[currNode] = 0;
                    transmittingNode.remove(0);
                    droppedCount++;
                }
            } else{
                collisionCounters[currNode] = 0;
                transmittingNode.remove(0);
                successCount++;

                for(int i = 0; i < n ;i++) {
                	ArrayList<Double> currentNode = nodes.get(i);
                    if(currentNode.isEmpty()) continue;
                    int delta = Math.abs(i-currNode);
                    double busyTime = currentTime + delta*(tProp) + tTrans;
                    for (int j = 0; j < currentNode.size(); j++) {
                    	// if node senses line is busy, delay transmission
                        if(currentNode.get(j) < busyTime){
                            currentNode.set(j, busyTime);
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }

        System.out.printf("Dropped = %d\n", droppedCount);
        System.out.printf("transmissionAttempts = %d\n", transmissionAttempts);
        System.out.printf("successCount = %d\n", successCount);
        double efficiency = ((double)successCount/((double)transmissionAttempts));
        System.out.println(efficiency);
    }

    public static void nonPersistant(double a, double t, double r, double l, double d , double s,int n) {


        // List of nodes, where each node is a queue of packets
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n); 

        // For each node, generate arrival packets
        for(int i = 0; i < n; i++){
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                node.add(currentTime);
            }
            nodes.add(node);
        }

        int[] collisionCounters = new int[n];		// Collision counter for each node
        int[] collisionCounterSensing = new int[n];	// Collision counter for busybusy
        int successCount = 0;					// Count of successfully transmitted packets
        int droppedCount = 0;					// Count of dropped packets
        int transmissionAttempts = 0;			// count of attemped transmissions
        double tProp = d/s;						// Propegation delay between two adjacent nodes
        double tTrans = l/r;					// Transmission delay for a packet
        double bitTime = (512.0)/r;				// bit time
        double currentTime = 0.0;				// current simulation time
        while(true){
            int currNode = getNextNode(nodes,n); 
            if(currNode == -1) break;
            boolean collisionDetected = false;
            transmissionAttempts++;
            ArrayList<Double> transmittingNode = nodes.get(currNode); // Node sending a packet
            currentTime = transmittingNode.get(0);	// timestamp of next packet

            // Loop through nodes to detect collisions
            for(int i = 0; i < n; i++){
                ArrayList<Double> currentNode = nodes.get(i);
                if(i == currNode || currentNode.isEmpty()) continue;
                int delta = Math.abs(i-currNode); // distance between transmitting node and current node
                double dangertime = currentTime + delta*(tProp);

                // Check for collision
                if(currentNode.get(0) < dangertime){
                    collisionCounters[i]++;
                    double backOffTime = bitTime * generateRandomBackoff(collisionCounters[i]);
                    
                    //Check if collision count exceeds limit
                    if(collisionCounters[i]<=10){
                        collisionDetected = true;
                        transmissionAttempts++;
                        currentNode.set(0, (currentTime + backOffTime));
                        
                        // Delay other packets in the queue that arrive during backoff
                        for (int j = 1; j < currentNode.size(); j++) {
                            if(currentNode.get(j) < (currentTime + backOffTime)){
                                currentNode.set(j, (currentTime + backOffTime));
                            }
                            else{
                                break;
                            }
                        }
                    } else {
                    	// Collision count exceeds limit; drop packet and reset
                        collisionCounters[i] = 0;
                        currentNode.remove(0);
                        droppedCount++;
                    }
                }
            }

            // Handle collision
            if(collisionDetected){
                collisionCounters[currNode]++;	//increment counter
                if(collisionCounters[currNode] < 10){
                	double backOffTime = bitTime * generateRandomBackoff(collisionCounters[currNode]); // set backoff
                	double waitTime = currentTime + backOffTime;
                    transmittingNode.set(0, waitTime);
                    // Delay other packets in the queue that arrive during backoff
                    for (int j = 1; j < transmittingNode.size(); j++) {
                        if(transmittingNode.get(j) < waitTime){
                            transmittingNode.set(j, waitTime);
                        }
                        else{
                            break;
                        }
                    }
                } else {
                    // Collision count exceeds limit; drop packet and reset
                    collisionCounters[currNode] = 0;
                    transmittingNode.remove(0);
                    droppedCount++;
                }
            } else{
                collisionCounters[currNode] = 0;
                transmittingNode.remove(0);
                successCount++;

                for(int i = 0; i < n; i++) {
                	ArrayList<Double> currentNode = nodes.get(i);
                    if(currentNode.isEmpty()) continue;
                    int delta = Math.abs(i-currNode);
                    double busyTime = currentTime + delta*(tProp) + tTrans;
                    for (int j = 0; j < currentNode.size(); j++) {
                    	// if node senses line is busy, delay transmission
                        if(currentNode.get(j) < busyTime){
                            collisionCounterSensing[currNode]++;
                        	if(collisionCounterSensing[currNode] < 10){
                				double backOffTime = bitTime * generateRandomBackoff(collisionCounterSensing[currNode]); // set backoff
                        		double newDeparture = currentNode.get(j) + backOffTime;
                            	currentNode.set(j, newDeparture);
                        	}
                        	else
                        	{
                        		collisionCounterSensing[currNode] = 0;
                    			currentNode.remove(0);
                    			droppedCount++;
                        	}
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }

        System.out.printf("Dropped = %d\n", droppedCount);
        System.out.printf("transmissionAttempts = %d\n", transmissionAttempts);
        System.out.printf("successCount = %d\n", successCount);
        double efficiency = ((double)successCount/((double)transmissionAttempts));
        System.out.println(efficiency);

    }

    public static double generateRandomAlpha(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }
    
    public static int generateRandomBackoff(int count) {
    	int upper = (int)Math.pow(2,count);
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