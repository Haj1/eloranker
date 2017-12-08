package com.haj.eloranker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

	@Autowired
	private EloUserRepository eloUserRepository;

	@Autowired
	private GameRepository gameRepository;

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("users", getEloUsers());
		model.addAttribute("eloUser", new EloUser());
		model.addAttribute("games", getTop10Games());
		model.addAttribute("game", new Game());
		return "index";
	}

	@PostMapping("/api/eloUsers")
	public String createEloUser(@ModelAttribute EloUser eloUser) {
		if(eloUser.getName() == null || eloUser.getName().equals("")) return "redirect:../";
		eloUser.setElo(1200);
		eloUserRepository.save(eloUser);
		return "redirect:../";
	}

	@GetMapping("/api/eloUsers") @ResponseBody
	public List<EloUser> getEloUsers() {
		return eloUserRepository.findAllByOrderByEloDesc();
	}

	@PostMapping("/api/games")
	public String createGame(@ModelAttribute Game game) {
		if(game.getWinner() == null || game.getLoser() == null) return "redirect:../";
		game.setTimestamp(LocalDate.now());
		gameRepository.save(game);
		calculateNewElos(game);
		return "redirect:../";
	}

	@GetMapping("/api/games") @ResponseBody
	public List<Game> getGames() {
		return gameRepository.findAllByOrderByTimestamp();
	}

	@GetMapping("/api/top10Games") @ResponseBody
	public List<Game> getTop10Games() {
		List<Game> games = gameRepository.findTop10ByOrderByTimestampAsc();
		return games;
	}

	private void calculateNewElos(Game game) {
		boolean isNewbie = false;
		if(game.getWinner().getGamesPlayed().size() <= 3) isNewbie = true;

		int newWinnerElo = EloLogic.getNewRating(game.getWinner().getElo(), game.getLoser().getElo(), 1, isNewbie);
		EloUser winner = eloUserRepository.findOne(game.getWinner().getId());
		winner.setElo(newWinnerElo);

		int newLoserElo = EloLogic.getNewRating(game.getLoser().getElo(), game.getWinner().getElo(), 0, isNewbie);
		EloUser loser = eloUserRepository.findOne(game.getLoser().getId());
		loser.setElo(newLoserElo);

		eloUserRepository.save(winner);
		eloUserRepository.save(loser);
	}
}
