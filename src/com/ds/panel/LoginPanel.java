package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sf.json.JSONObject;

import com.ds.tools.JavaRequest;

public class LoginPanel extends JPanel{
	private CommunicationPanel parentPanel;
	
	private JPanel loginPanel = null;
	private JTextField usernameFiled;
	private JPasswordField passwodFiled;
	private JTextField codeField;
	private JLabel codeImgLabel;
	
	public void initCodeImage(){
		ImageIcon codeImg = JavaRequest.getCodeImage();
		if(codeImg == null){
			codeImg = new ImageIcon("image/badImage.png");
		}
		codeImgLabel.setIcon(codeImg);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon imageIcon = new ImageIcon("image/login_and_register.jpg");
		Image image = imageIcon.getImage();
		if(image != null)
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null), null);
	}
	
	public LoginPanel(CommunicationPanel pPanel) {
		this.parentPanel = pPanel;
		setLayout(new BorderLayout(0, 0));
		
		BackgroundPanel panel_center = new BackgroundPanel("image/lrbg.png");
		add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new BorderLayout(0, 0));
		loginPanel = new JPanel();
		loginPanel.setOpaque(false);
		loginPanel.setBorder(BorderFactory.createTitledBorder("用户登录"));
		panel_center.add(loginPanel);
		loginPanel.setLayout(new GridLayout(6, 1, 0, 0));
		
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
		
		JPanel emptyPanel = new JPanel();
		emptyPanel.setOpaque(false);
		loginPanel.add(emptyPanel);
		
		JPanel panel_login_name = new JPanel();
		panel_login_name.setOpaque(false);
		loginPanel.add(panel_login_name);
		panel_login_name.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel usernameLabel = new JLabel("\u7528\u6237");
		panel_login_name.add(usernameLabel);
		
		usernameFiled = new JTextField();
		panel_login_name.add(usernameFiled);
		usernameFiled.setColumns(20);
		
		JPanel panel_login_password = new JPanel();
		panel_login_password.setOpaque(false);
		loginPanel.add(panel_login_password);
		panel_login_password.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801");
		panel_login_password.add(passwordLabel);
		
		passwodFiled = new JPasswordField();
		panel_login_password.add(passwodFiled);
		passwodFiled.setColumns(20);
		
		JPanel panel_login_code = new JPanel();
		panel_login_code.setOpaque(false);
		loginPanel.add(panel_login_code);
		panel_login_code.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel codeLabel = new JLabel("验证码");
		panel_login_code.add(codeLabel);
		
		codeField = new JTextField(8);
		panel_login_code.add(codeField);
		
		codeImgLabel = new JLabel();
		codeImgLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ImageIcon codeImg = JavaRequest.getCodeImage();
				if(codeImg == null){
					codeImg = new ImageIcon("image/badImage.png");
				}
				((JLabel)e.getSource()).setIcon(codeImg);
			}
		});
		
		codeImgLabel.setPreferredSize(new Dimension(55, 25));
		panel_login_code.add(codeImgLabel);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(20);
		loginPanel.add(panel);
		
		JCheckBox checkBox = new JCheckBox("\u8BB0\u4F4F\u6211");
		checkBox.setOpaque(false);
		panel.add(checkBox);
		
		//登录按钮
		JButton button = new JButton("\u767B\u5F55");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if(usernameFiled.getText().isEmpty() || passwodFiled.getText().isEmpty()){
						JOptionPane.showMessageDialog(null, "姓名或密码不能为空!", "温馨提示", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					JSONObject jsono = new JSONObject();
					jsono.put("username", usernameFiled.getText());
					jsono.put("password", passwodFiled.getText());
					jsono.put("code", codeField.getText());
					jsono.put("codeAuth", JavaRequest.codeAuth);
					String data = JavaRequest.sendPost("userLogin", jsono);
					JSONObject result = JSONObject.fromObject(data);
					if((Boolean) result.get("success")){
						//将认证tooken放入请求类中
						JavaRequest.userToken = result.getString("userToken");
						JavaRequest.username = usernameFiled.getText();
						//切换到用户交流信息面板
						parentPanel.switchPanel(CommunicationPanel.MESSAGE_PANEL);
					} else {
						initCodeImage();//重新加载验证码
						JOptionPane.showMessageDialog(null, result.getString("message"), "温馨提示",JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(button);
		
		JPanel login_register_panel = new JPanel();
		login_register_panel.setOpaque(false);
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
		
		panel_down.setOpaque(false);
		panel_left.setOpaque(false);
		panel_right.setOpaque(false);
		panel_top.setOpaque(false);
	}
}
