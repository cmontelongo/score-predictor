package com.score_predictor.tournament_core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.score_predictor.tournament_core.business.FifaStandingsComparator;

public class Group {
    private final String name;
    private final Map<Object, TeamStanding> standings;

    public Group(String name, List<Team> teams) {
        if (teams.size() != 4) {
            throw new IllegalArgumentException("Group must contain exactly 4 teams.");
        }
        this.name = name;
        this.standings = teams.stream()
                .map(TeamStanding::new)
                .collect(Collectors.toMap(
                        ts -> ts.getTeam().getId(),
                        Function.identity()));
    }

    @FunctionalInterface
    interface MatchResultProcessor {
        void process(TeamStanding home, TeamStanding away, int hGoals, int aGoals);
    }

    public void addMatchResult(Match match) {
        TeamStanding homeStanding = Optional.ofNullable(standings.get(match.getHomeTeamId()))
                .orElseThrow(() -> new IllegalArgumentException("Home team not found in group: " + this.name));

        TeamStanding awayStanding = Optional.ofNullable(standings.get(match.getAwayTeamId()))
                .orElseThrow(() -> new IllegalArgumentException("Away team not found in group: " + this.name));

        int hGoals = match.getHomeScore();
        int aGoals = match.getAwayScore();

        List<MatchResultProcessor> matchResult = List.of(
                (h, a, hg, ag) -> {
                    h.recordLoss(hg, ag);
                    a.recordWin(ag, hg);
                },
                (h, a, hg, ag) -> {
                    h.recordDraw(hg, ag);
                    a.recordDraw(ag, hg);
                },
                (h, a, hg, ag) -> {
                    h.recordWin(hg, ag);
                    a.recordLoss(ag, hg);
                });

        int matchStrategyIndex = Integer.compare(hGoals, aGoals) + 1;

        matchResult.get(matchStrategyIndex).process(homeStanding, awayStanding, hGoals, aGoals);
    }

    public List<TeamStanding> getSortedStandings() {
        List<TeamStanding> sortedList = new ArrayList<>(standings.values());

        sortedList.sort(new FifaStandingsComparator());

        return Collections.unmodifiableList(sortedList);
    }

    public String getName() {
        return name;
    }
}
