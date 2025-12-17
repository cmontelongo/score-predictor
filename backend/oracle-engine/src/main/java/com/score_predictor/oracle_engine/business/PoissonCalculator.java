package com.score_predictor.oracle_engine.business;

import org.springframework.stereotype.Component;

@Component
public class PoissonCalculator {

    /**
     * P(k; λ) = (λ^k * e^-λ) / k!
     */
    public double getProbability(int k, double lambda) {
        return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
    }

    public double[] calculateMatchOutcomeProbabilities(double homeExpectedGoals, double awayExpectedGoals) {
        // [0]=Home, [1]=Draw, [2]=Away
        double[] outcomes = new double[3];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double prob = getProbability(i, homeExpectedGoals) * getProbability(j, awayExpectedGoals);

                int index = Integer.compare(j, i) + 1;

                outcomes[index] += prob;
            }
        }

        return outcomes;
    }

    private long factorial(int n) {
        if (n <= 1)
            return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
