package com.haj.eloranker;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Lists;

import java.time.LocalDate;
import java.util.*;

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
		model.addAttribute("pairs", getBestDoublesPairs());
		return "index";
	}

	@PostMapping("/api/eloUsers")
	public String createEloUser(@ModelAttribute EloUser eloUser) {
		if(eloUser.getName() == null || eloUser.getName().equals("")) return "redirect:../";
		// Default ELO Ranking
		eloUser.setElo(1200);
		eloUserRepository.save(eloUser);
		return "redirect:../";
	}

	@GetMapping("/api/eloUsers") @ResponseBody
	public List<EloUser> getEloUsers() {
		List<EloUser> users = eloUserRepository.findAllByOrderByEloDesc();

		// populate games played
		for(EloUser user : users) {
			List<Game> singlesGames = new ArrayList<>();
			singlesGames.addAll(gameRepository.findAllByWinner(user));
			singlesGames.addAll(gameRepository.findAllByLoser(user));
			user.setGamesPlayed(singlesGames);

			List<DoublesGame> doublesGames = new ArrayList<>();
			doublesGames.addAll(doublesGameRepository.findByWinner1(user));
			doublesGames.addAll(doublesGameRepository.findByWinner2(user));
			doublesGames.addAll(doublesGameRepository.findByLoser1(user));
			doublesGames.addAll(doublesGameRepository.findByLoser2(user));
			user.setDoublesGamesPlayed(doublesGames);
		}
		return users;
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
	public String createDoublesGame(@ModelAttribute DoublesGame game) {
		if(game.getWinner1() == null ||
				game.getWinner2() == null ||
				game.getLoser1() == null ||
				game.getLoser2() == null) return "redirect:../";

		game.setTimestamp(LocalDate.now());
		doublesGameRepository.save(game);
		calculateNewElosFromDoubles(game);
		return "redirect:../../";
	}

	@GetMapping("/api/doublesCombos") @ResponseBody
	public List<DoublesPair> getBestDoublesPairs() {
		Iterable<DoublesGame> allDoublesGames = doublesGameRepository.findAll();
		List<DoublesPair> pairs = new ArrayList<>();
		for(DoublesGame game : allDoublesGames) {
			DoublesPair winnerOption1 = new DoublesPair(game.getWinner1(), game.getWinner2(), 0F);

			// protect against duplicates
			if(!pairs.contains(winnerOption1) ){
				pairs.add(new DoublesPair(game.getWinner1(), game.getWinner2(), 0F));
			}

			DoublesPair loserOption1 = new DoublesPair(game.getLoser1(), game.getLoser2(), 0F);
			if(!pairs.contains(loserOption1) ){
				pairs.add(new DoublesPair(game.getLoser1(), game.getLoser2(), 0F));
			}
		}

        // TODO we want to filter out pairs that haven't played hardly any games otherwise it's way too verbose
		// set success ratios of pairs
        ListIterator<DoublesPair> iter = pairs.listIterator();
        while(iter.hasNext()) {
		    DoublesPair pair = iter.next();
			PairStats pairStats = successRatioOfPair(pair);
			if(pairStats.getNumberOfGamesPlayed() > 5) {
                pair.setSuccessPercentage(pairStats.getSuccessRatio() * 100);
            } else {
			    iter.remove();
            }
		}

		// sort pairs in order of success
		pairs.sort(Comparator.comparing(DoublesPair::getSuccessPercentage).reversed());

		return pairs;
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

	private PairStats successRatioOfPair(DoublesPair pair) {
		float successRatio = 0;
		long won = doublesGameRepository.countByWinner1AndWinner2(pair.getPlayer1(), pair.getPlayer2());
		// account for order being the other way around
		won = won + doublesGameRepository.countByWinner1AndWinner2(pair.getPlayer2(), pair.getPlayer1());
		long lost = doublesGameRepository.countByLoser1AndLoser2(pair.getPlayer1(), pair.getPlayer2());
		// account for order being the other way around
		lost = lost + doublesGameRepository.countByLoser1AndLoser2(pair.getPlayer2(), pair.getPlayer1());

		long numberOfGamesPlayed = won + lost;

        // if won is, successRatio should be 0.
        if (won != 0) {
            if (lost == 0) {
                // win is non-zero. lost is zero.
                successRatio = 1;
            } else {
                successRatio = (float) won / (won + lost);
            }
        }
        return new PairStats(successRatio, numberOfGamesPlayed);
	}
}
