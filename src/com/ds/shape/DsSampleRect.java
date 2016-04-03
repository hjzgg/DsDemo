package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsSampleRect extends DsRect implements Cloneable{
	public Color fontColor = Color.BLACK;
	@Override
	public void drawShape(Graphics g) {
		g.setColor(color == null ? Color.RED : color);
		g.fillRect(lx, ly, lw, lh);
		if(content == null) return;
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, fontSize < 0 ? 35 : fontSize));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(fontColor);
		g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2);
	}

	@Override
	public Object clone() {
		Object obj = null;
		obj = super.clone();
		return obj;
	}
	public DsSampleRect(int lx, int ly, int lw, int lh, String content) {
		super(lx, ly, lw, lh, content);
	}
}
