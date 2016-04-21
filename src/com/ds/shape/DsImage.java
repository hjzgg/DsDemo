package com.ds.shape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ds.tools.MyTimer;

public class DsImage extends JLabel{
	private ImageIcon icon = null;
	private String imagePath;
	@Override
	protected void paintComponent(Graphics g) {
		if(icon == null)
			icon = new ImageIcon(this.imagePath);
		g.drawImage(icon.getImage(), 5, 5, this.getWidth()-10, this.getHeight()-10, null);
		super.paintComponent(g);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		DsImage people = new DsImage("image/wall.png");
		people.setPreferredSize(new Dimension(70, 70));
		frame.add(people);
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public DsImage(String imagePath) {
         this.imagePath = imagePath;
	}
}
