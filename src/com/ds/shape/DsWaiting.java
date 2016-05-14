package com.ds.shape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ds.tools.MyTimer;

public class DsWaiting extends JLabel{
	private ImageIcon icon = null;
	private int imageIndex = 0;
	public static  final int imageNum = 13;
	@Override
	protected void paintComponent(Graphics g) {
		if(icon == null)
			icon = new ImageIcon("image/wait/wait0.jpg");
		g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		super.paintComponent(g);
	}
	
	public DsWaiting(){
		MyTimer.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				imageIndex = ++imageIndex % imageNum;
				icon = new ImageIcon("image/wait/wait" + imageIndex + ".jpg");
				DsWaiting.this.updateUI();
			}
		}, 100, 100);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		DsWaiting wait = new DsWaiting();
		wait.setPreferredSize(new Dimension(30, 30));
		frame.add(wait);
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
