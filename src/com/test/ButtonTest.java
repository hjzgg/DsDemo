package com.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonTest extends JButton{
	private ImageIcon icon = null;
	private int imageIndex = 0;
	private final int imageNum = 39;
	@Override
	protected void paintComponent(Graphics g) {
		if(icon == null)
			icon = new ImageIcon("image/btnBg/back0.jpg");
		g.drawImage(icon.getImage(), 5, 5, this.getWidth()-10, this.getHeight()-10, null);
		super.paintComponent(g);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.add(new ButtonTest("hjzgg"));
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public ButtonTest(String text) {
		super(text);
        setContentAreaFilled(false); 
        setForeground(Color.WHITE);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				imageIndex = ++imageIndex % imageNum;
				icon = new ImageIcon("image/btnBg/back" + imageIndex + ".jpg");
				ButtonTest.this.updateUI();
			}
		}, 100, 100);
	}
}
