package com.ds.shape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ds.button.FunctionButton;
import com.ds.tools.MyTimer;

public class DsPeople extends JLabel{
	private ImageIcon icon = null;
	private int dir = DIR_LEFT;
	private int imageIndex = 0;
	public static final int DIR_LEFT = 0;
	public static final int DIR_RIGHT = 1;
	public static final int DIR_UP = 2;
	public static final int DIR_DOWN = 3;
	
	private static final String[] dirs = {"left", "right", "up", "down"};
	
	public static  final int imageNum = 3;
	@Override
	protected void paintComponent(Graphics g) {
		if(icon == null)
			icon = new ImageIcon("image/queuOfPeople/people_" + dirs[dir] + "0.gif");
		g.drawImage(icon.getImage(), 5, 5, this.getWidth()-10, this.getHeight()-10, null);
		super.paintComponent(g);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		DsPeople people = new DsPeople(3);
		people.setPreferredSize(new Dimension(45, 60));
		frame.add(people);
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public DsPeople(int dir) {
        setForeground(Color.WHITE);
		this.dir = dir;
		MyTimer.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				imageIndex = ++imageIndex % imageNum;
				icon = new ImageIcon("image/queuOfPeople/people_" + dirs[DsPeople.this.dir] + imageIndex + ".gif");
				DsPeople.this.updateUI();
			}
		}, 100, 100);
	}
}
