package com.ben.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class Main extends JFrame{

	int width = 400;
	int height = 300;
	Dimension screenSize = new Dimension(width, height);
	
	Image dbImage;
	Graphics dbg;
	
	static Ball b = new Ball(200, 150);
	
	public class AL extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			b.p1.keyPressed(e);
			b.p2.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			b.p1.keyReleased(e);
			b.p2.keyReleased(e);
		}
	}
	
	public void paint(Graphics g) {
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		draw(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.white);
		b.draw(g);
		b.p1.draw(g);
		b.p2.draw(g);
		g.drawString(b.p1Score + " | " + b.p2Score, 185, 40);
		repaint();
	}
	
	public Main() {
		addKeyListener(new AL());
		this.setTitle("Pong");
		this.setResizable(false);
		this.setVisible(true);
		this.setSize(screenSize);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	public static void main(String[] args) {
		Main game = new Main();
		Thread ball = new Thread(b);
		ball.start();
		Thread player1 = new Thread(b.p1);
		player1.start();
		Thread player2 = new Thread(b.p2);
		player2.start();
	}
}
