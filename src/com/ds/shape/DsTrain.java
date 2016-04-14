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

public class DsTrain extends JLabel{
	private ImageIcon icon = null;
	private String number;
	private int imageIndex = 0;
	
	private int lx, ly;
 
	public void setXY(int x, int y){
		this.lx = x;
		this.ly = y;
		setBounds(lx, ly, this.getWidth(), this.getHeight());
	}
	
	
	public static  final int imageNum = 3;
	@Override
	protected void paintComponent(Graphics g) {
		if(icon == null)
			icon = new ImageIcon("image/train/train_" + "left" + "0.png");
		g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.BOLD, 20));
		FontMetrics fontMetrics = g.getFontMetrics();
		int contentH = fontMetrics.getHeight();
		int contentW = fontMetrics.stringWidth(number);
		g.setColor(Color.BLACK);
		g.drawString(number, (this.getWidth()-contentW)/2, contentH/2);
		g.drawImage(icon.getImage(), 5, contentH/2, this.getWidth()-10, this.getHeight()-10, null);
		super.paintComponent(g);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		DsTrain train = new DsTrain("11");
		train.setPreferredSize(new Dimension(70, 60));
		frame.add(train);
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public DsTrain(String number) {
        this.number = number;
		MyTimer.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				imageIndex = ++imageIndex % imageNum;
				icon = new ImageIcon("image/train/train_" + "left" + imageIndex + ".png");
				DsTrain.this.updateUI();
			}
		}, 100, 100);
	}
}
