package frontend;

import java.awt.Component;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class SpalshScreen extends JPanel {
	
	private final ParadigmPanic frame;
	
	private Image img;
	private ImagePanel bkg;
	
	private Image cloud;
	private ImagePanel cloud1, cloud2, cloud3;
	private JPanel title_panel; // Removed cloud1_h as it wasn't used
	
	private Image title;
	private ImagePanel title_holder;
	
	private JLabel ins;
	private Font g_font;
	
	// Animation variables
	private Timer cloudTimer;
	private Timer titleTimer;
	private int cloudSpeed = 5;
	private boolean cloud1stat = false;
	private boolean cloud2stat = false;
	private boolean cloud3stat = false;
	
	// Title bobbing
	private int titleY;
	private int titleAmp = 10;
	private int titleMove = 1;
	
	// The shared listener for clicking
	private MouseAdapter transitionListener;
	
	public SpalshScreen(ParadigmPanic frame) {
		this.frame = frame;
		
		img = loadImage("/resources/images/splash.png");
		bkg = new ImagePanel(img);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(img.getWidth(frame), img.getHeight(frame)));
		add(bkg, BorderLayout.CENTER);
		
		// Initialize the listener once
		transitionListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				stopTimers();
				System.out.println("Screen clicked - Transitioning!");
				frame.showTitleScreen();
			}
		};
		registerClickTarget(this);
		registerClickTarget(bkg);
		
		// ---------------- CLOUD SETUP -------------------
		cloud = loadImage("/resources/images/cloud.png");
		
		cloud1 = new ImagePanel(cloud);
		cloud2 = new ImagePanel(cloud);
		cloud3 = new ImagePanel(cloud);
		
		setupCloud(cloud1);
		setupCloud(cloud2);
		setupCloud(cloud3);
		
		cloud1.setBounds(0, 85, 564, 150);
		cloud2.setBounds(0, 347, 564, 150);
		cloud3.setBounds(1000, 85, 564, 150);
		
		bkg.add(cloud1);
		bkg.add(cloud2);
		bkg.add(cloud3);
		
		startCloudAnimation();
		
		bkg.revalidate();
		bkg.repaint();
	}
	
	// Helper to apply settings and listeners to clouds
	private void setupCloud(ImagePanel c) {
		if (c == null) {
			return;
		}
		c.setBackground(new Color(0, 0, 0, 0));
		registerClickTarget(c);
	}
	
	private void stopTimers() {
		if (cloudTimer != null) cloudTimer.stop();
		if (titleTimer != null) titleTimer.stop();
	}
	
	private void mainGameFont() {
		try (InputStream is = loadResourceStream("/resources/font/LowresPixel.otf")) {
			if (is != null) {
				Font f = Font.createFont(Font.TRUETYPE_FONT, is);
				g_font = f.deriveFont(25f);
			}
		} catch (Exception ignore) { }
	}
	
	private void startCloudAnimation() {
	    cloudTimer = new Timer(16, e -> {
	    	cloud1stat = moveCloud(cloud1);
	    	cloud2stat = moveCloud(cloud2);
	    	cloud3stat = moveCloudleft(cloud3);

	        bkg.repaint();

	        if (cloud1stat && cloud2stat && cloud3stat) {
	            cloudTimer.stop();
	            showTitle();
	        }
	    });
	    cloudTimer.start();
	}
	
	private boolean moveCloud(JComponent cloud) {
		int x = cloud.getX() + cloudSpeed;
		int y = cloud.getY();
		int halfWidth = cloud.getWidth() / 2;
	    int maxX = bkg.getWidth() - halfWidth;

	    if (x < maxX) {
	        cloud.setLocation(x, y);
	        return false;
	    } else {
	        cloud.setLocation(maxX, y);
	        return true;
	    }
	}
	
	private boolean moveCloudleft(JComponent cloud) {
		 int x = cloud.getX() - cloudSpeed;
		 int y = cloud.getY();
		 int halfWidth = cloud.getWidth() / 2;
		 int minX = -halfWidth; 

		 if (x > minX) {
		    cloud.setLocation(x, y);
		    return false;
		 } else {
		    cloud.setLocation(minX, y);
		    return true;
		 }
	}
	
	private void showTitle() {
		if (title_holder == null) {
			title = loadImage("/resources/images/title.png");
			title_holder = new ImagePanel(title);
			title_holder.setBackground(new Color (0, 0, 0, 0));
			registerClickTarget(title_holder);
		}
		
	    titleY = 150;
	    title_holder.setBounds(285, titleY, 1000, 350);
	    title_holder.setVisible(true);
	    bkg.add(title_holder);
	    
	    mainGameFont();
		
		title_panel = new JPanel();
		title_panel.setBackground(new Color(0, 0, 0, 0));
		title_panel.setSize(new Dimension(400, 50));
		title_panel.setLayout(new BorderLayout());
		registerClickTarget(title_panel);
		
		ins = new JLabel("'Click anywhere to continue'");
		ins.setFont(g_font);
		ins.setForeground(new Color(128, 0, 0));
		ins.setHorizontalAlignment(SwingConstants.CENTER);
		title_panel.add(ins);
		title_panel.setBounds(575, 500, 400, 50);
		
		bkg.add(title_panel);
		
		bkg.revalidate();
		bkg.repaint();

	    startTitleBobbing();
	}
	
	private void startTitleBobbing() {
	    titleTimer = new Timer(40, e -> {
	        int x = title_holder.getX();
	        int y = title_holder.getY() + titleMove;

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

	private Image loadImage(String resourcePath) {
		java.net.URL url = getClass().getResource(resourcePath);
		if (url != null) {
			return new ImageIcon(url).getImage();
		}
		Path fallback = resolveSourcePath(resourcePath);
		if (Files.exists(fallback)) {
			return new ImageIcon(fallback.toString()).getImage();
		}
		throw new IllegalStateException("Missing image resource: " + resourcePath);
	}

	private InputStream loadResourceStream(String resourcePath) throws IOException {
		InputStream stream = getClass().getResourceAsStream(resourcePath);
		if (stream != null) return stream;
		Path fallback = resolveSourcePath(resourcePath);
		if (Files.exists(fallback)) return Files.newInputStream(fallback);
		return null;
	}

	private Path resolveSourcePath(String resourcePath) {
		String sanitized = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
		return Paths.get("src", sanitized);
	}

	private void registerClickTarget(Component component) {
		if (component == null || transitionListener == null) {
			return;
		}
		boolean alreadyRegistered = false;
		for (MouseListener listener : component.getMouseListeners()) {
			if (listener == transitionListener) {
				alreadyRegistered = true;
				break;
			}
		}
		if (!alreadyRegistered) {
			component.addMouseListener(transitionListener);
		}
		if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				registerClickTarget(child);
			}
			container.addContainerListener(new ContainerAdapter() {
				@Override
				public void componentAdded(ContainerEvent e) {
					registerClickTarget(e.getChild());
				}
			});
		}
	}
}