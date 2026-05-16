package com.evaluation.util;

import com.evaluation.dto.VehicleDto;

import java.util.ArrayList;
import java.util.List;

public class KnapsackUtil {

    public static KnapsackResult solveKnapsack(List<VehicleDto> vehicles, int mechanicHours) {
        int n = vehicles.size();
        int[][] dp = new int[n + 1][mechanicHours + 1];

        for (int i = 1; i <= n; i++) {
            VehicleDto v = vehicles.get(i - 1);
            int weight = v.getTimeRequired();
            int value = v.getImpact();

            for (int w = 0; w <= mechanicHours; w++) {
                if (weight <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - weight] + value);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        int maxImpact = dp[n][mechanicHours];
        int res = maxImpact;
        int w = mechanicHours;
        int hoursUsed = 0;
        List<VehicleDto> selectedTasks = new ArrayList<>();

        for (int i = n; i > 0 && res > 0; i--) {
            if (res == dp[i - 1][w]) {
                continue;
            } else {
                VehicleDto v = vehicles.get(i - 1);
                selectedTasks.add(v);
                res -= v.getImpact();
                w -= v.getTimeRequired();
                hoursUsed += v.getTimeRequired();
            }
        }

        return new KnapsackResult(selectedTasks, maxImpact, hoursUsed);
    }

    public static class KnapsackResult {
        public List<VehicleDto> selectedTasks;
        public int maxImpact;
        public int mechanicHoursUsed;

        public KnapsackResult(List<VehicleDto> selectedTasks, int maxImpact, int mechanicHoursUsed) {
            this.selectedTasks = selectedTasks;
            this.maxImpact = maxImpact;
            this.mechanicHoursUsed = mechanicHoursUsed;
        }
    }
}
