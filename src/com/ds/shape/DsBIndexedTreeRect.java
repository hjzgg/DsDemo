package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsBIndexedTreeRect implements Shape{
	public int lx, ly, lw, lh;
	public String contentLeft;
	public String contentRight;
	@Override
	public void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lx, ly, lw, lh);
		g.setColor(Color.RED);
		g.fillRect(lx+1, ly+1, lw-4, lh-4);
		int x = lx+1, y = ly+1, w=(lw-5)/3, tw = w, h=lh-4;
		int contentW, contentH;
		g.setColor(Color.BLACK);
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, 18));
		FontMetrics fontMetrics = g.getFontMetrics();
		contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		contentW = fontMetrics.stringWidth(contentLeft);
		g.drawString(contentLeft, x+(tw-contentW)/2, y+(h-contentH)/2);
		g.drawLine(x+w, y, x+w, y+h);
		contentW = fontMetrics.stringWidth(contentRight);
		g.drawString(contentRight, x+w+(2*tw-contentW)/2, y+(h-contentH)/2);
	}
	public DsBIndexedTreeRect(int lx, int ly, int lw, int lh,
			String contentLeft, String contentRight) {
		super();
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
		this.contentLeft = contentLeft;
		this.contentRight = contentRight;
	}
	
}
