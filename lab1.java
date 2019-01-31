import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class lab1 {
    public static void main(String[] args) throws FileNotFoundException {

        double alpha = 350;
        double l = 2000;
        double c = 1000000;
        double t = 1000;

        FileOutputStream fosmm1k = new FileOutputStream("mm1k_results.csv", false);
        PrintWriter pwmm1k = new PrintWriter(fosmm1k);

        pwmm1k.printf("p, k10_E[n], k10_P_loss, k25_E[n], k25_P_loss, k50_E[n], k50_P_loss\n");
        for (int lambda = 250; lambda <= 750; lambda += 50) {
            double[] k10_res = simulatemm1k(alpha, lambda, l, c, t, 10);
            double[] k25_res = simulatemm1k(alpha, lambda, l, c, t, 25);
            double[] k50_res = simulatemm1k(alpha, lambda, l, c, t, 50);
            pwmm1k.printf("%.1f, %.3f, %.3f, %.3f, %.3f, %.3f, %.3f\n", lambda * (l / c), k10_res[0], k10_res[1],
                    k25_res[0], k25_res[1], k50_res[0], k50_res[1]);
        }

        pwmm1k.close();

        FileOutputStream fosmm1 = new FileOutputStream("mm1_results.csv", false);
        PrintWriter pwmm1 = new PrintWriter(fosmm1);

        double[] res = new double[2];
        pwmm1.printf("p, idle, qCount\n");
        for (int lambda = 125; lambda <= 475; lambda += 50) {
            res = simulatemm1(alpha, lambda, l, c, t);
            pwmm1.printf("%.2f, %.3f, %.3f\n", lambda * (l / c), res[1], res[0]);
        }

        pwmm1.println();
        res = simulatemm1(alpha, 600, l, c, t);
        pwmm1.printf("%.2f, %.3f, %.3f\n", 600 * (l / c), res[1], res[0]);

        pwmm1.close();

    }

    public static double[] simulatemm1(double alpha, double lambda, double l, double c, double t) {
        // List containing arrival, departure and observer events
        LinkedList<Event> eventList = new LinkedList<Event>();
        double queueDelay = 0.0; // Time to process the current elements in queue
        double currentTime = 0.0; // Current timestamp of simulation
        double delta = 0.0; // Delay between arrival events
        double serviceTime = 0.0; // Service delay for a given packet size
        double departureTime = 0.0; // Timestamp for the departure event
        double observerCount = 0; // Number of observer events

        // ++++++++++ EVENT GENERATION ++++++++++++++

        while (currentTime < t) { // Populate event list with arrival and departure events
            // Perform Timing caculations for arrival/departure events
            delta = generateRandom(lambda);
            currentTime += delta;
            serviceTime = generateRandom(1.0 / l) / c;
            queueDelay = Math.max(0, queueDelay - delta);
            departureTime = currentTime + serviceTime + queueDelay;
            queueDelay += serviceTime;

            // Add events with their corresponding timestamp
            Event arrival = new Event("Arrival", currentTime);
            eventList.add(arrival);
            Event departure = new Event("Departure", departureTime);
            eventList.add(departure);
        }
        currentTime = 0.0;
        while (currentTime < t) { // Populate event list with observer events
            currentTime += generateRandom(alpha);
            Event temp = new Event("Observer", currentTime);
            eventList.add(temp);
            observerCount++;
        }

        Collections.sort(eventList, new timeComp()); // Sort list on timestamps

        // ++++++++++ SIMULATION ++++++++++++++

        double queueSize = 0; // Number of elements in the queue
        double queueSum = 0; // Sum of number of elements in the queue
        long idleCount = 0; // Track number of times queue is idle (queueSize = 0)
        for (Event e : eventList) {
            if (e.type.equals("Arrival")) {
                queueSize++;
            } else if (e.type.equals("Departure")) {
                queueSize--;
            } else if (e.type.equals("Observer")) {
                queueSum += queueSize;
                idleCount += (queueSize == 0) ? 1 : 0; // increment if idle
            }
        }

        double avgQueueSize = (queueSum / observerCount); // Average number of elements in the queue, E[n]
        double idleFraction = (idleCount / observerCount); // Fraction of time the queue is idle

        return new double[] { avgQueueSize, idleFraction };
    }

    public static double[] simulatemm1k(double alpha, double lambda, double l, double c, double t, double k) {
        // List containing arrival, departure and observer events
        PriorityQueue<Event> eventList = new PriorityQueue<Event>(1000000, new timeComp());
        double currentTime = 0.0; // Current timestamp of simulation
        int totalPacketCount = 0; // Number of arrival events
        double observerCount = 0; // Number of observer events

        while (currentTime < t) {
            currentTime += generateRandom(lambda);
            eventList.add(new Event("Arrival", currentTime));
            totalPacketCount++;
        }
        currentTime = 0.0;
        while (currentTime < t) {
            currentTime += generateRandom(alpha);
            Event temp = new Event("Observer", currentTime);
            eventList.add(temp);
            observerCount++;

        }

        // Packet queue
        LinkedList<Double> queue = new LinkedList<Double>();

        double dropCount = 0; // Count number of dropped packets
        double queueDelay = 0.0; // Time to process the current elements in queue
        double delta = 0.0; // Delay between arrival events
        double serviceTime = 0.0; // Service delay for a given packet size
        double departureTime = 0.0; // Timestamp for the departure event
        double queueSize = 0; // Number of elements in the queue
        double queueSum = 0; // Sum of number of elements in the queue

        while (true) {
            Event e = eventList.poll();
            if (e == null) // break when the list is empty
                break;
            if (e.type.equals("Arrival")) {
                if (queue.size() > k) { // if the queue is full
                    dropCount++; // the packet is dropped
                } else {
                    serviceTime = generateRandom(1.0 / l) / c;
                    queue.addFirst(serviceTime);
                    departureTime = e.time + serviceTime + queueDelay;
                    queueDelay += serviceTime;
                    eventList.add(new Event("Departure", departureTime));
                }
            } else if (e.type.equals("Departure")) {
                queueDelay = Math.max(0, queueDelay - queue.removeLast());
            } else if (e.type.equals("Observer")) {
                queueSum += queue.size();
            }
        }
        double avgQueueSize = (queueSum / observerCount);
        double packetLoss = (dropCount / totalPacketCount) * 100;

        return new double[] { avgQueueSize, packetLoss };

    }

    public static double generateRandom(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }

}

class timeComp implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        if (e1.time > e2.time) {
            return 1;
        } else {
            return -1;
        }
    }
}
