package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import com.ds.size.ShapeSize;

public class DsListRect implements Shape, Cloneable{
	public int lx, ly, lw, lh;
	public Color color = null;
	public String content;
	public boolean isDrawColLine = true;
	public boolean isLineDrawRight = true;
	@Override
	public void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lx, ly, lw, lh);
		if(color == null)
			g.setColor(Color.RED);
		else 
			g.setColor(color);
		g.fillRect(lx+1, ly+1, lw-4, lh-4);
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, 35));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(Color.BLACK);
		if(isDrawColLine){
			int tw = ShapeSize.ListModel.RECT_WIDTH - ShapeSize.ListModel.SMALL_RECT_WIDTH;
			if(isLineDrawRight){
				g.drawLine(lx+tw, ly+1, lx+tw, ly+lh-4);
				g.drawString(content, lx+(tw-contentW)/2, ly+(lh-contentH)/2);
			} else {
				g.drawLine(lx+ShapeSize.ListModel.SMALL_RECT_WIDTH, ly+1, lx+ShapeSize.ListModel.SMALL_RECT_WIDTH, ly+lh-4);
				g.drawString(content, lx+ShapeSize.ListModel.SMALL_RECT_WIDTH+(tw-contentW)/2, ly+(lh-contentH)/2);
			}
		} else {
			g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2);
		}
	}

	public DsListRect(int lx, int ly, int lw, int lh, String content) {
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
