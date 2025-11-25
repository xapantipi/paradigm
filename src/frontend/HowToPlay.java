package frontend;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.*;

/*
 * For the How To Play menu of the game
 */

public class HowToPlay extends JPanel{
	
	private ParadigmPanic frame;
	
	//labels
	private JLabel pageView;
	private JLabel pageIndicator;
	
	//bkg label stype
	private ImagePanel htpbkg1;
	private Image bkg;
	private String bkg_name;
	//for other pages that are not the first page
	private Image img;
	
	//for switcing pages
	//pages for the how to play part
	private final String[] paths = {
			"/resources/images/1_htp.png", 
			"/resources/images/htp2.png", 
			"/resources/images/htp3.png",
			"/resources/images/htp4.png", 
			"/resources/images/htp5.png",
			"/resources/images/htp6.png",
			"/resources/images/htp7.png", 
			"/resources/images/htp8.png"
		};
	private int page = 0;
	
	//buttons
	private JButton return_b, next_b, prev_b;
	private ImageIcon ret, next, prev;
	private Icons icon;

	public HowToPlay(ParadigmPanic frame) {
		this.frame = frame;
		
		//loading first bkg 
		bkg = new ImageIcon(getClass().getResource(paths[page])).getImage();
		htpbkg1 = new ImagePanel(bkg);
		htpbkg1.setLayout(null);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(bkg.getWidth(frame), bkg.getHeight(frame)));
		add(htpbkg1, BorderLayout.CENTER);
		
		//buttons
		ret = Icons.icon("return button.png", 50, 50);
		next = Icons.icon("next button.png", 100, 100);
		prev = Icons.icon("previous button.png", 100, 100);
		
		//create buttons
		return_b = new JButton(ret);
		next_b = new JButton(next);
		prev_b = new JButton(prev); 
		
		//button appearance
		for (JButton btn : new JButton[] {return_b, next_b, prev_b}) {
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusable(false);
			btn.setOpaque(false);
		}
		
		//mouse listener for pezas
		next_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				next_b.setBounds(1035, 579, 90, 90);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				next_b.setBounds(1030, 570, 100, 100);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				next_b.setBounds(1035, 579, 90, 90);
				next_b.setBounds(1030, 570, 100, 100);
			}
		});
		
		prev_b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				prev_b.setBounds(405, 570, 100, 100);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				prev_b.setBounds(410, 579, 90, 90);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				prev_b.setBounds(405, 570, 100, 100);
			}
		});
		
		
		//actions for buttons
		return_b.addActionListener(e -> {
			System.out.println("Returning");
			frame.showTitleScreen();
			frame.revalidate();
			frame.repaint();
			});
		
		next_b.addActionListener(e -> {
			if (page == 7) {
				System.out.println("Last page");
				System.out.println(page);
			} else {
				page++;
				System.out.println("Next page");
				System.out.println(page);
				System.out.println(paths[page]);

				img = new ImageIcon(getClass().getResource(paths[page])).getImage();
				htpbkg1.setImage(img);
				setPreferredSize(new Dimension(bkg.getWidth(frame), bkg.getHeight(frame)));
			}
			
			frame.revalidate();
			frame.repaint();
		});
		
		prev_b.addActionListener(e -> {
			if (page == 0) {
				System.out.println("first page");
				System.out.println(page);
			} else {
				page--;
				System.out.println("Previous page");
				System.out.println(page);
				System.out.println(paths[page]);
				
				img = new ImageIcon(getClass().getResource(paths[page])).getImage();
				htpbkg1.setImage(img);
				setPreferredSize(new Dimension(bkg.getWidth(frame), bkg.getHeight(frame)));
			}
			
			frame.revalidate();
			frame.revalidate();
		});
		
		return_b.setBounds(30, 25, 100, 100);
		next_b.setBounds(1030, 570, 100, 100);
		prev_b.setBounds(405, 570, 100, 100);
		 
		htpbkg1.add(return_b);
		htpbkg1.add(next_b);
		htpbkg1.add(prev_b);
		
		htpbkg1.revalidate();
		htpbkg1.repaint();
	}

}



