package com.haj.eloranker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DoublesGameRepository extends CrudRepository<DoublesGame, Long> {
	List<DoublesGame> findAllByOrderByTimestamp();
	List<DoublesGame> findTop10ByOrderByTimestampDesc();
}
