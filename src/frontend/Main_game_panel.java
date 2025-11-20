package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import paradigmpanic.backend.GameEngine;
import paradigmpanic.backend.Question;

public class Main_game_panel extends JPanel {

	private final ParadigmPanic frame;
	private final GameEngine engine;

	private ImagePanel bkgholder;
	private Image img;

	private JButton intern_b, fifty_b, code_b;
	private JButton ans_1b, ans_2b, ans_3b, ans_4b;
	private JButton[] answerButtons;

	private ImageIcon intern_n, fifty_n, code_n;
	private ImageIcon disabled_lifeline, answer_idle, answer_focus, correct_icon, wrong_icon;

	private JLabel timer;
	private JLabel points;
	private JLabel hints;
	private JLabel ques;
	private JPanel questionPanel;
	private ImagePanel t_screen, r_screen, h_screen;
	private Font font, g_font, snippetFont;

	private Timer countdownTimer;
	private int secondsRemaining;
	private boolean acceptingInput = true;

	public Main_game_panel(ParadigmPanic frame, GameEngine engine) {
		this.frame = frame;
		this.engine = engine;
		loadGameFontIfAvailable();
		setupBackground();
		setupScorePanels();
		setupHintPanel();
		setupButtons();
		setupQuestionArea();
		layoutComponents();
		displayQuestion(engine.getCurrentQuestion());
	}

	private void loadGameFontIfAvailable() {
		if (font == null) {
			font = loadFont("/resources/font/pixelFont.ttf", 25f);
		}
		if (g_font == null) {
			g_font = loadFont("/resources/font/LowresPixel.otf", 25f);
		}
		if (snippetFont == null) {
			snippetFont = loadFont("/resources/font/pixel.ttf", 20f);
		}

		if (font == null) {
			font = new Font("Monospaced", Font.BOLD, 24);
		}
		if (g_font == null) {
			g_font = font;
		}
		if (snippetFont == null) {
			snippetFont = g_font.deriveFont(20f);
		}
	}

	private Font loadFont(String path, float size) {
		try (InputStream is = getClass().getResourceAsStream(path)) {
			if (is != null) {
				return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
			}
		} catch (Exception ignore) {
		}
		return null;
	}

	private void setupBackground() {
		img = new ImageIcon(getClass().getResource("/resources/images/question page.png")).getImage();
		bkgholder = new ImagePanel(img);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkgholder, BorderLayout.CENTER);
		bkgholder.setLayout(null);
	}

	private void setupScorePanels() {
		Image screen = new ImageIcon(getClass().getResource("/resources/images/screen.png")).getImage();
		t_screen = new ImagePanel(screen);
		r_screen = new ImagePanel(screen);

		timer = new JLabel("00:00");
		timer.setFont(font);
		timer.setForeground(Color.WHITE);
		timer.setHorizontalAlignment(SwingConstants.CENTER);
		t_screen.setLayout(new BorderLayout());
		t_screen.add(timer, BorderLayout.CENTER);

		points = new JLabel("0 PTS");
		points.setFont(font);
		points.setForeground(Color.WHITE);
		points.setHorizontalAlignment(SwingConstants.CENTER);
		r_screen.setLayout(new BorderLayout());
		r_screen.add(points, BorderLayout.CENTER);
	}

	private void setupHintPanel() {
		Image ask = new ImageIcon(getClass().getResource("/resources/images/ask bubble.png")).getImage();
		h_screen = new ImagePanel(ask);
		hints = new JLabel("Intern is on standby...");
		hints.setFont(g_font);
		hints.setForeground(Color.BLACK);
		hints.setHorizontalAlignment(SwingConstants.CENTER);
		h_screen.setLayout(new BorderLayout());
		h_screen.add(hints, BorderLayout.CENTER);
	}

	private void setupButtons() {
		intern_n = Icons.icon("ask.png", 250, 90);
		fifty_n = Icons.icon("50.png", 250, 90);
		code_n = Icons.icon("code.png", 250, 90);
		disabled_lifeline = Icons.icon("RECHARGING.png", 250, 90);
		answer_idle = Icons.icon("answer_button.png", 460, 125);
		answer_focus = Icons.icon("answer_button.png", 470, 140);
		correct_icon = Icons.icon("correct_answer.png", 470, 140);
		wrong_icon = Icons.icon("wrong_answer.png", 470, 140);

		intern_b = new JButton(intern_n);
		fifty_b = new JButton(fifty_n);
		code_b = new JButton(code_n);

		ans_1b = new JButton("", answer_idle);
		ans_2b = new JButton("", answer_idle);
		ans_3b = new JButton("", answer_idle);
		ans_4b = new JButton("", answer_idle);

		answerButtons = new JButton[] { ans_1b, ans_2b, ans_3b, ans_4b };

		for (JButton button : new JButton[] { intern_b, fifty_b, code_b }) {
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusable(false);
			button.setOpaque(false);
		}

		for (JButton button : answerButtons) {
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusable(false);
			button.setOpaque(false);
			button.setHorizontalTextPosition(SwingConstants.CENTER);
			button.setVerticalTextPosition(SwingConstants.CENTER);
			button.setFont(g_font);
			button.setForeground(Color.BLACK);
		}

		intern_b.addActionListener(e -> triggerHint());
		fifty_b.addActionListener(e -> trigger5050());
		code_b.addActionListener(e -> showSnippet());

		for (int i = 0; i < answerButtons.length; i++) {
			final int index = i;
			answerButtons[i].addActionListener(e -> handleAnswer(index));
			answerButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					if (answerButtons[index].isEnabled() && acceptingInput) {
						answerButtons[index].setIcon(answer_focus);
					}
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					if (answerButtons[index].isEnabled() && acceptingInput) {
						answerButtons[index].setIcon(answer_idle);
					}
				}
			});
		}
	}

	private void setupQuestionArea() {
		questionPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (g_font != null) {
					g.setFont(g_font);
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				}
			}
		};
		questionPanel.setOpaque(false);
		questionPanel.setLayout(new BorderLayout());
		ques = new JLabel("", SwingConstants.CENTER);
		ques.setFont(g_font);
		ques.setForeground(Color.BLACK);
		ques.setVerticalAlignment(SwingConstants.CENTER);
		ques.setHorizontalAlignment(SwingConstants.CENTER);
		ques.setOpaque(false);
		questionPanel.add(ques, BorderLayout.CENTER);
	}

	private void layoutComponents() {
		intern_b.setBounds(50, 125, 250, 90);
		fifty_b.setBounds(50, 225, 250, 90);
		code_b.setBounds(50, 325, 250, 90);

		ans_1b.setBounds(317, 520, 460, 125);
		ans_2b.setBounds(754, 520, 460, 125);
		ans_3b.setBounds(317, 620, 460, 125);
		ans_4b.setBounds(754, 620, 460, 125);

		t_screen.setBounds(1250, 150, 200, 80);
		r_screen.setBounds(1250, 300, 200, 80);
		questionPanel.setBounds(375, 112, 792, 385);
		h_screen.setBounds(0, 450, 350, 450);

		bkgholder.add(intern_b);
		bkgholder.add(fifty_b);
		bkgholder.add(code_b);
		bkgholder.add(ans_1b);
		bkgholder.add(ans_2b);
		bkgholder.add(ans_3b);
		bkgholder.add(ans_4b);
		bkgholder.add(t_screen);
		bkgholder.add(r_screen);
		bkgholder.add(questionPanel);
		bkgholder.add(h_screen);
	}

	private void displayQuestion(Question question) {
		if (question == null) {
			return;
		}
		acceptingInput = true;
		hints.setText("Intern is on standby...");
		for (JButton button : answerButtons) {
			button.setEnabled(true);
			button.setIcon(answer_idle);
			button.setForeground(Color.BLACK);
		}
		ques.setText(formatQuestionText(question.getText()));
		List<String> options = question.getOptions();
		for (int i = 0; i < answerButtons.length; i++) {
			String text = i < options.size() ? options.get(i) : "";
			answerButtons[i].setText(text);
			answerButtons[i].setEnabled(i < options.size());
			if (i >= options.size()) {
				answerButtons[i].setIcon(answer_idle);
			}
		}
		points.setText(engine.getReputationPoints() + " PTS");
		secondsRemaining = question.getTimeLimitSeconds();
		updateTimerLabel();
		restartCountdown();
		updateLifelineAvailability(question.hasSnippet());
	}

	private void restartCountdown() {
		if (countdownTimer != null) {
			countdownTimer.stop();
		}
		countdownTimer = new Timer(1000, e -> {
			secondsRemaining--;
			if (secondsRemaining <= 0) {
				timer.setText("00:00");
				countdownTimer.stop();
				handleAnswer(-1);
			} else {
				updateTimerLabel();
			}
		});
		countdownTimer.start();
	}

	private void updateTimerLabel() {
		timer.setText(String.format("00:%02d", Math.max(0, secondsRemaining)));
	}

	private void updateLifelineAvailability(boolean hasSnippet) {
		boolean hintReady = engine.isLifelineReady(GameEngine.Lifeline.PEER_HINT);
		boolean fiftyReady = engine.isLifelineReady(GameEngine.Lifeline.FIFTY_FIFTY);

		intern_b.setEnabled(hintReady);
		intern_b.setIcon(hintReady ? intern_n : disabled_lifeline);
		fifty_b.setEnabled(fiftyReady);
		fifty_b.setIcon(fiftyReady ? fifty_n : disabled_lifeline);
		code_b.setVisible(hasSnippet);
		code_b.setEnabled(hasSnippet);
		code_b.setIcon(hasSnippet ? code_n : disabled_lifeline);
	}

	private void handleAnswer(int index) {
		if (!acceptingInput) {
			return;
		}
		acceptingInput = false;
		if (countdownTimer != null) {
			countdownTimer.stop();
		}
		GameEngine.AnswerResult result;
		try {
			result = engine.submitAnswer(index);
		} catch (IllegalStateException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "No Question", JOptionPane.ERROR_MESSAGE);
			return;
		}
		showAnswerFeedback(result, index);
		Timer delay = new Timer(1000, evt -> {
			if (result.isGameOver()) {
				frame.handleGameCompletion(engine);
			} else {
				Question next = engine.nextQuestion();
				displayQuestion(next);
			}
		});
		delay.setRepeats(false);
		delay.start();
	}

	private void showAnswerFeedback(GameEngine.AnswerResult result, int selectedIndex) {
		int correctIndex = result.getCorrectIndex();
		for (int i = 0; i < answerButtons.length; i++) {
			JButton button = answerButtons[i];
			button.setEnabled(false);
			if (i == correctIndex) {
				button.setIcon(correct_icon);
			} else if (i == selectedIndex) {
				button.setIcon(wrong_icon);
			} else {
				button.setIcon(answer_idle);
			}
		}
		points.setText(result.getUpdatedPoints() + " PTS");
	}

	private void triggerHint() {
		try {
			String hintText = engine.useLifelineHint();
			hints.setText("<html>" + hintText + "</html>");
			updateLifelineAvailability(engine.getCurrentQuestion().hasSnippet());
		} catch (IllegalStateException ex) {
			hints.setText(ex.getMessage());
		}
	}

	private void trigger5050() {
		try {
			int[] toDisable = engine.useLifeline5050();
			for (int index : toDisable) {
				if (index >= 0 && index < answerButtons.length) {
					answerButtons[index].setEnabled(false);
					answerButtons[index].setForeground(Color.GRAY);
					answerButtons[index].setIcon(answer_idle);
				}
			}
			hints.setText("Two interns crossed out the noise!");
			updateLifelineAvailability(engine.getCurrentQuestion().hasSnippet());
		} catch (IllegalStateException ex) {
			hints.setText(ex.getMessage());
		}
	}

	private void showSnippet() {
		Question question = engine.getCurrentQuestion();
		if (question == null || !question.hasSnippet()) {
			return;
		}
		JTextArea area = new JTextArea(question.getCodeSnippet());
		area.setEditable(false);
		area.setWrapStyleWord(true);
		area.setLineWrap(true);
		area.setFont(snippetFont);
		area.setForeground(Color.WHITE);
		area.setBackground(new Color(26, 26, 26));
		area.setBorder(null);

		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setPreferredSize(new Dimension(500, 300));
		scrollPane.setBorder(null);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(36, 36, 36));
		panel.add(scrollPane, BorderLayout.CENTER);

		JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), panel, "Code Snippet",
				JOptionPane.PLAIN_MESSAGE, Icons.icon("code.png", 64, 32));
	}

	private String formatQuestionText(String text) {
		if (text == null) {
			return "";
		}
		FontMetrics fm = ques.getFontMetrics(g_font);
		int maxWidth = questionPanel.getWidth() - 40;
		if (maxWidth <= 0) {
			return "<html>" + text + "</html>";
		}
		StringBuilder builder = new StringBuilder("<html><div style='text-align:center;'>");
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();
		for (String word : words) {
			String testLine = line.length() == 0 ? word : line + " " + word;
			if (fm.stringWidth(testLine) > maxWidth) {
				builder.append(line).append("<br>");
				line = new StringBuilder(word);
			} else {
				line = new StringBuilder(testLine);
			}
		}
		builder.append(line).append("</div></html>");
		return builder.toString();
	}
}
