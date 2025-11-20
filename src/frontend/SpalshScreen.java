package frontend;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

import javax.swing.*;

public class SpalshScreen extends JPanel{
	
	private final ParadigmPanic frame;
	
	private Image img;
	private ImagePanel bkg;
	
	private Image cloud;
	private ImagePanel cloud1, cloud2, cloud3;
	private JPanel cloud1_h;
	
	private Image title;
	private ImagePanel title_holder;
	private JPanel title_panel;
	
	private JLabel ins;
	private JPanel enterHere_holder;
	private Font g_font;
	
	private void mainGameFont() {
		try(InputStream is = getClass().getResourceAsStream("/resources/font/LowresPixel.otf")) {
			if (is != null) {
				Font f = Font.createFont(Font.TRUETYPE_FONT, is);
				g_font = f.deriveFont(25f);
			}
		} catch (Exception ignore) {
			//catch phrase is empty
		}
	}
	
	//moves the cloud automatically across the frame
	private Timer cloudTimer;
	private Timer titleTimer;
	private int cloudSpeed = 4;
	private boolean cloud1stat = false;
	private boolean cloud2stat = false;
	private boolean cloud3stat = false;
	
	//bobbing title
	private int titleY;
	private int titleAmp = 10;
	private int titleMove = 1;
	
	public SpalshScreen(ParadigmPanic frame) {
		this.frame = frame;
		
		img = new ImageIcon(getClass().getResource("/resources/images/splash.png")).getImage();
		bkg = new ImagePanel(img);
		
		// ---------------- CLOUD SETUP-------------------
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkg, BorderLayout.CENTER);
		
		cloud = new ImageIcon(getClass().getResource("/resources/images/cloud.png")).getImage();
		
		cloud1 = new ImagePanel(cloud);
		cloud2 = new ImagePanel(cloud);
		cloud3 = new ImagePanel(cloud);
		
		cloud1.setBackground(new Color(0, 0, 0, 0));
		cloud2.setBackground(new Color(0, 0, 0, 0));
		cloud3.setBackground(new Color(0, 0, 0, 0));
		
		// ---------------------- CLICK ANYWHERE TO START ------------------------------
		
		bkg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Screen clicked!");
				
				frame.showTitleScreen();
			}
		});
		
		cloud1.setBounds(0, 85, 564, 150);
		cloud2.setBounds(0, 347, 564, 150);
		cloud3.setBounds(1000, 85, 564, 150);
		
		bkg.add(cloud1);
		bkg.add(cloud2);
		bkg.add(cloud3);
		
		// ------------- ALLOWS FOR CLOUD TO MOVE ---------------------
		
		startCloudAnimation();
		
		bkg.revalidate();
		bkg.repaint();
	}
	
	private void startCloudAnimation() {
	    cloudTimer = new Timer(16, e -> {
	    	cloud1stat = moveCloud(cloud1);
	    	cloud2stat = moveCloud(cloud2);
	    	cloud3stat = moveCloudleft(cloud3);

	        bkg.repaint();

	        if (cloud1stat && cloud2stat && cloud3stat) {
	            cloudTimer.stop();
	            showTitle();  // ✅ now reveal the title
	        }
	    });
	    cloudTimer.start();
	}
	
	private boolean moveCloud(JComponent cloud) {
		int x = cloud.getX() + cloudSpeed;
		int y = cloud.getY();
		
		int halfWidth = cloud.getWidth() / 2;
	    int maxX = bkg.getWidth() - halfWidth;

	    // Move only until half the cloud goes offscreen
	    if (x < maxX) {
	        cloud.setLocation(x, y);
	        return false;
	    } else {
	        cloud.setLocation(maxX, y);
	        return true;
	    }
	}
	
	private boolean moveCloudleft(JComponent cloud) {
		 int x = cloud.getX() - cloudSpeed;  // move LEFT
		 int y = cloud.getY();

		 int halfWidth = cloud.getWidth() / 2;
		 int minX = -halfWidth;             // stop when half is offscreen left

		 if (x > minX) {
		    cloud.setLocation(x, y);
		    return false;
		 } else {
		    cloud.setLocation(minX, y);    // final position = like leftmost cloud
		    return true;
		 }
	}
	
	private void showTitle() {
		if (title_holder == null) {
			title = new ImageIcon(getClass().getResource("/resources/images/title.png")).getImage();
			title_holder = new ImagePanel(title);
			title_holder.setBackground(new Color (0, 0, 0, 0));
		}
		
		// put it in its starting position
	    titleY = 150;   // whatever you had before
	    title_holder.setBounds(285, titleY, 1000, 350);
	    title_holder.setVisible(true);
	    bkg.add(title_holder);
	    bkg.revalidate();
	    bkg.repaint();
	    
	    mainGameFont();
		
		title_panel = new JPanel();
		title_panel.setBackground(new Color(0, 0, 0, 0));
		title_panel.setSize(new Dimension(400, 50));
		title_panel.setLayout(new BorderLayout());
		
		ins = new JLabel("'Click anywhere to continue'");
		ins.setFont(g_font);
		ins.setForeground(new Color(128, 0, 0));
		ins.setHorizontalAlignment(SwingConstants.CENTER);
		title_panel.add(ins);
		title_panel.setBounds(575, 500, 400, 50);
		
		bkg.add(title_panel);
		
		bkg.repaint();

	    startTitleBobbing();
		
	}
	
	private void startTitleBobbing() {
	    titleTimer = new Timer(40, e -> {  // slower timer = smooth bob
	        int x = title_holder.getX();
	        int y = title_holder.getY() + titleMove;

	        // change direction when reaching the amplitude limit
	        if (y > titleY + titleAmp) {
	            y = titleY + titleAmp;
	            titleMove = -1;
	        } else if (y < titleY - titleAmp) {
	            y = titleY - titleAmp;
	            titleMove = 1;
	        }

	        title_holder.setLocation(x, y);
	        bkg.repaint();
	    });
	    titleTimer.start();
	}

}






