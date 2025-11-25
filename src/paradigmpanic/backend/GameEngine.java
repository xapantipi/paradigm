package paradigmpanic.backend;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import paradigmpanic.backend.Question.Category;
import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.Question.Type;

public class GameEngine {

	public enum Lifeline {
		PEER_HINT,
		FIFTY_FIFTY
	}

	private static final int[] MINI_BOSS_INDICES = { 4, 9, 14 };
	private static final int[] BONUS_INDICES = { 5, 10, 12 };
	private static final long SPEED_BONUS_WINDOW_MS = 10_000L;
	private static final int SPEED_BONUS_POINTS = 1;

	private final String playerName;
	private final Difficulty difficulty;
	private final Category categoryFilter;
	private final List<Question> questions;
	private final EnumMap<Lifeline, Integer> lifelineCooldowns = new EnumMap<>(Lifeline.class);
	private final Random random = new Random();
	private final long startedAt;

	private Question currentQuestion;
	private int questionIndex;
	private int reputationPoints;
	private boolean immune;
	private boolean gameOver;
	private boolean won;
	private long finishedAt;
	private long questionStartedAt;

	public GameEngine(String playerName, Difficulty difficulty, Category category, List<Question> questionBank) {
		this.playerName = playerName == null || playerName.isBlank() ? "Player" : playerName.trim();
		this.difficulty = difficulty == null ? Difficulty.EASY : difficulty;
		this.categoryFilter = category == null ? Category.THEORY : category;
		this.questions = prepareQuestions(questionBank);
		if (this.questions.isEmpty()) {
			throw new IllegalStateException("No questions available for the selected filters.");
		}
		this.reputationPoints = GameSettings.getStartingRp(this.difficulty);
		this.currentQuestion = this.questions.get(0);
		this.questionIndex = 0;
		this.startedAt = System.currentTimeMillis();
		this.finishedAt = this.startedAt;
		this.questionStartedAt = this.startedAt;
		lifelineCooldowns.put(Lifeline.PEER_HINT, 0);
		lifelineCooldowns.put(Lifeline.FIFTY_FIFTY, 0);
	}

	private List<Question> prepareQuestions(List<Question> questionBank) {
		List<Question> filtered = new ArrayList<>();
		if (questionBank != null) {
			for (Question question : questionBank) {
				boolean difficultyMatch = question.getDifficulty() == difficulty;
				boolean categoryMatch = categoryFilter == Category.MIXED || question.getCategory() == categoryFilter;
				if (difficultyMatch && categoryMatch) {
					filtered.add(question);
				}
			}
		}
		if (filtered.isEmpty() && questionBank != null) {
			filtered.addAll(questionBank);
		}
		Collections.shuffle(filtered, random);
		enforceFixedQuestionStructure(filtered);
		return filtered;
	}

	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public int getReputationPoints() {
		return reputationPoints;
	}

	public boolean isImmune() {
		return immune;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean hasWon() {
		return won;
	}

	public boolean hasMoreQuestions() {
		return questionIndex + 1 < questions.size();
	}

	public AnswerResult submitAnswer(int selectedIndex) {
		ensureActiveQuestion();
		Question question = currentQuestion;
		long answerTime = System.currentTimeMillis();
		boolean correct = selectedIndex == question.getCorrectIndex();
		if (selectedIndex < 0 || selectedIndex >= question.getOptions().size()) {
			correct = false;
		}

		if (correct) {
			int baseScore = GameSettings.getScoreForDifficulty(difficulty);
			if (question.getType() == Type.MINI_BOSS) {
				baseScore *= 2;
			}
			reputationPoints += baseScore;
			if (answerTime - questionStartedAt <= SPEED_BONUS_WINDOW_MS) {
				reputationPoints += SPEED_BONUS_POINTS;
			}
			if (question.getType() == Type.BONUS) {
				immune = true;
			}
			if (question.getType() == Type.MINI_BOSS) {
				restoreOneLifeline();
			}
		} else {
			if (immune) {
				immune = false;
			} else {
				reputationPoints += GameSettings.getDeduction(difficulty, question.getType());
			}
		}

		if (reputationPoints <= 0) {
			gameOver = true;
			won = false;
			finishedAt = System.currentTimeMillis();
		} else if (!hasMoreQuestions()) {
			gameOver = true;
			won = correct;
			finishedAt = System.currentTimeMillis();
		}

		return new AnswerResult(correct, question.getCorrectIndex(), reputationPoints, gameOver, won);
	}

	public Question nextQuestion() {
		if (gameOver) {
			return null;
		}
		if (!hasMoreQuestions()) {
			currentQuestion = null;
			return null;
		}
		questionIndex++;
		currentQuestion = questions.get(questionIndex);
		decrementCooldowns();
		questionStartedAt = System.currentTimeMillis();
		return currentQuestion;
	}

	public int[] useLifeline5050() {
		ensureActiveQuestion();
		if (!isLifelineReady(Lifeline.FIFTY_FIFTY)) {
			throw new IllegalStateException("50:50 is still recharging.");
		}
		List<Integer> wrongIndices = new ArrayList<>();
		for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
			if (i != currentQuestion.getCorrectIndex()) {
				wrongIndices.add(i);
			}
		}
		Collections.shuffle(wrongIndices, random);
		int[] blocked = new int[Math.min(2, wrongIndices.size())];
		for (int i = 0; i < blocked.length; i++) {
			blocked[i] = wrongIndices.get(i);
		}
		setCooldown(Lifeline.FIFTY_FIFTY, GameSettings.LIFELINE_5050_COOLDOWN);
		return blocked;
	}

	public String useLifelineHint() {
		ensureActiveQuestion();
		if (!isLifelineReady(Lifeline.PEER_HINT)) {
			throw new IllegalStateException("Intern is still thinking.");
		}
		if (!currentQuestion.hasHint()) {
			throw new IllegalStateException("No hint for this question.");
		}
		setCooldown(Lifeline.PEER_HINT, GameSettings.PEER_HINT_COOLDOWN);
		return currentQuestion.getHint();
	}

	public boolean isLifelineReady(Lifeline lifeline) {
		return lifelineCooldowns.getOrDefault(lifeline, 0) == 0;
	}

	private void setCooldown(Lifeline lifeline, int value) {
		lifelineCooldowns.put(lifeline, Math.max(0, value));
	}

	private void decrementCooldowns() {
		for (Lifeline lifeline : lifelineCooldowns.keySet()) {
			int value = lifelineCooldowns.get(lifeline);
			if (value > 0) {
				lifelineCooldowns.put(lifeline, value - 1);
			}
		}
	}

	private void ensureActiveQuestion() {
		if (currentQuestion == null) {
			throw new IllegalStateException("No active question.");
		}
	}

	public Player toPlayerSnapshot() {
		long endTime = gameOver ? finishedAt : System.currentTimeMillis();
		Duration elapsed = Duration.ofMillis(Math.max(0, endTime - startedAt));
		return new Player(playerName, reputationPoints, elapsed, difficulty, categoryFilter);
	}

	public Duration getElapsedTime() {
		long reference = gameOver ? finishedAt : System.currentTimeMillis();
		return Duration.ofMillis(Math.max(0, reference - startedAt));
	}

	public void forceGameOver(boolean won) {
		this.gameOver = true;
		this.won = won;
		this.finishedAt = System.currentTimeMillis();
	}

	public static final class AnswerResult {
		private final boolean correct;
		private final int correctIndex;
		private final int updatedPoints;
		private final boolean gameOver;
		private final boolean won;

		private AnswerResult(boolean correct, int correctIndex, int updatedPoints, boolean gameOver, boolean won) {
			this.correct = correct;
			this.correctIndex = correctIndex;
			this.updatedPoints = updatedPoints;
			this.gameOver = gameOver;
			this.won = won;
		}

		public boolean isCorrect() {
			return correct;
		}

		public int getCorrectIndex() {
			return correctIndex;
		}

		public int getUpdatedPoints() {
			return updatedPoints;
		}

		public boolean isGameOver() {
			return gameOver;
		}

		public boolean hasWon() {
			return won;
		}
	}

	private void enforceFixedQuestionStructure(List<Question> list) {
		boolean[] designated = new boolean[list.size()];
		for (int index : MINI_BOSS_INDICES) {
			if (replaceQuestionType(list, index, Type.MINI_BOSS)) {
				designated[index] = true;
			} else if (index >= 0 && index < designated.length) {
				designated[index] = true;
			}
		}
		for (int index : BONUS_INDICES) {
			if (replaceQuestionType(list, index, Type.BONUS)) {
				designated[index] = true;
			} else if (index >= 0 && index < designated.length) {
				designated[index] = true;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (designated[i]) {
				continue;
			}
			Question question = list.get(i);
			if (question.getType() != Type.NORMAL) {
				list.set(i, cloneWithType(question, Type.NORMAL));
			}
		}
	}

	private boolean replaceQuestionType(List<Question> list, int index, Type type) {
		if (index < 0 || index >= list.size()) {
			return false;
		}
		Question original = list.get(index);
		if (original.getType() == type) {
			return true;
		}
		list.set(index, cloneWithType(original, type));
		return true;
	}

	private Question cloneWithType(Question original, Type type) {
		return new Question(
				original.getId(),
				original.getText(),
				original.getOptions(),
				original.getCorrectIndex(),
				original.getTimeLimitSeconds(),
				original.getHint(),
				original.getCodeSnippet(),
				original.getDifficulty(),
				original.getCategory(),
				type);
 	}

	private void restoreOneLifeline() {
		for (Lifeline lifeline : Lifeline.values()) {
			int cooldown = lifelineCooldowns.getOrDefault(lifeline, 0);
			if (cooldown > 0) {
				lifelineCooldowns.put(lifeline, Math.max(0, cooldown - 1));
				return;
			}
		}
	}
}

