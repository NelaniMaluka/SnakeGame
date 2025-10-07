package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionData {
    private long id;
    private long score;
    private int length;
    private int foodEaten;
    private int moves;
    private long timeSpentMillis;
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    // Convert the object to a CSV row
    public String[] toCsvRow() {
        return new String[]{
                String.valueOf(id),
                String.valueOf(score),
                String.valueOf(length),
                String.valueOf(foodEaten),
                String.valueOf(timeSpentMillis),
                String.valueOf(moves),
                date.toString()
        };
    }
}
