package paradigmpanic.backend;

import java.util.EnumMap;
import java.util.Map;

import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.Question.Type;

/**
 * Centralizes all tunable values that affect gameplay balance.
 */
public final class GameSettings {

	public static final int SCORE_NORMAL = 1;
	public static final int SCORE_MINIBOSS = 2;

	public static final Map<Difficulty, Integer> STARTING_RP = difficultyMap(12, 15, 18);
	public static final Map<Difficulty, Integer> DEDUCTION = difficultyMap(-1, -2, -3);
	public static final Map<Difficulty, Integer> BOSS_DEDUCTION = difficultyMap(-2, -3, -4);
	public static final Map<Difficulty, Integer> SCORE_BY_DIFFICULTY = difficultyMap(1, 2, 3);

	public static final int PEER_HINT_COOLDOWN = 2;
	public static final int LIFELINE_5050_COOLDOWN = 3;

	private GameSettings() {
	}

	public static int getStartingRp(Difficulty difficulty) {
		return STARTING_RP.getOrDefault(difficulty, STARTING_RP.get(Difficulty.EASY));
	}

	public static int getScoreForQuestion(Type type) {
		return type == Type.MINI_BOSS ? SCORE_MINIBOSS : SCORE_NORMAL;
	}

	public static int getScoreForDifficulty(Difficulty difficulty) {
		return SCORE_BY_DIFFICULTY.getOrDefault(difficulty, SCORE_BY_DIFFICULTY.get(Difficulty.EASY));
	}

	public static int getDeduction(Difficulty difficulty, Type type) {
		boolean boss = type == Type.MINI_BOSS;
		Map<Difficulty, Integer> table = boss ? BOSS_DEDUCTION : DEDUCTION;
		return table.getOrDefault(difficulty, table.get(Difficulty.EASY));
	}

	private static Map<Difficulty, Integer> difficultyMap(int easy, int medium, int hard) {
		EnumMap<Difficulty, Integer> map = new EnumMap<>(Difficulty.class);
		map.put(Difficulty.EASY, easy);
		map.put(Difficulty.MEDIUM, medium);
		map.put(Difficulty.HARD, hard);
		return map;
	}
}

