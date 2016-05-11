package com.ds.panel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class CommunicationPanel extends JPanel{
	//��¼��壬 ע����壬 �û�������Ϣ���
	private LoginPanel loginPanel = new LoginPanel(this);
	private RegisterPanel registerPanel = new RegisterPanel(this);
	private MessagePanel messagePanel = new MessagePanel(this);
	
	private int prePanelIndex;
	
	public static int LOGIN_PANEL = 0;
	public static int REGISTER_PANEL = 1;
	public static int MESSAGE_PANEL = 2;
	
	private JPanel[] panels = {loginPanel, registerPanel, messagePanel};
	public void switchPanel(int panelIndex){
		if(panelIndex<0 || panelIndex>panels.length) return;
		this.remove(panels[prePanelIndex]);
		this.add(panels[panelIndex], BorderLayout.CENTER);
		prePanelIndex = panelIndex;
		this.updateUI();
	}
	
	public CommunicationPanel(){
		prePanelIndex = 0;//Ĭ����ʾ��¼���
		//���ļ��ж�ȡ�û���Ϣ���û��� �� ���룩
		this.setLayout(new BorderLayout());
		this.add(panels[0], BorderLayout.CENTER);
	}
}