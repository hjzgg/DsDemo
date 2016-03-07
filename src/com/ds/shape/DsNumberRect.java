package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsNumberRect extends DsRect{
	public Color preColor = Color.BLACK;
	@Override
	public void drawShape(Graphics g) {
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, fontSize));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(preColor);
		g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2); 
	}

	public DsNumberRect(int lx, int ly, int lw, int lh, String content) {
		super(lx, ly, lw, lh, content);
	}
}
