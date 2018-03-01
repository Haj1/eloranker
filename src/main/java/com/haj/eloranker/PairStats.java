package com.haj.eloranker;

public class PairStats {
    private float successPercentage;
    private long numberOfGamesWon;
    private long numberOfGamesLost;

    public PairStats(float successPercentage, long numberOfGamesWon, long numberOfGamesLost) {
        this.successPercentage = successPercentage;
        this.numberOfGamesWon = numberOfGamesWon;
        this.numberOfGamesLost = numberOfGamesLost;
    }

    public float getSuccessPercentage() {
        return successPercentage;
    }

    public void setSuccessPercentage(float successPercentage) {
        this.successPercentage = successPercentage;
    }

    public long getNumberOfGamesWon() {
        return numberOfGamesWon;
    }

    public void setNumberOfGamesWon(long numberOfGamesWon) {
        this.numberOfGamesWon = numberOfGamesWon;
    }

    public long getNumberOfGamesLost() {
        return numberOfGamesLost;
    }

    public void setNumberOfGamesLost(long numberOfGamesLost) {
        this.numberOfGamesLost = numberOfGamesLost;
    }
}
