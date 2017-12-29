package com.haj.eloranker;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Game {
	@Id @GeneratedValue
	private Long id;
	@OneToOne
	private EloUser winner;
	@OneToOne
	private EloUser loser;
	private LocalDate timestamp;

	public Game() {
	}

	public Game(EloUser winner, EloUser loser) {
		this.winner = winner;
		this.loser = loser;
		this.timestamp = LocalDate.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EloUser getWinner() {
		return winner;
	}

	public void setWinner(EloUser winner) {
		this.winner = winner;
	}

	public EloUser getLoser() {
		return loser;
	}

	public void setLoser(EloUser loser) {
		this.loser = loser;
	}

	public LocalDate getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Game{" +
				"id=" + id +
				", winner=" + winner.getName() +
				", loser=" + loser.getName() +
				", timestamp=" + timestamp +
				'}';
	}
}
