package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ds.button.FunctionButton;
import com.ds.controler.DrawControler;
import com.ds.controler.PanelControler;
import com.ds.controler.PanelControler.FunctionMsgPanel;
import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

public class FunctionPanel extends JPanel{
	public FunctionMsgPanel functionMsgPanel = null;
	private JButton backBtn;
	private int backStep = 0;//�Ƿ���Ҫ��ӷ��ذ�ť
	private JPanel panelNorth;
	private JPanel panelEast;
	private JPanel panelWest;
	private JPanel panelSouth;
	
	private MyMenuPanel menuPanel = null;
	
	private void adjustPanelLayout(boolean isShowing){
		if(isShowing) {
			panelEast.setPreferredSize(new Dimension(0, 0));
			panelWest.setPreferredSize(new Dimension(0, 0));
			panelSouth.setPreferredSize(new Dimension(0, 0));
			panelNorth.setPreferredSize(new Dimension(0, 50));
			
			panelNorth.add(menuPanel, 0);
		} else {
			panelEast.setPreferredSize(new Dimension(100, 0));
			panelWest.setPreferredSize(new Dimension(100, 0));
			panelSouth.setPreferredSize(new Dimension(0, 100));
			panelNorth.setPreferredSize(new Dimension(0, 100));
			
			panelNorth.remove(menuPanel);
		}
	}
	
	private class ShowMsgListener extends MouseAdapter{
		private ShowDetailMsgFrame frame;
		private FunctionButton btn;
		//���֮�������һ�����ȥ
		@Override
		public void mouseClicked(MouseEvent e) {
			FunctionMsgPanel preFunctionMsgPanel = functionMsgPanel;
			FunctionPanel.this.remove(functionMsgPanel);
			functionMsgPanel = (FunctionMsgPanel) PanelControler.nextPanel.get(btn.getText());
			
			FunctionPanel.this.add(functionMsgPanel, BorderLayout.CENTER);
			FunctionPanel.this.updateUI();
			//���صĲ�����1
			++backStep;
			//��ʾ���˰�ť
			backBtn.setVisible(true);
			
			//����ҳ���ж���� չʾҳ��ֻ��һ��
			if(functionMsgPanel.getLayout() instanceof GridLayout) {//����ҳ��
				//ֻ�ǳ�ʼ��һ�� ����ϵ� ��ť
				if(!functionMsgPanel.isInitBtns()){
					for(FunctionButton btn : functionMsgPanel.btns)
						btn.addMouseListener(new ShowMsgListener(new ShowDetailMsgFrame(btn.getText()+"����Ԥ��"), btn));
					functionMsgPanel.initBtns();
				}
			} else { //չʾҳ��
				//java ���ڲ�����з��䣬��ִ����Ӧ���㷨ģ�����
				adjustPanelLayout(true);
				String classAndMethodMsg = PanelControler.SimulatePanel.ThirdPanel.btnMsgMapClassMsg.get(btn.getText());
				menuPanel.setClassAndMethodMsg(classAndMethodMsg);
			}
			PanelControler.prePanel.put(functionMsgPanel, preFunctionMsgPanel);
			if(frame.isVisible())
				frame.setVisible(false);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			frame.setBounds(e.getXOnScreen(), e.getYOnScreen(), frame.getWidth(), frame.getHeight());
			frame.setVisible(true);
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if(frame.isVisible())
						frame.setVisible(false);
				}
			}, 2000);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			frame.setVisible(false);
		}

		public ShowMsgListener(ShowDetailMsgFrame frame, FunctionButton btn) {
			super();
			this.frame = frame;
			this.btn = btn;
		}
	}
	
	public FunctionPanel() {
		setLayout(new BorderLayout(0, 0));
		
		panelWest = new JPanel();
		add(panelWest, BorderLayout.WEST);
		
		panelNorth = new JPanel();
		GridBagLayout northPaneLayout = new GridBagLayout();
		panelNorth.setLayout(northPaneLayout);
		backBtn = new JButton("����");
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//�������ϵ�ͼ��
				PanelControler.drawControler.getDrawModel().getShapeList().clear();

				--backStep;
				if(backStep == 0) {
					backBtn.setVisible(false);
				}
				adjustPanelLayout(false);
				FunctionPanel.this.remove(functionMsgPanel);
				functionMsgPanel = PanelControler.prePanel.get(functionMsgPanel);
				FunctionPanel.this.add(functionMsgPanel, BorderLayout.CENTER);
				FunctionPanel.this.updateUI();
			}
		});
		backBtn.setVisible(false);
		
		menuPanel = new MyMenuPanel(backBtn);
		JPanel backBtnPanel = new JPanel();
		
		panelNorth.add(backBtnPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		backBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 15));
		backBtnPanel.add(backBtn);
		northPaneLayout.setConstraints(backBtnPanel, gbc);
		gbc.weightx = 4;
		northPaneLayout.setConstraints(menuPanel, gbc);
		
		add(panelNorth, BorderLayout.NORTH);
		
		panelSouth = new JPanel();
		add(panelSouth, BorderLayout.SOUTH);
		
		panelEast = new JPanel();
		add(panelEast, BorderLayout.EAST);
		
		adjustPanelLayout(false);
		
		functionMsgPanel = PanelControler.SimulatePanel.FirstPanel.firstPanel;
		add(functionMsgPanel, BorderLayout.CENTER);
		for(FunctionButton btn : functionMsgPanel.btns)
			btn.addMouseListener(new ShowMsgListener(new ShowDetailMsgFrame(btn.getText()+"����Ԥ��"), btn));
	}
}
