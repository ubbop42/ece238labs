import java.util.*;

public class lab1{

     public static void main(String []args){
        final Scanner scanner = new Scanner(System.in);
         
        System.out.print("enter 0 for mm1 or 1 mm1k:");
        boolean isbounded = scanner.nextInt() == 1;
    
        System.out.print("n?: ");
        double n = scanner.nextDouble();

        System.out.print("λ?: ");
        double lambda = scanner.nextDouble();

        System.out.print("L?: ");
        double l = scanner.nextDouble();

        System.out.print("C?: ");
        double c = scanner.nextDouble();

        System.out.print("tme?: ");
        double t = scanner.nextDouble();

        if(isbounded){
            System.out.print("k?: ");
            double k = scanner.nextDouble();
            //simulate mm1k
        }
        else{
            //simulate mm1
        }
        
        double sum = 0;
        double nums[] = new double[1000];
        for(int i = 0; i < 1000;i++){
            double rand = genarateRandom(lambda);
            sum += rand;
            nums[i] = rand;
        }
        
        double avg = sum/1000;
        double stdev = 0;
        for(int i = 0 ; i < 1000; i++){
            stdev += Math.pow(nums[i] - avg, 2);
        }
        System.out.println(avg);
        System.out.println(Math.sqrt(stdev/999));
        
        
     }
     
     public static double genarateRandom(double lambda){
         return -Math.log(1.0-Math.random())/lambda;
     }
     
}