package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import paradigmpanic.backend.Question;

/*
 * Starting game including inputing name and choose category
 */

public class Start extends JPanel{
	
	private final ParadigmPanic frame;
	
	private Image bkgimg;
	private ImagePanel startbkg;
	private JButton continue_b, return_b;
	private ImageIcon continue_n, ret;	
	private ImageIcon continue_h, ret_h;
	
	//for name
	private String name = "Enter name_";
	private boolean usingPlaceholder = true;
	private Font font;
	private boolean nameEntered = false;
	
	//for absolute positioning for the name so that it will start on X and Y when entered
	//private int nameX = 640;
	private int nameY = 450;
	
	//loading font 
	private void loadGameFontIfAvailable() {
		try(InputStream is = getClass().getResourceAsStream("/resources/font/pixel.ttf")) {
			if (is != null) {
				Font f = Font.createFont(Font.TRUETYPE_FONT, is);
				font = f.deriveFont(35f);
			}
		} catch (Exception ignore) {
			//catch phrase is empty
		}
	}
	
	public Start(ParadigmPanic frame) {
		
		this.frame = frame;

		loadGameFontIfAvailable();
		
		//getting background image
		bkgimg = new ImageIcon(getClass().getResource("/resources/images/name bkg.png")).getImage();
		
		
		// ------------------ BKG IMAGE AND TEXT BOULEVARD --------------------------
		
		
		startbkg = new ImagePanel(bkgimg) {
			//since the name will be entered on the page
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(font);
				g2.setColor(Color.BLACK);
				g2.setColor(usingPlaceholder ? Color.DARK_GRAY : Color.BLACK);
				
				//draws cursor only when the panel is focused
				FontMetrics fm = g2.getFontMetrics();
				
				//centering
				int textWidth = fm.stringWidth(name);
			    int centered = (getWidth() - textWidth) / 2;
				
			    int cursorX = centered + fm.stringWidth(name);
			    int cursorY = nameY - fm.getAscent();
			    int cursorHeight = fm.getAscent() + fm.getDescent();

			    // only draw cursor when the panel has focus
			    if (Start.this.startbkg.isFocusOwner()) {
			        g2.fillRect(cursorX + 2, cursorY, 2, cursorHeight);
			    }
	
				g2.drawString(name,  centered, nameY);
				nameEntered = true;
				
				g2.dispose();
			}
		};
		
		
		// ---------------- LAYOUTING --------------
		
		
		setLayout(new BorderLayout());
		
		setPreferredSize(new Dimension(bkgimg.getWidth(frame), bkgimg.getHeight(frame)));
		add(startbkg, BorderLayout.CENTER);
		
		//------------ BUTTON AVENUE ---------------
		
		//return button just in case u want to exit
		ret = Icons.icon("return button.png", 60, 60);
		ret_h = Icons.icon("return button.png", 70, 70);
		
		continue_n = Icons.icon("continue.png", 250, 65);
		continue_h = Icons.icon("continue.png" , 255, 70);
		
		//creating buttons
		return_b = new JButton(ret);
		continue_b = new JButton(continue_n);
		
		
		
		//button appearance
		for (JButton btn : new JButton[] {return_b, continue_b}) {
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusable(false);
			btn.setOpaque(false);
		}
		
		return_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				return_b.setIcon(ret_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				return_b.setIcon(ret);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				return_b.setIcon(ret);
			}
		});
		
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
		
		
		//actions for buttons
		return_b.addActionListener(e -> {
			System.out.println("Returning");
			frame.showTitleScreen();
			frame.revalidate();
			frame.repaint();
		});
		
		continue_b.addActionListener(e -> {
			System.out.println("Next: choose difficulty");
			frame.setPlayerName(getNameClean());
			frame.showDifficulty();
			frame.revalidate();
			frame.repaint();
		});
		
		
		//------------------- POSITIONING AVENUE ----------------------
		
		//absolute positioning buttons kay i love making my life hard
		startbkg.setLayout(null);
		return_b.setBounds(30, 25, 100, 100);
		continue_b.setBounds((frame.getWidth() / 2 - continue_n.getIconWidth() / 2), 520, 250, 65);

		startbkg.add(return_b);
		startbkg.add(continue_b);
		
		//since the text will be placed on the bkg, it will repaint every time u add or remove a character
		startbkg.setFocusable(true);
		
		
		//----------------------- LISTENER'S BLOCK -------------------------------
		
		//click anywhere to type
		startbkg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startbkg.requestFocusInWindow();
			}
		});
		
		startbkg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				
				//backspace
				if (c == '\b') {
					if (!usingPlaceholder && !name.isEmpty()) {
						name = name.substring(0, name.length() - 1);
						startbkg.repaint();
					}
					return;
				}
				
				//first char replaces th eplaceholder
				if (usingPlaceholder) {
					name = String.valueOf(c);
					usingPlaceholder = false;
					startbkg.repaint();
					return;
				}
				
				//appending
				name += c;
				startbkg.repaint();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("ENTER -> name = " + getNameClean());
					continue_b.doClick();
				}
			}
		});
		
		// ----------------------- END OF LISTENER'S BLOCK ---------------------------
		
		startbkg.revalidate();
		startbkg.repaint();
	}
	
	private String getNameClean () {
		return usingPlaceholder ? "" : name;
	}
	
}


/*
 * For the difficulty part
 */


class difficulty extends JPanel {
	
	private final ParadigmPanic frame;
	private Question.Category selectedCategory = Question.Category.THEORY;
	
	//for bkg image
	private Image bkgimg1;
	private ImagePanel d_startbkg;
	private Icons icon;
	
	//buttons
	private JButton easy_b, inter_b, hard_b, back_b;
	private ImageIcon easy_h, inter_h, hard_h, back_h; 			//for hovering
	private ImageIcon easy_n, inter_n, hard_n, back_n;
	
	private JComboBox<Question.Category> categoryBox;
	private JLabel categoryLabel;
	private Font font;
	
	private void loadGameFontIfAvailable() {
		try(InputStream is = getClass().getResourceAsStream("/resources/font/pixel.ttf")) {
			if (is != null) {
				Font f = Font.createFont(Font.TRUETYPE_FONT, is);
				font = f.deriveFont(30f);
			}
		} catch (Exception ignore) {
		}
	}
	
	public difficulty (ParadigmPanic frame) {
		this.frame = frame;
		loadGameFontIfAvailable();
		
		//----------------- BACKGROUND BOULEVARD --------------------------
		
		bkgimg1 = new ImageIcon(getClass().getResource("/resources/images/difficulty bkg.png")).getImage();
		d_startbkg = new ImagePanel(bkgimg1);
		//d_startbkg.setLayout(null);
		
		
		//---------------- BUTTONS AREA -----------------------
		
		easy_n = Icons.icon("easy.png", 170, 200);
		easy_h = Icons.icon("easy.png", 150, 180);
		
		inter_n = Icons.icon("intermediate.png", 170, 200);
		inter_h = Icons.icon("intermediate.png", 150, 180);
		
		hard_n = Icons.icon("hard.png", 170, 200);
		hard_h = Icons.icon("hard.png", 150, 180);
		
		back_n = Icons.icon("return button.png", 60, 60);
		back_h = Icons.icon("return button.png", 70, 70);
	
		//creating buttons
		easy_b = new JButton(easy_n);
		inter_b = new JButton(inter_n);
		hard_b= new JButton(hard_n);
		back_b = new JButton(back_n);
		
		//button appearance
		for (JButton button : new JButton[] {easy_b, inter_b, hard_b, back_b}) {
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusable(false);
			button.setOpaque(false);
		}
		
		//mouseListeners for buttons to change appearance when mouse is hovering over buttons
		easy_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				easy_b.setIcon(easy_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				easy_b.setIcon(easy_n);
			}
			public void mouseClicked(MouseEvent e) {
				easy_b.setIcon(easy_n);
			}
		});
		
		inter_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				inter_b.setIcon(inter_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				inter_b.setIcon(inter_n);
			}
			public void mouseClicked(MouseEvent e) {
				inter_b.setIcon(inter_n);
			}
		});
		
		hard_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hard_b.setIcon(hard_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				hard_b.setIcon(hard_n);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				hard_b.setIcon(hard_n);
			}
			
		});
		
		back_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				back_b.setIcon(back_h);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				back_b.setIcon(back_n);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				back_b.setIcon(back_n);
			}
		});
		
		//----------------- BUTTON ACTIONS -------------------------
		easy_b.addActionListener(e-> startGame(Question.Difficulty.EASY));
		
		inter_b.addActionListener(e -> startGame(Question.Difficulty.MEDIUM));
		
		hard_b.addActionListener(e -> startGame(Question.Difficulty.HARD));
		
		back_b.addActionListener( e -> {
			System.out.println("Return");
			frame.showTitleScreen();
			revalidate();
			repaint();
		});
		
		//----------------- CREATIVE'S SPACE -----------------------
		
		categoryLabel = new JLabel("CHOOSE CATEGORY");
		categoryLabel.setFont(font);
		categoryLabel.setFont(font.deriveFont(22f));
		categoryLabel.setForeground(Color.WHITE);
		categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		categoryBox = new JComboBox<>(Question.Category.values());
		categoryBox.setFont(font);
		categoryBox.setForeground(Color.DARK_GRAY);
		categoryBox.setBackground(new Color(255, 255, 255, 220));
		categoryBox.addActionListener(e -> {
			Object selected = categoryBox.getSelectedItem();
			if (selected instanceof Question.Category category) {
				selectedCategory = category;
			}
		});
		ListCellRenderer<? super Question.Category> renderer = categoryBox.getRenderer();
		if (renderer instanceof JLabel) {
			((JLabel) renderer).setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(bkgimg1.getWidth(frame), bkgimg1.getHeight(frame)));
		add(d_startbkg, BorderLayout.CENTER);
		
		//adding buttons
		d_startbkg.add(easy_b);
		d_startbkg.add(inter_b);
		d_startbkg.add(hard_b);
		d_startbkg.add(back_b);
		
		easy_b.setBounds(450, 330, 170, 200);
		inter_b.setBounds(700, 330, 170, 200);
		hard_b.setBounds(950, 330, 170, 200);
		back_b.setBounds(30, 25, 100, 100);
		categoryLabel.setBounds(600, 600, 350, 50);
		categoryBox.setBounds(600, 660, 350, 50);
		
		d_startbkg.add(categoryLabel);
		d_startbkg.add(categoryBox);
		
		d_startbkg.revalidate();
		d_startbkg.repaint();
	}
	
	private void startGame(Question.Difficulty difficulty) {
		frame.startGame(difficulty, selectedCategory);
	}
}






