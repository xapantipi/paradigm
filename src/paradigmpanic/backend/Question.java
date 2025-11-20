package paradigmpanic.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Question {

	public enum Difficulty {
		EASY, MEDIUM, HARD
	}

	public enum Category {
		THEORY, PRACTICAL, MIXED
	}

	public enum Type {
		NORMAL, MINI_BOSS, BONUS
	}

	private final String id;
	private final String text;
	private final List<String> options;
	private final int correctIndex;
	private final int timeLimitSeconds;
	private final String hint;
	private final String codeSnippet;
	private final Difficulty difficulty;
	private final Category category;
	private final Type type;

	public Question(
			String id,
			String text,
			List<String> options,
			int correctIndex,
			int timeLimitSeconds,
			String hint,
			String codeSnippet,
			Difficulty difficulty,
			Category category,
			Type type) {

		this.id = id == null ? "" : id;
		this.text = Objects.requireNonNull(text, "text");
		this.options = List.copyOf(new ArrayList<>(options));
		this.correctIndex = correctIndex;
		this.timeLimitSeconds = timeLimitSeconds;
		this.hint = hint == null ? "" : hint;
		this.codeSnippet = codeSnippet == null ? "" : codeSnippet;
		this.difficulty = Objects.requireNonNullElse(difficulty, Difficulty.EASY);
		this.category = Objects.requireNonNullElse(category, Category.THEORY);
		this.type = Objects.requireNonNullElse(type, Type.NORMAL);
		if (this.options.isEmpty()) {
			throw new IllegalArgumentException("Question must contain options");
		}
		if (this.correctIndex < 0 || this.correctIndex >= this.options.size()) {
			throw new IllegalArgumentException("Correct index out of range");
		}
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public List<String> getOptions() {
		return Collections.unmodifiableList(options);
	}

	public int getCorrectIndex() {
		return correctIndex;
	}

	public int getTimeLimitSeconds() {
		return timeLimitSeconds;
	}

	public String getHint() {
		return hint;
	}

	public boolean hasHint() {
		return !hint.isBlank();
	}

	public String getCodeSnippet() {
		return codeSnippet;
	}

	public boolean hasSnippet() {
		return !codeSnippet.isBlank();
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public Category getCategory() {
		return category;
	}

	public Type getType() {
		return type;
	}
}

