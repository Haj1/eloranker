package com.haj.eloranker;

import java.util.Objects;

public class DoublesPair {
	private EloUser player1;
	private EloUser player2;
	private float successPercentage;

	public DoublesPair(EloUser player1, EloUser player2, float successPercentage) {
		this.player1 = player1;
		this.player2 = player2;
		this.successPercentage = successPercentage;
	}

	public EloUser getPlayer1() {
		return player1;
	}

	public void setPlayer1(EloUser player1) {
		this.player1 = player1;
	}

	public EloUser getPlayer2() {
		return player2;
	}

	public void setPlayer2(EloUser player2) {
		this.player2 = player2;
	}

	public float getSuccessPercentage() {
		return successPercentage;
	}

	public void setSuccessPercentage(float successPercentage) {
		this.successPercentage = successPercentage;
	}

	@Override
	public String toString() {
		return "DoublesPair{" +
				"player1=" + player1 +
				", player2=" + player2 +
				", successPercentage=" + successPercentage +
				'}';
	}

	// Some extra logic so that it doesn't matter which way round player 1 and 2 are
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DoublesPair that = (DoublesPair) o;
		return (Objects.equals(player1, that.player1) &&
				Objects.equals(player2, that.player2)) ||
				(Objects.equals(player1, that.player2) &&
						Objects.equals(player2, that.player1));
	}
}
