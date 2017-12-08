package com.haj.eloranker;

public class EloLogic {

	/** Default ELO, K is the development coefficient. (FIDE System)
	 K = 30 for a player new to the rating list until he has completed events with a total of at least 30 games.
	 K = 20 for RAPID and BLITZ ratings all players.
	 K = 15 as long as a player`s rating remains under 2400.
	 K = 10 once a player`s published rating has reached 2400, and he has also completed events with a total of at least 30 games. Thereafter it remains permanently at 10.
	 */
	private static final double ELO_K_FACTOR_NEWBIE =   30.0;
	private static final double ELO_K_FACTOR_DOWN2400 =   15.0;
	private static final double ELO_K_FACTOR_UP2400 =    10.0;
	/** Default ELO k factor. */
	private static final double DEFAULT_ELO_K_FACTOR =   30.0;

	/**
	 * Get new rating.
	 *
	 * @param rating
	 *            Rating of either the current player or the average of the
	 *            current team.
	 * @param opponentRating
	 *            Rating of either the opponent player or the average of the
	 *            opponent team or teams.
	 * @param score
	 *            Score: 0=Loss 0.5=Draw 1.0=Win
	 * @return the new rating
	 */
	public static int getNewRating(int rating, int opponentRating, double score, boolean isNewbie) {
		double kFactor       = getKFactor(rating, isNewbie);
		double expectedScore = getExpectedScore(rating, opponentRating);
		int    newRating     = calculateNewRating(rating, score, expectedScore, kFactor);

		return newRating;
	}

	/**
	 * Calculate the new rating based on the ELO standard formula.
	 * newRating = oldRating + constant * (score - expectedScore)
	 *
	 * @param oldRating  Old Rating
	 * @param score   Score
	 * @param expectedScore Expected Score
	 * @return    the new rating of the player
	 */
	private static int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
		return oldRating + (int) (kFactor * (score - expectedScore));
	}

	/**
	 * This is the standard chess constant.  This constant can differ
	 * based on different games.  The higher the constant the faster
	 * the rating will grow.  That is why for this standard chess method,
	 * the constant is higher for weaker players and lower for stronger
	 * players.
	 *
	 * @param rating  Rating
	 * @return    Constant
	 */
	private static double getKFactor (int rating, boolean isNewbie) {

		if (isNewbie) {
			return ELO_K_FACTOR_NEWBIE;
		} else if(rating < 2400) {
			return ELO_K_FACTOR_DOWN2400;
		} else if(rating >= 2400) {
			return ELO_K_FACTOR_UP2400;
		}
		return DEFAULT_ELO_K_FACTOR;
	}

	/**
	 * Get expected score based on two players.  If more than two players
	 * are competing, then opponentRating will be the average of all other
	 * opponent's ratings.  If there is two teams against each other, rating
	 * and opponentRating will be the average of those players.
	 *
	 * @param rating   Rating
	 * @param opponentRating Opponent(s) rating
	 * @return     the expected score
	 */
	private static double getExpectedScore (int rating, int opponentRating) {
		return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
	}


}
