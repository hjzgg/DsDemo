package com.ds.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.ds.size.ShapeSize;

public class DsCrossList implements Shape{
	public int lx, ly, lw, lh;
	public String s1 = "^";
	public String s2 = "^";
	public String s3 = "^";
	public String s4 = "^";
	@Override
	public void drawShape(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(lx, ly, lw, lh);
		g.setColor(Color.RED);
		g.fillRect(lx+1, ly+1, lw-4, lh-4);
		g.setFont(new Font("华文行楷", Font.BOLD, 20));
		//设置线型
		((Graphics2D)g).setStroke(new BasicStroke(1));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
		g.setColor(Color.BLACK);
		if(lw == ShapeSize.CrossListModel.NODE_ARCBOX_WIDTH){//绘制4个方格   ARCBOX结构体
			int x = lx+1, y = ly+1, w=(lw-5)/4, tw = w, h=lh-4;
			int contentW = fontMetrics.stringWidth(s1);
			g.drawString(s1, x+(tw-contentW)/2, y+(h-contentH)/2);
			g.drawLine(x+w, y, x+w, y+h);
			contentW = fontMetrics.stringWidth(s2);
			g.drawString(s2, x+w+(tw-contentW)/2, y+(h-contentH)/2);
			w += (lw-5)/4;
			g.drawLine(x+w, y, x+w, y+h);
			if(s3 != null){
				contentW = fontMetrics.stringWidth(s3);
				g.drawString(s3, x+w+(tw-contentW)/2, y+(h-contentH)/2);
			}
			w += (lw-5)/4;
			g.drawLine(x+w, y, x+w, y+h);
			if(s4 != null){
				contentW = fontMetrics.stringWidth(s4);
				g.drawString(s4, x+w+(tw-contentW)/2, y+(h-contentH)/2);
			}
		} else {//绘制3个方格， Vector Node结构体
			int x = lx+1, y = ly+1, w=(lw-5)/3, tw = w, h=lh-4;
			g.setFont(new Font("华文行楷", Font.BOLD, 12));
			fontMetrics = g.getFontMetrics();
			contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
			int contentW = fontMetrics.stringWidth(s1);
			g.drawString(s1, x+(tw-contentW)/2, y+(h-contentH)/2);
			g.setFont(new Font("华文行楷", Font.BOLD, 20));
			fontMetrics = g.getFontMetrics();
			contentH = fontMetrics.getLeading()-fontMetrics.getAscent();
			g.drawLine(x+w, y, x+w, y+h);
			if(s2 != null){
				contentW = fontMetrics.stringWidth(s2);
				g.drawString(s2, x+w+(tw-contentW)/2, y+(h-contentH)/2);
			}
			w += (lw-5)/3;
			g.drawLine(x+w, y, x+w, y+h);
			if(s3 != null){
				contentW = fontMetrics.stringWidth(s3);
				g.drawString(s3, x+w+(tw-contentW)/2, y+(h-contentH)/2);
			}
		}
	}
	
	public DsCrossList(int lx, int ly, int lw, int lh) {
		super();
		this.lx = lx;
		this.ly = ly;
		this.lw = lw;
		this.lh = lh;
	}
	
}
