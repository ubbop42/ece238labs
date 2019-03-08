#include <stdio.h>
#include <stdlib.h>
#include <math.h> 

float calcErrPercent(float theoretical, float actual){
        return fabs((theoretical)-actual)/theoretical*100;
}

int main(){

        double lambda = 75;
        srand(time(0));
        double sum = 0;
        double nums[1000];

        for (int i = 0; i < 1000; i++) {
                nums[i] = -log(1.0 - ((double)rand())/RAND_MAX) / lambda;;
                sum += nums[i];
        }

        double avg = sum / 1000;
        double stdev = 0;
        for (int i = 0; i < 1000; i++) {
                stdev += pow(nums[i] - avg, 2);
        }

        double variance = sqrt(stdev / (1000-1));

        printf("Expected mean: %f\n", (1/lambda));
        printf("Measured mean: %f\n", avg);
        printf("Error for mean: %0.2f%%\n", calcErrPercent(1/lambda, avg));
        printf("Mearured variance %f\n", variance);
        printf("Error for variance: %0.2f%%\n", calcErrPercent(1/lambda, variance));

}