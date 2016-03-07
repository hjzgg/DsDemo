package com.ds.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.ds.tools.MyTimer;


public class FunctionButton extends JButton{
	private ImageIcon icon = null;
	private int imageIndex = 0;
	public static  final int imageNum = 39;
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
		frame.add(new FunctionButton("hjzgg"));
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public FunctionButton(String text) {
		super(text);
        setContentAreaFilled(false); 
        setForeground(Color.WHITE);
		
		MyTimer.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				imageIndex = ++imageIndex % imageNum;
				icon = new ImageIcon("image/btnBg/back" + imageIndex + ".jpg");
				FunctionButton.this.updateUI();
			}
		}, 100, 100);
	}
}
