package paradigmpanic.backend;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

public class Player implements Serializable, Comparable<Player> {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final int score;
	private final Duration timeCompleted;
	private final Question.Difficulty difficulty;
	private final Question.Category category;

	public Player(String name, int score, Duration timeCompleted) {
		this(name, score, timeCompleted, Question.Difficulty.EASY, Question.Category.THEORY);
	}

	public Player(String name, int score, Duration timeCompleted, Question.Difficulty difficulty, Question.Category category) {
		this.name = Objects.requireNonNullElse(name, "Player");
		this.score = score;
		this.timeCompleted = Objects.requireNonNullElse(timeCompleted, Duration.ZERO);
		this.difficulty = difficulty == null ? Question.Difficulty.EASY : difficulty;
		this.category = category == null ? Question.Category.THEORY : category;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public Duration getTimeCompleted() {
		return timeCompleted;
	}

	public Question.Difficulty getDifficulty() {
		return difficulty;
	}

	public Question.Category getCategory() {
		return category;
	}

	@Override
	public int compareTo(Player other) {
		int byScore = Integer.compare(other.score, this.score);
		if (byScore != 0) {
			return byScore;
		}
		return this.timeCompleted.compareTo(other.timeCompleted);
	}
}

