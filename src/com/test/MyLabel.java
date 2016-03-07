package com.test;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MyLabel extends JLabel{

	/*
		自定义的Lable, 实现圆形或者是矩形的3d形状
	*/
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x1 = 100, y1 = 100, x2 = 300, y2 = 300;
		g.drawLine(x1, y1, x2, y2);
		g.drawArc((x1+x2)/2, (y1+y2)/2, 200, 100, 0, 180);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		MyLabel label = new MyLabel();
		frame.add(label);
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
