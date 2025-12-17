package com.score_predictor.tournament_core.business;

import java.util.Comparator;

import com.score_predictor.tournament_core.model.TeamStanding;

public class FifaStandingsComparator implements Comparator<TeamStanding> {

    @Override
    public int compare(TeamStanding t1, TeamStanding t2) {
        return Comparator.comparingInt(TeamStanding::getPoints)
                .thenComparingInt(TeamStanding::getGoalDifference)
                .thenComparingInt(TeamStanding::getGoalsFor)
                .reversed()
                .compare(t1, t2);
    }
}
