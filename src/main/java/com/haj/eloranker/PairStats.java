package com.haj.eloranker;

public class PairStats {
    private float successRatio;
    private long numberOfGamesPlayed;

    public PairStats(float successRatio, long numberOfGamesPlayed) {
        this.successRatio = successRatio;
        this.numberOfGamesPlayed = numberOfGamesPlayed;
    }

    public float getSuccessRatio() {
        return successRatio;
    }

    public void setSuccessRatio(float successRatio) {
        this.successRatio = successRatio;
    }

    public long getNumberOfGamesPlayed() {
        return numberOfGamesPlayed;
    }

    public void setNumberOfGamesPlayed(long numberOfGamesPlayed) {
        this.numberOfGamesPlayed = numberOfGamesPlayed;
    }
}
