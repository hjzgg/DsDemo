package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import com.ds.controler.PanelControler;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LoginPanel extends JPanel{
	private CommunicationPanel parentPanel;
	
	private JPanel loginPanel = null;
	private JTextField usernameFiled;
	private JTextField passwodFiled;
	public LoginPanel(CommunicationPanel pPanel) {
		this.parentPanel = pPanel;
		setLayout(new BorderLayout(0, 0));
		
		
		JPanel panel_left = new JPanel();
		panel_left.setPreferredSize(new Dimension(400, 0));
		add(panel_left, BorderLayout.WEST);
		
		JPanel panel_top = new JPanel();
		panel_top.setPreferredSize(new Dimension(0, 200));
		FlowLayout flowLayout_1 = (FlowLayout) panel_top.getLayout();
		add(panel_top, BorderLayout.NORTH);
		
		JPanel panel_right = new JPanel();
		panel_right.setPreferredSize(new Dimension(400, 0));
		add(panel_right, BorderLayout.EAST);
		
		JPanel panel_down = new JPanel();
		panel_down.setPreferredSize(new Dimension(0, 200));
		add(panel_down, BorderLayout.SOUTH);
		
		Panel panel_center = new Panel();
		panel_center.setSize(new Dimension(400, 500));
		add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(0, 0));
		loginPanel = new JPanel();
		loginPanel.setBorder(BorderFactory.createTitledBorder("用户登录"));
		panel_center.add(loginPanel);
		loginPanel.setLayout(new GridLayout(4, 1, 0, 0));
		
		JPanel panel_login_name = new JPanel();
		loginPanel.add(panel_login_name);
		panel_login_name.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel usernameLabel = new JLabel("\u7528\u6237");
		panel_login_name.add(usernameLabel);
		
		usernameFiled = new JTextField();
		panel_login_name.add(usernameFiled);
		usernameFiled.setColumns(20);
		
		JPanel panel_login_password = new JPanel();
		loginPanel.add(panel_login_password);
		panel_login_password.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801");
		panel_login_password.add(passwordLabel);
		
		passwodFiled = new JTextField();
		panel_login_password.add(passwodFiled);
		passwodFiled.setColumns(20);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(20);
		loginPanel.add(panel);
		
		JCheckBox checkBox = new JCheckBox("\u8BB0\u4F4F\u6211");
		panel.add(checkBox);
		
		//登录按钮
		JButton button = new JButton("\u767B\u5F55");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//切换到用户交流信息面板
				parentPanel.switchPanel(CommunicationPanel.MESSAGE_PANEL);
			}
		});
		panel.add(button);
		
		JPanel login_register_panel = new JPanel();
		loginPanel.add(login_register_panel);
		
		JLabel registerLabel = new JLabel("还没有账号，注册");
		registerLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				parentPanel.switchPanel(CommunicationPanel.REGISTER_PANEL);
			}
		});
		registerLabel.setForeground(Color.RED);
		registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		login_register_panel.add(registerLabel);
	}
}
