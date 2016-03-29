package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsSampleCircle extends DsCircle implements Cloneable{
	private boolean isTransparent = false;
	public void setTransparent(boolean isTransparent){
		this.isTransparent = isTransparent;
	}
	@Override
	public void drawShape(Graphics g) {
		g.setColor(color == null ? Color.RED : color);
		if(!isTransparent)
			g.fillOval(lx, ly, lw, lh);
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, fontSize < 0 ? 35 : fontSize));
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