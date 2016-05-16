package com.ds.panel;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel{
	private String imagePath;
	public BackgroundPanel(String imagePath){
		this.imagePath = imagePath;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon imageIcon = new ImageIcon(imagePath);
		Image image = imageIcon.getImage();
		if(image != null)
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null), null);
	}
}
