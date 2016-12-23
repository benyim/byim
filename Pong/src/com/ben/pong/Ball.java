package com.ben.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Ball implements Runnable{

	int x, y, xDirection, yDirection;
	int p1Score, p2Score;
	
	Rectangle ball;
	Player p1 = new Player(20, 120, 1);
	Player p2 = new Player(370, 120, 2);
	
	public Ball(int x, int y) {
		p1Score = p2Score = 0;
		this.x = x;
		this.y = y;
		Random r = new Random();
		int rXDir = r.nextInt(2);
		if(rXDir == 0) 
			rXDir--;
		setXDirection(rXDir);
		int rYDir = r.nextInt(2);
		if(rYDir == 0)
			rYDir--;
		setYDirection(rYDir);
		ball = new Rectangle(this.x, this.y, 5, 5);
	}
	
	public void move() {
		collision();
		ball.x += xDirection;
		ball.y += yDirection;
		if(ball.x <= 0){
			setXDirection(+1);
			p2Score++;
		}
		if(ball.x >= 395){
			setXDirection(-1);
			p1Score++;
		}
		if(ball.y <= 20){
			setYDirection(+1);
		}
		if(ball.y >= 298){
			setYDirection(-1);
		}
	}
	
	public void collision() {
		if(ball.intersects(p1.player)) {
			setXDirection(+1);
		}
		if(ball.intersects(p2.player)) {
			setXDirection(-1);
		}
	}
	
	public void setXDirection(int xdir) {
		xDirection = xdir;
	}
	
	public void setYDirection(int ydir) {
		yDirection = ydir;
	}
	
	public void run() {
		try{
			while(true) {
				move();
				
				Thread.sleep(6);
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(ball.x, ball.y, ball.width, ball.width);
	}
	
}
