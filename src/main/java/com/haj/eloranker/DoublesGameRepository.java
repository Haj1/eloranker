package com.haj.eloranker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DoublesGameRepository extends CrudRepository<DoublesGame, Long> {
	List<DoublesGame> findAllByOrderByTimestamp();
	List<DoublesGame> findTop10ByOrderByTimestampDesc();
	List<DoublesGame> findByWinner1AndWinner2(EloUser winner1, EloUser winner2);
	List<DoublesGame> findByLoser1AndLoser2(EloUser loser1, EloUser loser2);
	long countByWinner1AndWinner2(EloUser winner1, EloUser winner2);
	long countByLoser1AndLoser2(EloUser loser1, EloUser loser2);
}
