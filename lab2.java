import java.io.FileNotFoundException;
import java.util.*;

public class lab2 {
    public static void main(String[] args) throws FileNotFoundException {
        double t = 1000; // time
        int n = 20; // nodes
        double a = 7; // rate
        double r = 1000000; // speedlan
        double l = 1500; // length
        double d = 10; // distance
        double s = 200000000; // propspeed
        System.out.println("a=7 p");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            persistant(a, t, r, l, d, s, n * i);
        }
        a = 10;
        System.out.println("a=10 p");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            persistant(a, t, r, l, d, s, n * i);
        }
        a = 20;
        System.out.println("a=20 p");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            persistant(a, t, r, l, d, s, n * i);
        }
        a = 7;
        System.out.println("a=7 np");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            nonpersistant(a, t, r, l, d, s, n * i);
        }
        a = 10;
        System.out.println("a=10 np");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            nonpersistant(a, t, r, l, d, s, n * i);
        }
        a = 20;
        System.out.println("a=20 np");
        for (int i = 1; i <= 5; i++) {
            System.out.println("n= " + n * i);
            nonpersistant(a, t, r, l, d, s, n * i);
        }

    }

    public static void persistant(double a, double t, double r, double l, double d, double s, int n) {

        // List of nodes, where each node is a queue of packets
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n);
        int total = 0;
        // For each node, generate arrival packets
        for (int i = 0; i < n; i++) {
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                total++;
                node.add(currentTime);
            }
            nodes.add(node);
        }
        int[] collisionCounters = new int[n]; // Collision counter for each node
        int successCount = 0; // Count of successfully transmitted packets
        int droppedCount = 0;
        int transmissionAttempts = 0; // count of attemped transmissions
        double tProp = d / s; // Propegation delay between two adjacent nodes
        double tTrans = l / r; // Transmission delay for a packet
        double bitTime = (512.0) / r; // bit time
        double currentTime = 0.0; // current simulation time
        while (true) {
            int currNode = getNextNode(nodes, n);
            if (currNode == -1) {
                break;
            }
            boolean collisionDetected = false;
            transmissionAttempts++;
            ArrayList<Double> transmittingNode = nodes.get(currNode); // Node sending a packet
            currentTime = transmittingNode.get(0); // timestamp of next packet
            if (currentTime > t) {
                break;
            }

            // Loop through nodes to detect collisions
            for (int i = 0; i < n; i++) {
                ArrayList<Double> currentNode = nodes.get(i);
                if (i == currNode || currentNode.isEmpty())
                    continue;
                int delta = Math.abs(i - currNode); // distance between transmitting node and current node
                double dangertime = currentTime + delta * (tProp);

                // Check for collision
                if (currentNode.get(0) <= dangertime) {

                    collisionCounters[i]++;
                    transmissionAttempts++;
                    // Check if collision count exceeds limit
                    if (collisionCounters[i] <= 10) {
                        collisionDetected = true;
                        double backOffTime = bitTime * generateRandomBackoff(collisionCounters[i]);
                        currentNode.set(0, (currentTime + backOffTime));
                        // Delay other packets in the queue that arrive during backoff
                        for (int j = 1; j < currentNode.size(); j++) {
                            if (currentNode.get(j) < (currentTime + backOffTime)) {
                                currentNode.set(j, (currentTime + backOffTime));
                            } else {
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
            if (collisionDetected) {
                collisionCounters[currNode]++; // increment counter
                if (collisionCounters[currNode] <= 10) {
                    double backOffTime = bitTime * generateRandomBackoff(collisionCounters[currNode]); // set backoff
                    double waitTime = currentTime + backOffTime;
                    transmittingNode.set(0, waitTime);
                    // Delay other packets in the queue that arrive during backoff
                    for (int j = 1; j < transmittingNode.size(); j++) {
                        if (transmittingNode.get(j) < waitTime) {
                            transmittingNode.set(j, waitTime);
                        } else {
                            break;
                        }
                    }
                } else {
                    // Collision count exceeds limit; drop packet and reset
                    collisionCounters[currNode] = 0;
                    transmittingNode.remove(0);
                    droppedCount++;
                }
            } else {
                collisionCounters[currNode] = 0;
                transmittingNode.remove(0);
                successCount++;

                for (int i = 0; i < n; i++) {
                    ArrayList<Double> currentNode = nodes.get(i);
                    if (currentNode.isEmpty())
                        continue;
                    if (i == currNode) {
                        double busyTime = currentTime + tTrans;
                        while (true) {
                            if (currentNode.isEmpty())
                                break;
                            if (currentNode.get(0) == currentTime) {
                                currentNode.remove(0);
                            } else {
                                break;
                            }
                        }
                    } else {
                        int delta = Math.abs(i - currNode);
                        double busyTime = currentTime + delta * (tProp) + tTrans;
                        for (int j = 0; j < currentNode.size(); j++) {
                            // if node senses line is busy, delay transmission
                            if (currentNode.get(j) < busyTime) {
                                currentNode.set(j, busyTime);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        double efficiency = ((double) successCount / ((double) transmissionAttempts));
        double throughput = ((double) successCount * 1500.0 / 1000000.0) / t;
        System.out.println(efficiency);
        System.out.println(throughput);
    }

    public static void nonpersistant(double a, double t, double r, double l, double d, double s, int n) {

        // List of nodes, where each node is a queue of packets
        ArrayList<ArrayList<Double>> nodes = new ArrayList<ArrayList<Double>>(n);
        // For each node, generate arrival packets
        for (int i = 0; i < n; i++) {
            ArrayList<Double> node = new ArrayList<Double>();
            double currentTime = 0.0;
            while (currentTime < t) {
                currentTime += generateRandomAlpha(a);
                node.add(currentTime);
            }
            nodes.add(node);
        }
        int[] collisionCounters = new int[n]; // Collision counter for each node
        int[] busyCounters = new int[n]; // Collision counter for each node
        int successCount = 0; // Count of successfully transmitted packets
        int droppedCount = 0;
        int transmissionAttempts = 0; // count of attemped transmissions
        double tProp = d / s; // Propegation delay between two adjacent nodes
        double tTrans = l / r; // Transmission delay for a packet
        double bitTime = (512.0) / r; // bit time
        double currentTime = 0.0; // current simulation time
        while (true) {
            int currNode = getNextNode(nodes, n);
            if (currNode == -1) {
                break;
            }
            boolean collisionDetected = false;
            transmissionAttempts++;
            busyCounters[currNode] = 0;
            ArrayList<Double> transmittingNode = nodes.get(currNode); // Node sending a packet
            currentTime = transmittingNode.get(0); // timestamp of next packet
            if (currentTime > t) {
                break;
            }
            // Loop through nodes to detect collisions
            for (int i = 0; i < n; i++) {
                ArrayList<Double> currentNode = nodes.get(i);
                if (i == currNode || currentNode.isEmpty())
                    continue;
                int delta = Math.abs(i - currNode); // distance between transmitting node and current node
                double dangertime = currentTime + delta * (tProp);

                // Check for collision
                if (currentNode.get(0) <= dangertime) {

                    collisionCounters[i]++;
                    transmissionAttempts++;
                    // Check if collision count exceeds limit
                    if (collisionCounters[i] <= 10) {
                        collisionDetected = true;
                        double backOffTime = bitTime * generateRandomBackoff(collisionCounters[i]);
                        currentNode.set(0, (currentTime + backOffTime));
                        // Delay other packets in the queue that arrive during backoff
                        for (int j = 1; j < currentNode.size(); j++) {
                            if (currentNode.get(j) < (currentTime + backOffTime)) {
                                currentNode.set(j, (currentTime + backOffTime));
                            } else {
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
            if (collisionDetected) {
                collisionCounters[currNode]++; // increment counter
                if (collisionCounters[currNode] <= 10) {
                    double backOffTime = bitTime * generateRandomBackoff(collisionCounters[currNode]); // set backoff
                    double waitTime = currentTime + backOffTime;
                    transmittingNode.set(0, waitTime);
                    // Delay other packets in the queue that arrive during backoff
                    for (int j = 1; j < transmittingNode.size(); j++) {
                        if (transmittingNode.get(j) < waitTime) {
                            transmittingNode.set(j, waitTime);
                            // collisionCounters[currNode]++;
                        } else {
                            break;
                        }
                    }
                } else {
                    // Collision count exceeds limit; drop packet and reset
                    collisionCounters[currNode] = 0;
                    transmittingNode.remove(0);
                    droppedCount++;
                }
            } else {
                collisionCounters[currNode] = 0;
                transmittingNode.remove(0);
                successCount++;

                for (int i = 0; i < n; i++) {
                    ArrayList<Double> currentNode = nodes.get(i);
                    if (currentNode.isEmpty())
                        continue;
                    if (i == currNode) {
                        double busyTime = currentTime + tTrans;
                        while (true) {
                            if (currentNode.isEmpty())
                                break;
                            if (currentNode.get(0) == currentTime) {
                                currentNode.remove(0);
                            } else {
                                break;
                            }
                        }
                    } else {
                        int delta = Math.abs(i - currNode);
                        double busyTime = currentTime + delta * (tProp) + tTrans;
                        if (currentNode.get(0) < busyTime) {
                            busyCounters[i]++; // increment counter
                            double backOffTime = bitTime * generateRandomBackoff(busyCounters[i]); // set backoff
                            double waitTime = currentTime + backOffTime;
                            while (busyCounters[i] <= 10 && waitTime < busyTime) {
                                busyCounters[i]++; // increment counter
                                backOffTime = bitTime * generateRandomBackoff(busyCounters[i]); // set backoff
                                waitTime = waitTime + backOffTime;
                            }
                            if (waitTime >= busyTime) {
                                currentNode.set(0, waitTime);
                                // Delay other packets in the queue that arrive during backoff
                                for (int j = 1; j < currentNode.size(); j++) {
                                    if (currentNode.get(j) < waitTime) {
                                        currentNode.set(j, waitTime);
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                busyCounters[i] = 0;
                                while (true) {
                                    if (currentNode.isEmpty())
                                        break;
                                    if (currentNode.get(0) < busyTime) {
                                        currentNode.remove(0);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        double efficiency = ((double) successCount / ((double) transmissionAttempts));
        double throughput = ((double) successCount * 1500.0 / 1000000.0) / t;
        System.out.println(efficiency);
        System.out.println(throughput);
    }

    public static double generateRandomAlpha(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }

    public static int generateRandomBackoff(int count) {
        int upper = (int) Math.pow(2, count);
        Random rand = new Random();
        int randomNum = rand.nextInt(upper);
        return randomNum;
    }

    public static int getNextNode(ArrayList<ArrayList<Double>> nodes, int n) {
        double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < n; i++) {
            if (nodes.get(i).isEmpty())
                continue;
            double time = nodes.get(i).get(0);
            if (time < min) {
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