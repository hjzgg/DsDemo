package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sf.json.JSONObject;

import com.ds.tools.JavaRequest;

public class RegisterPanel extends JPanel{
	private CommunicationPanel parentPanel;
	
	private JPanel registerPanel = null;
	private JTextField usernameFiled;
	private JPasswordField passwodFiled;
	private JPasswordField comfirmFiled;
	private JTextField emailField;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon imageIcon = new ImageIcon("image/login_and_register.jpg");
		Image image = imageIcon.getImage();
		if(image != null)
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null), null);
	}
	
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
		
		BackgroundPanel panel_center = new BackgroundPanel("image/lrbg.png");
		add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(0, 0));
		
		registerPanel = new JPanel();
		registerPanel.setOpaque(false);
		registerPanel.setBorder(BorderFactory.createTitledBorder("用户注册"));
		panel_center.add(registerPanel);
		registerPanel.setLayout(new GridLayout(6, 1, 0, 0));
		
		JPanel emptyPanel = new JPanel();
		emptyPanel.setOpaque(false);
		registerPanel.add(emptyPanel);
		
		JPanel panel_register_name = new JPanel();
		panel_register_name.setOpaque(false);
		registerPanel.add(panel_register_name);
		panel_register_name.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel usernameLabel = new JLabel("\u7528\u6237");
		panel_register_name.add(usernameLabel);
		
		usernameFiled = new JTextField();
		usernameFiled.setToolTipText("\u7528\u6237\u540D");
		panel_register_name.add(usernameFiled);
		usernameFiled.setColumns(20);
		
		JPanel panel_register_email = new JPanel();
		panel_register_email.setOpaque(false);
		registerPanel.add(panel_register_email);
		panel_register_email.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel emailLabel = new JLabel("邮件");
		panel_register_email.add(emailLabel);
		emailField = new JTextField();
		emailField.setToolTipText("邮件");
		panel_register_email.add(emailField);
		emailField.setColumns(20);
		
		JPanel panel_register_password = new JPanel();
		panel_register_password.setOpaque(false);
		registerPanel.add(panel_register_password);
		panel_register_password.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801");
		panel_register_password.add(passwordLabel);
		
		passwodFiled = new JPasswordField();
		passwodFiled.setToolTipText("\u5BC6\u7801");
		panel_register_password.add(passwodFiled);
		passwodFiled.setColumns(20);
		
		JPanel panel_confirm_password = new JPanel();
		panel_confirm_password.setOpaque(false);
		FlowLayout flowLayout_2 = (FlowLayout) panel_confirm_password.getLayout();
		registerPanel.add(panel_confirm_password);
		
		JLabel comfirmLabel = new JLabel("\u786E\u8BA4");
		comfirmLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_confirm_password.add(comfirmLabel);
		
		comfirmFiled = new JPasswordField();
		comfirmFiled.setToolTipText("\u786E\u8BA4\u5BC6\u7801");
		panel_confirm_password.add(comfirmFiled);
		comfirmFiled.setColumns(20);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(20);
		registerPanel.add(panel);
		
		//进行注册
		JButton button = new JButton("\u6CE8\u518C");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(usernameFiled.getText().isEmpty() || passwodFiled.getText().isEmpty()){
						JOptionPane.showMessageDialog(null, "姓名或密码不能为空!", "温馨提示", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					if(passwodFiled.getText().equals(comfirmFiled.getText())){
						JSONObject jsono = new JSONObject();
						jsono.put("username", URLEncoder.encode(usernameFiled.getText(), "utf-8"));
						jsono.put("password", passwodFiled.getText());
						jsono.put("email", emailField.getText());
						String data = JavaRequest.sendPost("userRegister", jsono);
						JSONObject result = JSONObject.fromObject(data);
						if((Boolean) result.get("success")){
							JOptionPane.showMessageDialog(null, "register success.", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
							parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
						} else {
						    JOptionPane.showMessageDialog(null, result.get("message"), "温馨提示",JOptionPane.WARNING_MESSAGE);  
						}
					} else {
						JOptionPane.showMessageDialog(null, "两次输入密码不一致!", "温馨提示", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ez) {
					ez.printStackTrace();
				}
			}
		});
		panel.add(button);
		
		//返回登录页面
		JButton toLoginBtn = new JButton("\u8FD4\u56DE\u767B\u5F55");
		toLoginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		});
		panel.add(toLoginBtn);
		
		panel_down.setOpaque(false);
		panel_left.setOpaque(false);
		panel_right.setOpaque(false);
		panel_top.setOpaque(false);
	}
}
