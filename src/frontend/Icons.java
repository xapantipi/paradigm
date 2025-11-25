package frontend;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.Objects;

public final class Icons {
	public Icons() {
		
	}
	
	private static final String base = "/resources/images/";
	
	public static ImageIcon icon(String name, int w, int h) {
		URL url = Objects.requireNonNull(Icons.class.getResource(base + name));
		Image scaled = new ImageIcon(url).getImage().getScaledInstance(w,  h,  Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
