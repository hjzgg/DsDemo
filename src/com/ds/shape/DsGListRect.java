package com.ds.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

//广义表一共三个字段， 真值，水平指针，垂直指针
public class DsGListRect implements Shape{
	public int lx, ly, lw, lh;
	public String contentValue;
	public String contentTag;
	public String contentPtrContent = null;
	@Override
	public void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lx, ly, lw, lh);
		g.setColor(Color.RED);
		g.fillRect(lx+1, ly+1, lw-4, lh-4);
		g.setFont(new Font("华文行楷", Font.BOLD, 35));
		//设置线型
		((Graphics2D)g).setStroke(new BasicStroke(1));
		FontMetrics fontMetrics = g.getFontMetrics();
		int x = lx+1, y = ly+1, w=(lw-5)/3, h=lh-4;
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		int contentW = fontMetrics.stringWidth(contentTag);
		g.setColor(Color.BLACK);
		g.drawString(contentTag, x+(w-contentW)/2, y+(h-contentH)/2);
		g.drawLine(x+w, y, x+w, y+h);
		if(contentValue == null){
			w+=(lw-5)/3;
			g.drawLine(x+w, y, x+w, y+h);
			if(contentPtrContent != null){
				contentW = fontMetrics.stringWidth(contentPtrContent);
				g.drawString(contentPtrContent, x+w+((lw-5)/3-contentW)/2, y+(h-contentH)/2);
			}
		} else {
			contentW = fontMetrics.stringWidth(contentValue);
			g.drawString(contentValue, x+w+(lw-w-contentW)/2, y+(h-contentH)/2);
		}
	}
	
	public DsGListRect(int lx, int ly, int lw, int lh) {
		super();
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
	}
	
}
