package com.ben.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Player implements Runnable{

	int x, y, yDirection, id;
	Rectangle player;
	
	public Player(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
		player = new Rectangle(this.x, this.y, 10, 50);
	}
	
	public void run() {
		try{
			while(true) {
				move();
				
				Thread.sleep(7);
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void move() {
		player.y += yDirection;
		if(player.y <= 21) {
			player.y = 21;
		}
		if(player.y >= 250) {
			player.y = 250;
		}
	}
	
	public void setYDirection(int ydir) {
		yDirection = ydir;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (id) {
			default:
				System.out.println("Please put correct number in player constructor. keypressed");
				break;
			case 1:
				if(e.getKeyCode() == e.VK_W)
					setYDirection(-1);
				if(e.getKeyCode() == e.VK_S)
					setYDirection(+1);
				break;
			case 2:
				if(e.getKeyCode() == e.VK_UP)
					setYDirection(-1);
				if(e.getKeyCode() == e.VK_DOWN)
					setYDirection(+1);
				break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		switch (id) {
			default:
				System.out.println("Please put correct number in player constructor.keyreleased");
				break;
			case 1:
				if(e.getKeyCode() == e.VK_W)
					setYDirection(0);
				if(e.getKeyCode() == e.VK_S)
					setYDirection(0);
				break;
			case 2:
				if(e.getKeyCode() == e.VK_UP)
					setYDirection(0);
				if(e.getKeyCode() == e.VK_DOWN)
					setYDirection(0);
				break;
		}
	}
	
	public void draw(Graphics g) {
		switch (id) {
			default:
				System.out.println("Please put correct number in player constructor.draw");
				break;
			case 1:
				g.fillRect(player.x, player.y, player.width, player.height);
				break;
			case 2:
				g.fillRect(player.x, player.y, player.width, player.height);
				break;
		}
	}
}
