import java.util.*;

public class lab1 {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        boolean isbounded = false;
        double alpha = 350;
        double l = 2000;
        double c = 1000000;
        double t = 1000;

        if (isbounded) {
			
			System.out.printf("k, p, E[n]\n");
			int k[] = { 10, 25, 50 };
			double[] res = new double[2];
			for( int i = 0; i < 3; i++){
	            for (int lambda = 250; lambda <= 750; lambda += 50){
	            	res = simulatemm1k(alpha, lambda, l, c, t, k[i]);
	                System.out.printf("%d, %.1f, %.3f\n", k[i], lambda * (l / c), res[0]);
	            }
	        }

		    System.out.printf("\nk, p, P_loss\n");
			for( int i = 0; i < 3; i++){
		        for (int lambda = 200; lambda < 1000; lambda += 50){
	             	res = simulatemm1k(alpha, lambda, l, c, t, k[i]);
		            System.out.printf("%d, %.1f, %.3f\n", k[i], lambda * (l / c), res[1]);
		        }
		        for (int lambda = 1000; lambda < 2500; lambda += 100){
	             	res = simulatemm1k(alpha, lambda, l, c, t, k[i]);
		            System.out.printf("%d, %.1f, %.3f\n", k[i], lambda * (l / c), res[1]);
		        }
		        for (int lambda = 2500; lambda <= 5000; lambda += 200){
	             	res = simulatemm1k(alpha, lambda, l, c, t, k[i]);
		            System.out.printf("%d, %.1f, %.3f\n", k[i], lambda * (l / c), res[1]);
		        }
			}

        } else {
            double[] res = new double[2];
            System.out.printf("ρ, idle, qCount\n");
            for (int lambda = 125; lambda <= 475; lambda += 5){
            	res = simulatemm1(alpha, lambda, l, c, t);
                System.out.printf("%.2f, %.3f, %.3f\n", lambda * (l / c), res[1], res[0]);
            }

            System.out.println();
            res = simulatemm1(alpha, 600, l, c, t);
            System.out.printf("%.2f, %.3f, %.3f\n", 600 * (l / c), res[1], res[0]);

        }
    }

    public static double[] simulatemm1(double alpha, double lambda, double l, double c, double t) {
        LinkedList<Event> eventList = new LinkedList<Event>();
        double qDelay = 0.0;
        double currentTime = 0.0;
        while (currentTime < t) {
            double delta = genarateRandom(lambda);
            currentTime += delta;
            double serviceTime = genarateRandom(1.0 / l) / c;
            qDelay = Math.max(0, qDelay - delta);
            double departureTime = currentTime + serviceTime + qDelay;
            qDelay += serviceTime;
            Event arrival = new Event("Arrival", currentTime);
            eventList.add(arrival);
            Event departure = new Event("Departure", departureTime);
            eventList.add(departure);
        }
        currentTime = 0.0;
        while (currentTime < t) {
            currentTime += genarateRandom(alpha);
            Event temp = new Event("Observer", currentTime);
            eventList.add(temp);
        }

        Collections.sort(eventList, new timeComp());

        double q = 0;
        double qSum = 0;
        double observerCount = 0;
        long idleCount = 0;
        for (Event e : eventList) {
            if (e.type.equals("Arrival")) {
                q++;
            } else if (e.type.equals("Departure")) {
                q--;
            } else if (e.type.equals("Observer")) {
                qSum += q;
                observerCount++;
                idleCount += (q == 0) ? 1 : 0;
            }
        }
        double avgNumberOfElementsInQ = (qSum / observerCount);
        double idle = (idleCount / observerCount);

        double res[] = new double[2];
        res[0] = avgNumberOfElementsInQ;
        res[1] = idle;
        return res;
    }

    public static double[] simulatemm1k(double alpha, double lambda, double l, double c, double t, double k) {
        // LinkedList<Event> eventList = new LinkedList<Event>();
        PriorityQueue<Event> eventList = new PriorityQueue<Event>(10000000, new timeComp());
        double currentTime = 0.0;
        int totalPacketCount = 0;
        while (currentTime < t) {
            double delta = genarateRandom(lambda);
            currentTime += delta;
            Event arrival = new Event("Arrival", currentTime);
            eventList.add(arrival);
            totalPacketCount++;
        }
        currentTime = 0.0;
        while (currentTime < t) {
            currentTime += genarateRandom(alpha);
            Event temp = new Event("Observer", currentTime);
            eventList.add(temp);
        }

        LinkedList<Double> q = new LinkedList<Double>();
        double qDelay = 0;
        double qSum = 0;
        double dropCount = 0;
        double observerCount = 0;
        long idleCount = 0;
        for (int i = 0; ; i++) {
            Event e = eventList.poll();
            if (e == null)
                break;
            if (e.type.equals("Arrival")) {
                if (q.size() > k) {
                    dropCount++;
                } else {
                    double serviceTime = genarateRandom(1.0 / l) / c;
                    q.addFirst(serviceTime);
                    double departureTime = e.time + serviceTime + qDelay;
                    qDelay += serviceTime;
                    Event departure = new Event("Departure", departureTime);
                    eventList.add(departure);
                }
            } else if (e.type.equals("Departure")) {
                qDelay = Math.max(0, qDelay - q.removeLast());
            } else if (e.type.equals("Observer")) {
                qSum += q.size();
                observerCount++;
                idleCount += q.isEmpty() ? 1 : 0;
            }
        }
        double avgNumberOfElementsInQ = (qSum / observerCount);
        double idle = (idleCount / observerCount);

        double res[] = new double[2];
        res[0] = avgNumberOfElementsInQ;
        res[1] = (dropCount/totalPacketCount)*100;
        return res;
    }

    public static double genarateRandom(double lambda) {
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
