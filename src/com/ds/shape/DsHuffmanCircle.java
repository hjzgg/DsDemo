package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DsHuffmanCircle extends DsCircle{
	public String nodeTip;
	
	@Override
	public void drawShape(Graphics g) {
		super.drawShape(g);
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, 15));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(nodeTip);
		g.setColor(Color.YELLOW);
		g.drawString(nodeTip, lx+(lw-contentW)/2, ly-contentH);
	}

	public DsHuffmanCircle(int lx, int ly, int lw, int lh, String content, String nodeTip) {
		super(lx, ly, lw, lh, content);
		this.nodeTip = nodeTip;
	}
}
