package paradigmpanic.backend;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

public class Player implements Serializable, Comparable<Player> {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final int score;
	private final Duration timeCompleted;

	public Player(String name, int score, Duration timeCompleted) {
		this.name = Objects.requireNonNullElse(name, "Player");
		this.score = score;
		this.timeCompleted = Objects.requireNonNullElse(timeCompleted, Duration.ZERO);
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

	@Override
	public int compareTo(Player other) {
		int byScore = Integer.compare(other.score, this.score);
		if (byScore != 0) {
			return byScore;
		}
		return this.timeCompleted.compareTo(other.timeCompleted);
	}
}

