package com.haj.eloranker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EloUserRepository extends CrudRepository<EloUser, Long> {
	List<EloUser> findAllByOrderByEloDesc();
}
