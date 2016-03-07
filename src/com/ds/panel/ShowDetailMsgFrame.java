package com.ds.panel;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.FlowLayout;

public class ShowDetailMsgFrame extends JFrame {
    private String msg;
    private void initGUI(){
    	Font font = new Font("微软雅黑 Light", Font.PLAIN, 15);
    	FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    	int msgW = fontMetrics.stringWidth(msg);
    	int msgH = fontMetrics.getHeight();
    	setBounds(0, 0, msgW+10, msgH+10);
    	
    	JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignOnBaseline(true);
		panel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel(msg);
		lblNewLabel.setFont(font);
		panel.add(lblNewLabel);
		//设置此窗口为不可获取焦点的窗口
		setFocusableWindowState(false);
		//取消所有的事件的接收
		setEnabled(false);
    	setAlwaysOnTop(true);
    	setUndecorated(true);
    }
    
	public ShowDetailMsgFrame(String msg)
			throws HeadlessException {
		super();
		this.msg = msg;
		initGUI();
	}
}
