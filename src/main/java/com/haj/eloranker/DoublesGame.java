package com.haj.eloranker;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class DoublesGame {
	@Id
	@GeneratedValue
	private Long id;
	@OneToOne
	private EloUser winner1;
	@OneToOne
	private EloUser winner2;
	@OneToOne
	private EloUser loser1;
	@OneToOne
	private EloUser loser2;
	private LocalDate timestamp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EloUser getWinner1() {
		return winner1;
	}

	public void setWinner1(EloUser winner1) {
		this.winner1 = winner1;
	}

	public EloUser getWinner2() {
		return winner2;
	}

	public void setWinner2(EloUser winner2) {
		this.winner2 = winner2;
	}

	public EloUser getLoser1() {
		return loser1;
	}

	public void setLoser1(EloUser loser1) {
		this.loser1 = loser1;
	}

	public EloUser getLoser2() {
		return loser2;
	}

	public void setLoser2(EloUser loser2) {
		this.loser2 = loser2;
	}

	public LocalDate getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "DoublesGame{" +
				"id=" + id +
				", winner1=" + winner1 +
				", winner2=" + winner2 +
				", loser1=" + loser1 +
				", loser2=" + loser2 +
				", timestamp=" + timestamp +
				'}';
	}
}
