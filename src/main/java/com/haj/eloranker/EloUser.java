package com.haj.eloranker;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EloUser {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull @Column(unique = true)
	private String name;
	private Integer elo;
	@Transient
	private List<Game> gamesPlayed = new ArrayList<>();
	@Transient
	private List<DoublesGame> doublesGamesPlayed = new ArrayList<>();

	public EloUser() {
	}

	public EloUser(String name) {
		this.name = name;
		this.elo = 1200;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getElo() {
		return elo;
	}

	public void setElo(Integer elo) {
		this.elo = elo;
	}

	public List<Game> getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(List<Game> gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public List<DoublesGame> getDoublesGamesPlayed() {
		return doublesGamesPlayed;
	}

	public void setDoublesGamesPlayed(List<DoublesGame> doublesGamesPlayed) {
		this.doublesGamesPlayed = doublesGamesPlayed;
	}

	@Override
	public String toString() {
		return "EloUser{" +
				"id=" + id +
				", name='" + name + '\'' +
				", elo=" + elo +
				'}';
	}

}
