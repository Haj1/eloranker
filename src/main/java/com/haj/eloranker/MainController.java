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

	@Autowired
	private DoublesGameRepository doublesGameRepository;

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("users", getEloUsers());
		model.addAttribute("eloUser", new EloUser());
		model.addAttribute("games", getTop10Games());
		model.addAttribute("game", new Game());
		model.addAttribute("doublesGame", new DoublesGame());
		model.addAttribute("doublesGames", getTop10DoublesGames());
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

	@PostMapping("/api/games/singles")
	public String createSinglesGame(@ModelAttribute Game game) {
		if(game.getWinner() == null || game.getLoser() == null) return "redirect:../";
		game.setTimestamp(LocalDate.now());
		gameRepository.save(game);
		calculateNewElosFromSingles(game);
		return "redirect:../../";
	}

	@PostMapping("/api/games/doubles")
	public String createDoublesGame(@ModelAttribute DoublesGame doublesGame) {
		if(doublesGame.getWinner1() == null ||
				doublesGame.getWinner2() == null ||
				doublesGame.getLoser1() == null ||
				doublesGame.getLoser2() == null) return "redirect:../";

		doublesGame.setTimestamp(LocalDate.now());
		doublesGameRepository.save(doublesGame);
		calculateNewElosFromDoubles(doublesGame);
		return "redirect:../../";
	}

	@GetMapping("/api/games") @ResponseBody
	public List<Game> getGames() {
		return gameRepository.findAllByOrderByTimestamp();
	}

	@GetMapping("/api/top10Games") @ResponseBody
	public List<Game> getTop10Games() {
		return gameRepository.findTop10ByOrderByTimestampDesc();
	}

	@GetMapping("/api/doublesGames") @ResponseBody
	public List<DoublesGame> getDoublesGames() {
		return doublesGameRepository.findAllByOrderByTimestamp();
	}


	@GetMapping("/api/top10DoublesGames") @ResponseBody
	public List<DoublesGame> getTop10DoublesGames() {
		return doublesGameRepository.findTop10ByOrderByTimestampDesc();
	}

	private void calculateNewElosFromSingles(Game game) {
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

	private void calculateNewElosFromDoubles(DoublesGame doublesGame) {
		int winnersAverageElo = (doublesGame.getWinner1().getElo() + doublesGame.getWinner2().getElo()) / 2;
		int losersAverageElo = (doublesGame.getLoser1().getElo() + doublesGame.getLoser2().getElo()) / 2;

		// ignore newbie bonus for doubles?
		int newWinnersAverageElo = EloLogic.getNewRating(winnersAverageElo, losersAverageElo, 1, false);

		// work out difference between old elo and new elo
		int eloDiff = newWinnersAverageElo - winnersAverageElo;
		System.out.println("ELO Diff: " + eloDiff);

		EloUser winner1 = eloUserRepository.findOne(doublesGame.getWinner1().getId());
		EloUser winner2 = eloUserRepository.findOne(doublesGame.getWinner2().getId());
		EloUser loser1 = eloUserRepository.findOne(doublesGame.getLoser1().getId());
		EloUser loser2 = eloUserRepository.findOne(doublesGame.getLoser2().getId());

		// update each player's elo
		winner1.setElo(winner1.getElo() + eloDiff);
		winner2.setElo(winner2.getElo() + eloDiff);
		loser1.setElo(loser1.getElo() - eloDiff);
		loser2.setElo(loser2.getElo() - eloDiff);

		eloUserRepository.save(winner1);
		eloUserRepository.save(winner2);
		eloUserRepository.save(loser1);
		eloUserRepository.save(loser2);
	}
}
