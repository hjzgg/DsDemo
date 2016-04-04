package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

//»­Ô²
public class DsCircle implements Shape, Cloneable{
	public int lx, ly, lw, lh;
	public String content;
	public Color color = Color.RED;
	public int fontSize = 35;
	@Override
	public void drawShape(Graphics g) {
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, fontSize < 0 ? 35 : fontSize));
		g.setColor(Color.BLACK);
		g.fillOval(lx, ly, lw, lh);
		g.setColor(color);
		g.fillOval(lx+2, ly+2, lw-6, lh-6);
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(Color.BLACK);
		g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2);
	}
	
	public Point getPoint(){
		return new Point(lx, ly);
	}

	public DsCircle(int lx, int ly, int lw, int lh, String content) {
		super();
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
		this.content = content;
	}

	@Override
	public Object clone(){
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}