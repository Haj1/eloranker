package com.haj.eloranker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {
	List<Game> findAllByOrderByTimestamp();
	List<Game> findTop10ByOrderByTimestampDesc();
	List<Game> findAllByWinner(EloUser winner);
	List<Game> findAllByLoser(EloUser loser);
}
