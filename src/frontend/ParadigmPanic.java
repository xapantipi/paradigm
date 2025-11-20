package frontend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import paradigmpanic.backend.GameEngine;
import paradigmpanic.backend.Leaderboard;
import paradigmpanic.backend.Player;
import paradigmpanic.backend.Question;
import paradigmpanic.backend.Question.Category;
import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.SimpleJsonLoader;

public class ParadigmPanic extends JFrame {

	private SwingApp titleScreen;
	private HowToPlay instructions;
	private Start gamestart;
	private difficulty difficulty;
	private Main_game_panel game_panel;
	private Finish_w win;
	private Finish_l loss;
	private LeaderBoard lead;
	private SpalshScreen splash;

	private Leaderboard leaderboardStore;
	private List<Question> questionBank = Collections.emptyList();
	private GameEngine currentEngine;
	private String playerName = "Player";

	public ParadigmPanic() {
		setTitle("Paradigm Panic!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1900, 1080);
		setResizable(false);
		leaderboardStore = new Leaderboard();
		questionBank = loadQuestions();

		spalshS();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private List<Question> loadQuestions() {
		try (InputStream is = getClass().getResourceAsStream("/resources/questions.json")) {
			if (is == null) {
				throw new IOException("questions.json missing");
			}
			return List.copyOf(new SimpleJsonLoader().load(is));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to load questions.json", "Paradigm Panic",
					JOptionPane.ERROR_MESSAGE);
			return Collections.emptyList();
		}
	}

	public void spalshS() {
		splash = new SpalshScreen(this);
		setContentPane(splash);
		revalidate();
		repaint();
		pack();
	}

	public void showTitleScreen() {
		titleScreen = new SwingApp(this);
		setContentPane(titleScreen);
		revalidate();
		repaint();
		pack();
	}

	public void showInstructions() {
		instructions = new HowToPlay(this);
		setContentPane(instructions);
		revalidate();
		getContentPane().repaint();
		pack();
	}

	public void showGameStart() {
		gamestart = new Start(this);
		setContentPane(gamestart);
		revalidate();
		repaint();
		pack();
	}

	public void showDifficulty() {
		difficulty = new difficulty(this);
		setContentPane(difficulty);
		revalidate();
		repaint();
		pack();
	}

	public void startGame(Difficulty difficulty, Category category) {
		if (questionBank.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Question bank is empty.", "Paradigm Panic", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			currentEngine = new GameEngine(playerName, difficulty, category, questionBank);
		} catch (IllegalStateException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Paradigm Panic", JOptionPane.ERROR_MESSAGE);
			return;
		}
		showMainpanel(currentEngine);
	}

	public void showMainpanel(GameEngine engine) {
		game_panel = new Main_game_panel(this, engine);
		setContentPane(game_panel);
		revalidate();
		repaint();
		pack();
	}

	public void setPlayerName(String name) {
		if (name == null || name.isBlank()) {
			this.playerName = "Recruit";
		} else {
			this.playerName = name.trim();
		}
	}

	public void handleGameCompletion(GameEngine engine) {
		Player snapshot = engine.toPlayerSnapshot();
		leaderboardStore.record(snapshot);
		if (engine.hasWon()) {
			showWin(snapshot);
		} else {
			showLoss(snapshot);
		}
	}

	public void showWin(Player player) {
		win = new Finish_w(this, player);
		setContentPane(win);
		revalidate();
		repaint();
		pack();
	}

	public void showLoss(Player player) {
		loss = new Finish_l(this, player);
		setContentPane(loss);
		revalidate();
		repaint();
		pack();
	}

	public void showLead() {
		lead = new LeaderBoard(this);
		setContentPane(lead);
		revalidate();
		repaint();
		pack();
	}

	public Leaderboard getLeaderboardStore() {
		return leaderboardStore;
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(ParadigmPanic::new);
	}
}
