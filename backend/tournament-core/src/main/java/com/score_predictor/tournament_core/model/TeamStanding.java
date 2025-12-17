package com.score_predictor.tournament_core.model;

import lombok.Data;

@Data
public class TeamStanding {
    private final Team team;
    private int points;
    private int matchesPlayed;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    public TeamStanding(Team team) {
        this.team = team;
    }

    public void recordWin(int gf, int ga) {
        this.matchesPlayed++;
        this.won++;
        this.points += 3;
        updateGoals(gf, ga);
    }

    public void recordDraw(int gf, int ga) {
        this.matchesPlayed++;
        this.drawn++;
        this.points += 1;
        updateGoals(gf, ga);
    }

    public void recordLoss(int gf, int ga) {
        this.matchesPlayed++;
        this.lost++;
        updateGoals(gf, ga);
    }

    private void updateGoals(int gf, int ga) {
        this.goalsFor += gf;
        this.goalsAgainst += ga;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    @Override
    public String toString() {
        return String.format("%s | Pts: %d | GD: %d | GF: %d",
                team.getName(), getPoints(), getGoalDifference(), getGoalsFor());
    }
}
