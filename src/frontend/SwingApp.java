package frontend;

/*
 * Splash screen - title page
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class SwingApp extends JPanel{
	
	private JLabel titleLabel;
	private ImageIcon start_n, howTo_n, leaderboard_n, sound;
	private ImageIcon start_h, howTo_h, leaderboard_h, sound_m;
	private JButton start_b, howTo_b, leaderboard_b, sound_b;
	private Image img; 
	private ImagePanel bkg;
	private Icons icon;
	private final String [] buttons = {
			"sound button.png",
			"sound off.png"
		};
	private boolean btn_ctr;
	private final ParadigmPanic frame;
	private Clip bgmClip;
	
	public SwingApp(ParadigmPanic frame) {
		this.frame = frame;
		
		//setting bkg image
		img = new ImageIcon(getClass().getResource("/resources/images/title_s.png")).getImage();
		bkg = new ImagePanel(img);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkg, BorderLayout.CENTER);
		 
		//buttons
		
		//start button
		start_n = Icons.icon("start(u).png", 450, 100);
		start_h = Icons.icon("start(c).png", 450, 100);

		
		//how to play button
		howTo_n = Icons.icon("howTo(u).png", 450, 100);
		howTo_h = Icons.icon("htp.png", 450, 100);

		
		//leaderboard button
		leaderboard_n = Icons.icon("leaderboard(u).png", 450, 100);
		leaderboard_h = Icons.icon("leaderboard(c).png", 450, 100);
		
		//sound buttons
		sound = Icons.icon("volume.png", 100, 100);
		sound_m = Icons.icon("mute.png", 100, 100);
		
		//create buttons
		start_b = new JButton(start_n);
		howTo_b = new JButton(howTo_n);
		leaderboard_b = new JButton(leaderboard_n);
		sound_b = new JButton(sound);
		
		initializeAudio();
		
		//button appearance
		for (JButton btn : new JButton[] {start_b, howTo_b, leaderboard_b, sound_b}) {
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusable(false);
			btn.setOpaque(false);
		}
		
		//Action listeners to change button appearance when pointer is hovering over it
		
		start_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				start_b.setIcon(start_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				start_b.setIcon(start_n);
			}
		});
		
		howTo_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				howTo_b.setIcon(howTo_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				howTo_b.setIcon(howTo_n);
			}
		}); 
		
		leaderboard_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				leaderboard_b.setIcon(leaderboard_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				leaderboard_b.setIcon(leaderboard_n);
			}
		}); 
		
		sound_b.addActionListener(e -> toggleSound());
		
		//button actions 
		//separate from listener, this handles actions for when you click the buttons
		start_b.addActionListener(e -> {
			System.out.println("Start button clicked");
			frame.showGameStart();
			frame.revalidate();
			frame.repaint();
			});
		
		howTo_b.addActionListener(e -> {
			System.out.println("How to play button clicked");
			frame.showInstructions();
			frame.revalidate();
			frame.repaint();
		});
		
		leaderboard_b.addActionListener(e -> {
			System.out.println("Leaderboard button clicked");
			frame.showLead();
			frame.revalidate();
			frame.repaint();
		});
		
		
		//adding buttons to frame
		bkg.add(start_b);
		bkg.add(howTo_b);
		bkg.add(leaderboard_b);
		bkg.add(sound_b);
		
		start_b.setBounds(530, 412, 450, 100);
		howTo_b.setBounds(530, 486, 450, 100);
		leaderboard_b.setBounds(530, 560, 450, 100);
		sound_b.setBounds(1350, 700, 100, 100);
		
		bkg.revalidate();
		bkg.repaint();
	}

	private void initializeAudio() {
		URL resource = getClass().getResource("/resources/sounds/bgm.wav");
		if (resource == null) {
			return;
		}
		try (AudioInputStream stream = AudioSystem.getAudioInputStream(resource)) {
			bgmClip = AudioSystem.getClip();
			bgmClip.open(stream);
			playBgm();
		} catch (IOException | UnsupportedOperationException | IllegalArgumentException | javax.sound.sampled.UnsupportedAudioFileException | javax.sound.sampled.LineUnavailableException ex) {
			bgmClip = null;
		}
	}

	private void toggleSound() {
		btn_ctr = !btn_ctr;
		if (btn_ctr) {
			sound_b.setIcon(sound_m);
			muteBgm();
			System.out.println("Mute");
		} else {
			sound_b.setIcon(sound);
			playBgm();
			System.out.println("Sound on");
		}
	}

	private void playBgm() {
		if (bgmClip == null) {
			return;
		}
		bgmClip.stop();
		bgmClip.setFramePosition(0);
		bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
		bgmClip.start();
	}

	private void muteBgm() {
		if (bgmClip == null) {
			return;
		}
		bgmClip.stop();
	}
}

//for the bkg so that you can have the buttons on top without interfering with the hierarchy  
class ImagePanel extends JPanel {
	private Image bkg;
	
	public ImagePanel(Image bkg) {
		this.bkg = bkg;
		setLayout(null);
	}
	
	public void setImage(Image img) {
		this.bkg = img; 
		repaint(); 
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bkg, 0, 0, getWidth(), getHeight(), this);
	}
}





