package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import paradigmpanic.backend.Leaderboard;
import paradigmpanic.backend.Player;
import paradigmpanic.backend.Question;
import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.Question.Category;

public class LeaderBoard extends JPanel {

	private enum BoardMode {
		EASY("EASY"), MEDIUM("INTERMEDIATE"), HARD("HARD");

		private final String label;

		BoardMode(String label) {
			this.label = label;
		}
	}

	private final ParadigmPanic frame;
	private final Leaderboard leaderboard;

	private Font g_font;
	private Font headingFont;

	private Image img;
	private ImagePanel bkg;

	private JButton easy_b, inter_b, hard_b, back_b;
	private ImageIcon easy_h, easy_n;
	private ImageIcon inter_h, inter_n;
	private ImageIcon hard_h, hard_n;
	private JComboBox<Question.Category> categoryBox;

	private Image screen;
	private ImagePanel screen1, screen2, screen3, screen4, screen5;
	private JLabel screen1_h, screen2_h, screen3_h, screen4_h, screen5_h;
	private JLabel boardTitle;
	private JLabel[] rankLabels;

	private ImageIcon ret, ret_h;
	private BoardMode selectedMode = BoardMode.EASY;
	
	

	public LeaderBoard(ParadigmPanic frame) {
		this.frame = frame;
		this.leaderboard = frame != null ? frame.getLeaderboardStore() : new Leaderboard();
		loadGameFontIfAvailable();

		img = new ImageIcon(getClass().getResource("/resources/images/leader bkg.png")).getImage();
		bkg = new ImagePanel(img);

		setupScreens();
		setupButtons();
		setupCategorySelector();
		layoutComponents();
		selectMode(BoardMode.EASY);
		refreshLeaderboard();
	}

	private void loadGameFontIfAvailable() {
		try (InputStream is = getClass().getResourceAsStream("/resources/font/LowresPixel.otf")) {
			if (is != null) {
				Font f = Font.createFont(Font.TRUETYPE_FONT, is);
				g_font = f.deriveFont(25f);
				headingFont = f.deriveFont(42f);
			}
		} catch (Exception ignore) {
		}
		if (g_font == null) {
			g_font = new Font("Monospaced", Font.BOLD, 24);
		}
		if (headingFont == null) {
			headingFont = g_font.deriveFont(38f);
		}
	}

	private void setupCategorySelector() {
		categoryBox = new JComboBox<>(Question.Category.values());
		Font boxFont = g_font == null ? new Font("Monospaced", Font.BOLD, 20) : g_font.deriveFont(20f);
		categoryBox.setFont(boxFont);
		categoryBox.setForeground(Color.YELLOW);
		categoryBox.setBackground(new Color(255, 255, 255, 220));
		categoryBox.setOpaque(true);
		categoryBox.setRenderer(new CategoryRenderer(categoryBox.getRenderer()));
		categoryBox.addActionListener(e -> refreshLeaderboard());
	}

	private void setupScreens() {
		screen = new ImageIcon(getClass().getResource("/resources/images/leader screen.png")).getImage();

		screen1 = new ImagePanel(screen);
		screen2 = new ImagePanel(screen);
		screen3 = new ImagePanel(screen);
		screen4 = new ImagePanel(screen);
		screen5 = new ImagePanel(screen);

		screen1_h = createRankLabel();
		screen2_h = createRankLabel();
		screen3_h = createRankLabel();
		screen4_h = createRankLabel();
		screen5_h = createRankLabel();

		for (ImagePanel panel : new ImagePanel[] { screen1, screen2, screen3, screen4, screen5 }) {
			panel.setLayout(new BorderLayout());
		}

		screen1.add(screen1_h, BorderLayout.CENTER);
		screen2.add(screen2_h, BorderLayout.CENTER);
		screen3.add(screen3_h, BorderLayout.CENTER);
		screen4.add(screen4_h, BorderLayout.CENTER);
		screen5.add(screen5_h, BorderLayout.CENTER);

		rankLabels = new JLabel[] { screen1_h, screen2_h, screen3_h, screen4_h, screen5_h };

		screen1.setBounds(700, 150, 600, 75);
		screen2.setBounds(700, 250, 600, 75);
		screen3.setBounds(700, 350, 600, 75);
		screen4.setBounds(700, 450, 600, 75);
		screen5.setBounds(700, 550, 600, 75);

		boardTitle = new JLabel("TOP RECRUITS", SwingConstants.CENTER);
		boardTitle.setFont(headingFont);
		boardTitle.setForeground(Color.YELLOW);
		boardTitle.setBounds(700, 60, 600, 70);
	}

	private JLabel createRankLabel() {
		JLabel label = new JLabel("", SwingConstants.CENTER);
		label.setFont(g_font);
		label.setForeground(Color.YELLOW);
		return label;
	}

	private void setupButtons() {
		easy_n = Icons.icon("leader_e.png", 300, 80);
		easy_h = Icons.icon("leader_e.png", 320, 90);

		inter_n = Icons.icon("leader_i.png", 300, 80);
		inter_h = Icons.icon("leader_i.png", 320, 90);

		hard_n = Icons.icon("leader_h.png", 300, 80);
		hard_h = Icons.icon("leader_h.png", 320, 90);

		ret = Icons.icon("return button.png", 60, 60);
		ret_h = Icons.icon("return button.png", 70, 70);

		back_b = new JButton(ret);
		easy_b = new JButton(easy_n);
		inter_b = new JButton(inter_n);
		hard_b = new JButton(hard_n);

		for (JButton button : new JButton[] { back_b, easy_b, inter_b, hard_b }) {
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusable(false);
			button.setOpaque(false);
		}

		easy_b.addMouseListener(new HoverAdapter(() -> 
			selectedMode != BoardMode.EASY, easy_h, easy_n, easy_b));
		
		inter_b.addMouseListener(new HoverAdapter(() -> 
			selectedMode != BoardMode.MEDIUM, inter_h, inter_n, inter_b));
		
		hard_b.addMouseListener(new HoverAdapter(() -> 
			selectedMode != BoardMode.HARD, hard_h, hard_n, hard_b));

		easy_b.addActionListener(e -> 
			selectMode(BoardMode.EASY));
		
		inter_b.addActionListener(e -> 
			selectMode(BoardMode.MEDIUM));
		
		hard_b.addActionListener(e -> 
			selectMode(BoardMode.HARD));

		back_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				back_b.setIcon(ret_h);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				back_b.setIcon(ret);
			}
		});

		back_b.addActionListener(e -> {
			if (frame != null) {
				frame.showTitleScreen();
				revalidate();
				repaint();
			}
		});
	}

	private void layoutComponents() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkg, BorderLayout.CENTER);

		bkg.add(boardTitle);
		bkg.add(screen1);
		bkg.add(screen2);
		bkg.add(screen3);
		bkg.add(screen4);
		bkg.add(screen5);

		back_b.setBounds(30, 25, 100, 100);
		easy_b.setBounds(125, 200, 300, 80);
		inter_b.setBounds(125, 350, 300, 80);
		hard_b.setBounds(125, 500, 300, 80);
		categoryBox.setBounds(125, 120, 300, 50);

		bkg.add(back_b);
		bkg.add(categoryBox);
		bkg.add(easy_b);
		bkg.add(inter_b);
		bkg.add(hard_b);

		bkg.revalidate();
		bkg.repaint();
	}

	private void selectMode(BoardMode mode) {
		selectedMode = mode;
		easy_b.setIcon(mode == BoardMode.EASY ? easy_h : easy_n);
		inter_b.setIcon(mode == BoardMode.MEDIUM ? inter_h : inter_n);
		hard_b.setIcon(mode == BoardMode.HARD ? hard_h : hard_n);
		refreshLeaderboard();
	}

	private void refreshLeaderboard() {
		Difficulty difficulty = switch (selectedMode) {
			case EASY -> Difficulty.EASY;
			case MEDIUM -> Difficulty.MEDIUM;
			case HARD -> Difficulty.HARD;
		};
		Question.Category category = categoryBox == null ? Category.THEORY
				: (Question.Category) categoryBox.getSelectedItem();
		if (category == null) {
			category = Category.THEORY;
		}
		List<Player> players = leaderboard.getTopPlayers(difficulty, category);
		boardTitle.setText("TOP RECRUITS (" + selectedMode.label + " - " + category + ")");

		if (players.isEmpty()) {
			rankLabels[0].setText("No recruits on the board yet.");
			for (int i = 1; i < rankLabels.length; i++) {
				rankLabels[i].setText("---");
			}
			return;
		}

		for (int i = 0; i < rankLabels.length; i++) {
			if (i < players.size()) {
				rankLabels[i].setText(formatEntry(i + 1, players.get(i)));
			} else {
				rankLabels[i].setText("---");
			}
		}
	}

	private String formatEntry(int rank, Player player) {
		String name = trimName(player.getName(), 16);
		String score = player.getScore() + " PTS";
		String time = formatDuration(player.getTimeCompleted());
		return rank + ". " + name + fillDots(name, score) + score + " (" + time + ")";
	}

	private String trimName(String name, int max) {
		String clean = name == null || name.isBlank() ? "Recruit" : name.trim();
		if (clean.length() <= max) {
			return clean;
		}
		int cut = Math.max(1, max - 3);
		return clean.substring(0, cut) + "...";
	}

	private String fillDots(String name, String score) {
		int total = 28;
		int used = name.length() + score.length();
		int dots = Math.max(3, total - used);
		return " " + ".".repeat(dots) + " ";
	}

	private String formatDuration(Duration duration) {
		long seconds = Math.max(0, duration.toSeconds());
		long minutes = seconds / 60;
		long remainder = seconds % 60;
		return String.format("%02d:%02d", minutes, remainder);
	}

	private static final class HoverAdapter extends MouseAdapter {
		private final java.util.function.BooleanSupplier shouldHover;
		private final ImageIcon hoverIcon;
		private final ImageIcon idleIcon;
		private final JButton button;

		private HoverAdapter(java.util.function.BooleanSupplier shouldHover, ImageIcon hoverIcon, ImageIcon idleIcon,
				JButton button) {
			this.shouldHover = shouldHover;
			this.hoverIcon = hoverIcon;
			this.idleIcon = idleIcon;
			this.button = button;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (shouldHover.getAsBoolean()) {
				button.setIcon(hoverIcon);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (shouldHover.getAsBoolean()) {
				button.setIcon(idleIcon);
			}
		}
	}

	private static final class CategoryRenderer extends DefaultListCellRenderer {
		@Override
		public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			return label;
		}
	}
}
