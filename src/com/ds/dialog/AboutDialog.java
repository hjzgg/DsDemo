package com.ds.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class AboutDialog extends JDialog
{
  Font f, f2;

  public AboutDialog(JFrame parent)
  {
	super(parent, true);
    setTitle("About");
    setLayout(new FlowLayout());

    f = new Font("宋体", Font.BOLD, 20);
    f2 = new Font("宋体", Font.PLAIN, 12);

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //getting the current screen resolution

    setBounds((dim.width-300)/2, (dim.height-190)/2, 300, 190);
    setResizable(false);
  }

  public void paint(Graphics g)
  {
	super.paint(g);
    g.setColor(Color.red);
    g.setFont(f);
    g.drawString("数据结构算法模拟系统 1.0", 10, 60);
    g.setColor(Color.black);
    g.setFont(f2);
    g.drawString("Author: 胡峻峥", 10, 90);
    g.drawString("http://www.cnblogs.com/hujunzheng/", 10, 110);
    g.drawString("https://github.com/hjzgg?tab=repositories", 10, 130);
    g.drawString("2570230521@qq.com", 10, 150);
    g.drawString("All rights reserved.", 10, 170);
  }
}
