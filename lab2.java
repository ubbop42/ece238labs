import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class lab2 {
    public static void main(String[] args) throws FileNotFoundException {
        double t = 1000; //time
        long n = 10; //nodes
        double a = 10; //rate
        double r = 10; //speedlan
        double l = 10; //length
        double d = 10; //distance
        double s = 10; //propspeed

    }

    public static double[] perstsitance(double a, double t, double r, double l, double d , double s,long n) {
        ArrayList[][] table = new ArrayList[10][10];
        table[0][0] = new ArrayList(); 
        table[0][0].add();

    }

    public static double[] nonPerstsitance(double a, double t, double r, double l, double d , double s) {
    

    }

    public static double generateRandom(double lambda) {
        return -Math.log(1.0 - Math.random()) / lambda;
    }

}