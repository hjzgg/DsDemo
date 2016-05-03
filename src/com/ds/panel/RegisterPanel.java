package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class RegisterPanel extends JPanel{
	private CommunicationPanel parentPanel;
	
	private JPanel registerPanel = null;
	private JTextField usernameFiled;
	private JTextField passwodFiled;
	private JTextField comfirmFiled;
	public RegisterPanel(CommunicationPanel pPanel) {
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
		add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(0, 0));
		
		registerPanel = new JPanel();
		registerPanel.setBorder(BorderFactory.createTitledBorder("�û�ע��"));
		panel_center.add(registerPanel);
		registerPanel.setLayout(new GridLayout(4, 1, 0, 0));
		
		JPanel panel_register_name = new JPanel();
		registerPanel.add(panel_register_name);
		panel_register_name.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel usernameLabel = new JLabel("\u7528\u6237");
		panel_register_name.add(usernameLabel);
		
		usernameFiled = new JTextField();
		usernameFiled.setToolTipText("\u7528\u6237\u540D");
		panel_register_name.add(usernameFiled);
		usernameFiled.setColumns(20);
		
		JPanel panel_register_password = new JPanel();
		registerPanel.add(panel_register_password);
		panel_register_password.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801");
		panel_register_password.add(passwordLabel);
		
		passwodFiled = new JTextField();
		passwodFiled.setToolTipText("\u5BC6\u7801");
		panel_register_password.add(passwodFiled);
		passwodFiled.setColumns(20);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_1.getLayout();
		registerPanel.add(panel_1);
		
		JLabel comfirmLabel = new JLabel("\u786E\u8BA4");
		comfirmLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(comfirmLabel);
		
		comfirmFiled = new JTextField();
		comfirmFiled.setToolTipText("\u786E\u8BA4\u5BC6\u7801");
		panel_1.add(comfirmFiled);
		comfirmFiled.setColumns(20);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(20);
		registerPanel.add(panel);
		
		//����ע��
		JButton button = new JButton("\u6CE8\u518C");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel.add(button);
		
		//���ص�¼ҳ��
		JButton toLoginBtn = new JButton("\u8FD4\u56DE\u767B\u5F55");
		toLoginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		});
		panel.add(toLoginBtn);
	}
}