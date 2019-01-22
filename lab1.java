import java.util.*;

public class lab1 {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        System.out.print("enter 0 for mm1 or 1 mm1k:");
        boolean isbounded = scanner.nextInt() == 1;

        // Observer events = rand(n)
        System.out.print("n?: ");
        double n = scanner.nextDouble();

        // Arrival events = rand(lambda)
        System.out.print("λ?: ");
        double lambda = scanner.nextDouble();

        // Packet length = rand(L)
        System.out.print("L?: ");
        double l = scanner.nextDouble();

        // Service Time = L/C
        System.out.print("C?: ");
        double c = scanner.nextDouble();

        // Simulation length
        System.out.print("time?: ");
        double t = scanner.nextDouble();

        if (isbounded) {
            System.out.print("k?: ");
            double k = scanner.nextDouble();
            // simulate mm1k
        } else {
            LinkedList<Event> eventList = new LinkedList<Event>();
            Double qDelay = 0.0;
            double currentTime = 0.0;
            long count = 0;
            while (currentTime < t) {
                double delta = genarateRandom(lambda);
                currentTime += delta;
                double serviceTime = genarateRandom(1.0 / l) / c;
                qDelay = Math.max(0, qDelay - delta);
                if (qDelay > 0)
                    count++;
                double departureTime = currentTime + serviceTime + qDelay;
                qDelay += serviceTime;
                Event arrival = new Event("Arrival", currentTime);
                eventList.add(arrival);
                Event departure = new Event("Departure", departureTime);
                eventList.add(departure);
            }
            System.out.println("events size : " + eventList.size());
            System.out.println("n of q delayes : " + count);
            currentTime = 0.0;
            while (currentTime < t) {
                currentTime += genarateRandom(lambda * n);
                Event temp = new Event("Observer", currentTime);
                eventList.add(temp);
            }
            System.out.println("events size : " + eventList.size());
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

    public static double genarateRandom(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }

}
