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
		int x = 100, y = 100;
		g.drawArc(x, y, 50, 20, 180, 200);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		MyLabel label = new MyLabel();
		frame.add(label);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
