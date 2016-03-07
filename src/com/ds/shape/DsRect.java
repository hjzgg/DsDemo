package com.ds.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;

import com.ds.size.ShapeSize;


//画矩形
public class DsRect implements Shape, Cloneable, Comparable<DsRect>{
	public int lx, ly, lw, lh;
	public Color color = null;
	public String content;
	public int fontSize = 35;
	@Override
	public void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lx, ly, lw, lh);
		g.setColor(color == null ? Color.RED : color);
		g.fillRect(lx+1, ly+1, lw-4, lh-4);
		g.setFont(new Font("华文行楷", Font.BOLD, fontSize < 0 ? 35 : fontSize));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(content);
		g.setColor(Color.BLACK);
		g.drawString(content, lx+(lw-contentW)/2, ly+(lh-contentH)/2);
	}

	public DsRect(int lx, int ly, int lw, int lh, String content) {
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

	@Override
	public int compareTo(DsRect o) {
		return this.hashCode() - o.hashCode();
	}
}
