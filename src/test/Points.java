package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Surface extends JPanel {
	private static final long serialVersionUID = 1L;

	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.blue);
		Dimension size = getSize();
		Insets insets = getInsets();
		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			int x = Math.abs(r.nextInt()) % w;
			int y = Math.abs(r.nextInt()) % h;
			g2d.drawLine(x, y, x, y);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}
}

class myFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public myFrame() {
		setTitle("Points");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new Surface());
		setSize(350, 250);
		setLocationRelativeTo(null);
	}
	
	
}

class myThread extends Thread {
	@Override
	public void run() {
		while (true) {
			myFrame ps = new myFrame();
			ps.setVisible(true);
			ps.repaint();
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class Points {
	public static void main(String[] args) {
		myThread th = new myThread();
		th.start();
	}
}
