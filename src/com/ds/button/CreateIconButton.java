package com.ds.button;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class CreateIconButton {
	public static  JButton createBtn(String text, String icon) {
		JButton btn = new JButton(text, new ImageIcon(icon));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(100, 35));// 设置按钮大小
		btn.setFont(new Font("粗体", Font.PLAIN, 15));// 按钮文本样式
		btn.setMargin(new Insets(5, 5, 5, 5));// 按钮内容与边框距离
		return btn;
	}
}
