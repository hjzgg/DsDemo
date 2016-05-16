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
		btn.setPreferredSize(new Dimension(100, 35));// ���ð�ť��С
		btn.setFont(new Font("����", Font.PLAIN, 15));// ��ť�ı���ʽ
		btn.setMargin(new Insets(5, 5, 5, 5));// ��ť������߿����
		return btn;
	}
}
