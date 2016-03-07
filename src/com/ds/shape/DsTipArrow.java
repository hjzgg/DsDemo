package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class DsTipArrow implements Shape{
	public int x, y;
	public String content;
	@Override
	public void drawShape(Graphics g) {
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, 35));
		g.setColor(Color.GREEN);
		g.drawString(content, x, y);
		g.setColor(Color.RED);
		g.drawString(content, x+3, y);
	}

	public DsTipArrow(int x, int y, String content) {
		super();
		this.x = x;
		this.y = y;
		this.content = content;
	}
}
