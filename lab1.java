import java.util.*;

public class lab1 {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        System.out.print("enter 0 for mm1 or 1 mm1k:");
        boolean isbounded = false;

        // Observer events = rand(n)
        System.out.print("alpha?: ");
        double alpha = 350;

        // Arrival events = rand(lambda)
        System.out.print("λ?: ");
        // double lambda = 75;

        // Packet length = rand(L)
        System.out.print("L?: ");
        double l = 2000;

        // Service Time = L/C
        System.out.print("C?: ");
        double c = 1000000;

        // Simulation length
        System.out.print("time?: ");
        double t = 1000;
        System.out.println("");
        if (isbounded) {
            System.out.print("k?: ");
            double k = scanner.nextDouble();
            // simulate mm1k
        } else {
            for (int lambda = 125; lambda <= 475; lambda += 5) {
                System.out.printf("ρ = %.2f: %.3f\n", lambda * (l / c), simulatemmk(alpha, lambda, l, c, t)[1]);
            }

            System.out.println();
            System.out.printf("ρ = %.2f: %.3f\n", 600 * (l / c), simulatemmk(alpha, 600, l, c, t)[1]);

        }

        // double sum = 0;
        // double nums[] = new double[1000];
        // for (int i = 0; i < 1000; i++) {
        // double rand = genarateRandom(lambda);
        // sum += rand;
        // nums[i] = rand;
        // }

        // double avg = sum / 1000;
        // double stdev = 0;
        // for (int i = 0; i < 1000; i++) {
        // stdev += Math.pow(nums[i] - avg, 2);
        // }
        // System.out.println(avg);
        // System.out.println(Math.sqrt(stdev / 999));
    }

    public static double[] simulatemmk(double alpha, double lambda, double l, double c, double t) {
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
        // System.out.println("avg = " + avgNumberOfElementsInQ);
        // System.out.println("idle = " + idle * 100 + "%");

        double res[] = new double[2];
        res[0] = avgNumberOfElementsInQ;
        res[1] = idle;
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
