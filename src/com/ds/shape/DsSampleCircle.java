package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsSampleCircle extends DsCircle implements Cloneable{
	@Override
	public void drawShape(Graphics g) {
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, fontSize < 0 ? 35 : fontSize));
		g.setColor(color);
		g.fillOval(lx, ly, lw, lh);
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(Color.BLACK);
		g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2);
	}
	public DsSampleCircle(int lx, int ly, int lw, int lh, String content) {
		super(lx, ly, lw, lh, content);
	}
}