package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAnalytics {
    // Totals
    private long totalGames;
    private long totalMoves;
    private long totalFoodEaten;
    private long totalTimeSpent;

    // Averages
    private long averageLength;
    private long averageFoodEaten;
    private long averageMoves;
    private long averageTimeSpent;

    // Longest values
    private long longestLength;
    private long longestFoodEaten;
    private long longestMoves;
    private long longestTimeSpent;

    // Shortest values
    private long shortestLength;
    private long shortestFoodEaten;
    private long shortestMoves;
    private long shortestTimeSpent;
}
