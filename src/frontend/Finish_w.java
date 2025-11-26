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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import paradigmpanic.backend.Player;

public class Finish_w extends JPanel {

	private final ParadigmPanic frame;
	private final Player player;

	private Image img;
	private ImagePanel bkgholder;

	private ImageIcon continue_n, continue_h;
	private JButton continue_b;
	private JPanel statsPanel;
	private JLabel nameLabel;
	private JLabel scoreLabel;
	private JLabel timeLabel;
	private Font statsFont;

	public Finish_w(ParadigmPanic frame, Player player) {
		this.frame = frame;
		this.player = player;
		loadGameFontIfAvailable();
		buildUi("/resources/images/win.png", Color.WHITE);
	}

	private void buildUi(String backgroundPath, Color statsColor) {
		img = new ImageIcon(getClass().getResource(backgroundPath)).getImage();
		bkgholder = new ImagePanel(img);

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkgholder, BorderLayout.CENTER);

		setupContinueButton();
		statsPanel = buildStatsPanel(statsColor);

		bkgholder.add(continue_b);
		bkgholder.add(statsPanel);

		continue_b.setBounds((frame.getWidth() / 2 - continue_n.getIconWidth() / 2), 575, 250, 65);
		statsPanel.setBounds(540, 300, 200, 160);

		bkgholder.revalidate();
		bkgholder.repaint();
	}

	private void setupContinueButton() {
		continue_n = Icons.icon("continue.png", 250, 65);
		continue_h = Icons.icon("continue.png", 240, 60);

		continue_b = new JButton(continue_n);
		continue_b.setBorderPainted(false);
		continue_b.setContentAreaFilled(false);
		continue_b.setFocusable(false);
		continue_b.setOpaque(false);

		continue_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				continue_b.setIcon(continue_h);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				continue_b.setIcon(continue_n);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				continue_b.setIcon(continue_n);
			}
		});

		continue_b.addActionListener(e -> {
			frame.showTitleScreen();
			frame.revalidate();
			frame.repaint();
		});
	}

	private JPanel buildStatsPanel(Color color) {
		JPanel panel = new JPanel(null);
		panel.setOpaque(false);

		String name = (player != null) ? player.getName() : "Recruit";
		String score = (player != null) ? (player.getScore() + " RP") : "0 RP";
		String time = (player != null) ? formatDuration(player.getTimeCompleted()) : "00:00";

		nameLabel = new JLabel(name, SwingConstants.CENTER);
		scoreLabel = new JLabel(score, SwingConstants.CENTER);
		timeLabel = new JLabel(time, SwingConstants.CENTER);

		nameLabel.setFont(statsFont.deriveFont(28f));
		scoreLabel.setFont(statsFont.deriveFont(40f));
		timeLabel.setFont(statsFont.deriveFont(24f));

		nameLabel.setForeground(color);
		scoreLabel.setForeground(color);
		timeLabel.setForeground(color);

		nameLabel.setOpaque(false);
		scoreLabel.setOpaque(false);
		timeLabel.setOpaque(false);

		int w = 200;
		nameLabel.setBounds(0, 0, w, 30);
		scoreLabel.setBounds(0, 45, w, 50);
		timeLabel.setBounds(0, 120, w, 30);

		panel.add(nameLabel);
		panel.add(scoreLabel);
		panel.add(timeLabel);
		panel.setPreferredSize(new Dimension(w, 160));

		return panel;
	}

	private String formatDuration(Duration duration) {
		long seconds = Math.max(0, duration.toSeconds());
		long minutes = seconds / 60;
		long remainder = seconds % 60;
		return String.format("%02d:%02d", minutes, remainder);
	}

	private void loadGameFontIfAvailable() {
		try (InputStream is = getClass().getResourceAsStream("/resources/font/LowresPixel.otf")) {
			if (is != null) {
				statsFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(42f);
			}
		} catch (Exception ignore) {
		}
		if (statsFont == null) {
			statsFont = new Font("Monospaced", Font.BOLD, 40);
		}
	}
}

class Finish_l extends JPanel {

	private final ParadigmPanic frame;
	private final Player player;

	private Image img;
	private ImagePanel bkgholder;

	private ImageIcon continue_n, continue_h;
	private JButton continue_b;
	private JPanel statsPanel;
	private JLabel nameLabel;
	private JLabel scoreLabel;
	private JLabel timeLabel;
	private Font statsFont;

	public Finish_l(ParadigmPanic frame, Player player) {
		this.frame = frame;
		this.player = player;
		loadGameFontIfAvailable();
		buildUi("/resources/images/loss.png", Color.BLACK);
	}

	private void buildUi(String backgroundPath, Color statsColor) {
		img = new ImageIcon(getClass().getResource(backgroundPath)).getImage();
		bkgholder = new ImagePanel(img);

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkgholder, BorderLayout.CENTER);

		setupContinueButton();
		statsPanel = buildStatsPanel(statsColor);

		bkgholder.add(continue_b);
		bkgholder.add(statsPanel);

		continue_b.setBounds((frame.getWidth() / 2 - continue_n.getIconWidth() / 2), 575, 250, 65);
		statsPanel.setBounds(540, 325, 200, 150);

		bkgholder.revalidate();
		bkgholder.repaint();
	}

	private void setupContinueButton() {
		continue_n = Icons.icon("continue.png", 250, 65);
		continue_h = Icons.icon("continue.png", 240, 60);

		continue_b = new JButton(continue_n);
		continue_b.setBorderPainted(false);
		continue_b.setContentAreaFilled(false);
		continue_b.setFocusable(false);
		continue_b.setOpaque(false);

		continue_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				continue_b.setIcon(continue_h);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				continue_b.setIcon(continue_n);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				continue_b.setIcon(continue_n);
			}
		});

		continue_b.addActionListener(e -> {
			frame.showTitleScreen();
			frame.revalidate();
			frame.repaint();
		});
	}

	private JPanel buildStatsPanel(Color color) {
		JPanel panel = new JPanel(null);
		panel.setOpaque(false);

		String name = (player != null) ? player.getName() : "Recruit";
		String score = (player != null) ? (player.getScore() + " RP") : "0 RP";
		String time = (player != null) ? formatDuration(player.getTimeCompleted()) : "00:00";

		nameLabel = new JLabel(name, SwingConstants.CENTER);
		scoreLabel = new JLabel(score, SwingConstants.CENTER);
		timeLabel = new JLabel(time, SwingConstants.CENTER);

		nameLabel.setFont(statsFont.deriveFont(32f));
		scoreLabel.setFont(statsFont.deriveFont(36f));
		timeLabel.setFont(statsFont.deriveFont(28f));

		nameLabel.setForeground(color);
		scoreLabel.setForeground(color);
		timeLabel.setForeground(color);

		nameLabel.setOpaque(false);
		scoreLabel.setOpaque(false);
		timeLabel.setOpaque(false);

		int w = 200;
		nameLabel.setBounds(0, 0, w, 40);
		scoreLabel.setBounds(0, 45, w, 50);
		timeLabel.setBounds(0, 105, w, 40);

		panel.add(nameLabel);
		panel.add(scoreLabel);
		panel.add(timeLabel);
		panel.setPreferredSize(new Dimension(w, 150));

		return panel;
	}

	private String formatDuration(Duration duration) {
		long seconds = Math.max(0, duration.toSeconds());
		long minutes = seconds / 60;
		long remainder = seconds % 60;
		return String.format("%02d:%02d", minutes, remainder);
	}

	private void loadGameFontIfAvailable() {
		try (InputStream is = getClass().getResourceAsStream("/resources/font/LowresPixel.otf")) {
			if (is != null) {
				statsFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(42f);
			}
		} catch (Exception ignore) {
		}
		if (statsFont == null) {
			statsFont = new Font("Monospaced", Font.BOLD, 40);
		}
	}
}
