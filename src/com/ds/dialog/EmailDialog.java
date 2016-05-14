package com.ds.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.json.JSONObject;

import com.ds.panel.CommunicationPanel;
import com.ds.shape.DsWaiting;
import com.ds.tools.JavaRequest;
import com.ds.underlineLabel.JLabelUnderLinePerfect;

import javax.swing.JSlider;

class EmailPanel extends JPanel{

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		ImageIcon imageIcon = new ImageIcon("image/email_bg.jpg");
		Image image = imageIcon.getImage(); 
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null),  null);
	}
}

public class EmailDialog extends JDialog{
	
	private final int width = 500;
	private final int height = 500;
	private JTextField userName;
	private JTextField emaiAddress;
	private JTextField emailSubject;
	private JTextArea emailContent;
	private CommunicationPanel parentPanel;
	private JLabel waitLabel;
	//请求用户信息
	public void setUserMsg(String userName, CommunicationPanel parentPanelx){
		this.parentPanel = parentPanelx;
		this.userName.setText(userName);
		getEmailAddress();
	}
	
	private void getEmailAddress(){
		JSONObject jsono = new JSONObject();
		jsono.put("userName", userName.getText());
		String data = JavaRequest.sendPost("getUserMsg", jsono);
		JSONObject result = JSONObject.fromObject(data);
		if((Boolean) result.get("success")){
			emaiAddress.setText((String) result.get("message"));
		} else {
		    JOptionPane.showMessageDialog(null, result.get("message"), "温馨提示",JOptionPane.WARNING_MESSAGE); 
		    if(result.get("returnLogin") != null && (Boolean)result.get("returnLogin")){//如果用户登录超时返回用户登陆界面
		    	EmailDialog.this.dispose();
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		}
	}
	
	public EmailDialog(Frame owner, boolean modal) {
		super(owner, modal);
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenDimension.width-width)/2, (screenDimension.height-height)/2, width, height);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		final EmailPanel panel = new EmailPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		waitLabel = new DsWaiting();
		waitLabel.setBounds(403, 365, 30, 30);
		
		JLabel label = new JLabel("\u7528\u6237\u540D");
		label.setBounds(87, 102, 54, 15);
		panel.add(label);
		
		userName = new JTextField();
		userName.setColumns(20);
		userName.setBounds(151, 99, 150, 21);
		panel.add(userName);
		
		emaiAddress = new JTextField();
		emaiAddress.setColumns(20);
		emaiAddress.setBounds(151, 140, 150, 21);
		panel.add(emaiAddress);
		
		JLabel label_1 = new JLabel("\u90AE\u4EF6\u4E3B\u9898");
		label_1.setBounds(87, 185, 54, 15);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("\u5730\u5740");
		label_2.setBounds(87, 143, 78, 15);
		panel.add(label_2);
		
		emailSubject = new JTextField();
		emailSubject.setColumns(20);
		emailSubject.setBounds(151, 182, 150, 21);
		panel.add(emailSubject);
		
		JLabel label_3 = new JLabel("\u90AE\u4EF6\u5185\u5BB9");
		label_3.setBounds(87, 225, 54, 15);
		panel.add(label_3);
		
		emailContent = new JTextArea();
		emailContent.setBounds(87, 250, 346, 147);
		panel.add(emailContent);
		
		JButton sendBtn = new JButton("\u53D1\u9001\u90AE\u4EF6");
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JSONObject jsono = new JSONObject();
				jsono.put("title", emailSubject.getText());
				jsono.put("content", emailContent.getText());
				jsono.put("address", emaiAddress.getText());
				panel.add(waitLabel, 0);
				panel.updateUI();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						String data = JavaRequest.sendPost("sendEmail", jsono);
						JSONObject result = JSONObject.fromObject(data);
						
						if(!(Boolean) result.get("success")){//发送邮件失败
							panel.remove(waitLabel);
							JOptionPane.showMessageDialog(null, result.get("message"), "温馨提示", JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(null, result.get("message"), "温馨提示", JOptionPane.INFORMATION_MESSAGE);
							EmailDialog.this.dispose();//窗口关掉
						}
					}
				}).start();
			}
		});
		sendBtn.setBounds(87, 419, 93, 23);
		panel.add(sendBtn);
		
		JLabelUnderLinePerfect getAddressLabel = new JLabelUnderLinePerfect("\u83B7\u53D6\u6536\u4EF6\u4EBA\u5730\u5740");
		getAddressLabel.setUnderLineColor(Color.RED);
		getAddressLabel.setBounds(311, 102, 101, 15);
		getAddressLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		getAddressLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				getEmailAddress();
			}
		});
		panel.add(getAddressLabel);
	}
}
