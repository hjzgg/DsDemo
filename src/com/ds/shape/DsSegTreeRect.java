package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsSegTreeRect implements Shape{
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
		g.setFont(new Font("华文行楷", Font.BOLD, 18));
		FontMetrics fontMetrics = g.getFontMetrics();
		contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		contentW = fontMetrics.stringWidth(contentLeft);
		g.drawString(contentLeft, x+(tw-contentW)/2, y+(h-contentH)/2);
		g.drawLine(x+w, y, x+w, y+h);
		g.setFont(new Font("华文行楷", Font.BOLD, 15));
		fontMetrics = g.getFontMetrics();
		contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		contentW = fontMetrics.stringWidth(contentRight);
		g.drawString(contentRight, x+w+(2*tw-contentW)/2, y+(h-contentH)/2);
	}
	
	/**
	 * @param lx
	 * @param ly
	 * @param lw
	 * @param lh	Rect 的尺寸
	 * @param contentLeft	线段树的区间 极值
	 * @param contentCenter	左区间标识
	 * @param contentRight	右区间标识
	 */
	public DsSegTreeRect(int lx, int ly, int lw, int lh, String contentLeft,
			 String contentRight) {
		super();
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
		this.contentLeft = contentLeft;
		this.contentRight = contentRight;
	}
	
}
