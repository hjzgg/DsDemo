package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ds.button.CreateIconButton;
import com.ds.button.FunctionButton;
import com.ds.controler.DrawControler;
import com.ds.controler.PanelControler;
import com.ds.controler.PanelControler.FunctionMsgPanel;
import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

public class FunctionPanel extends JPanel{
	public FunctionMsgPanel functionMsgPanel = null;
	private JButton backBtn;
	private int backStep = 0;//是否需要添加返回按钮
	private BackgroundPanel panelNorth;
	private BackgroundPanel panelEast;
	private BackgroundPanel panelWest;
	private BackgroundPanel panelSouth;
	
	private MyMenuPanel menuPanel = null;
	
	private void adjustPanelLayout(boolean isShowing){
		if(isShowing) {
			panelEast.setPreferredSize(new Dimension(0, 0));
			panelWest.setPreferredSize(new Dimension(0, 0));
			panelSouth.setPreferredSize(new Dimension(0, 0));
			panelNorth.setPreferredSize(new Dimension(0, 50));
			menuPanel.setVisible(true);
		} else {
			panelEast.setPreferredSize(new Dimension(100, 0));
			panelWest.setPreferredSize(new Dimension(100, 0));
			panelSouth.setPreferredSize(new Dimension(0, 100));
			panelNorth.setPreferredSize(new Dimension(0, 100));
			menuPanel.setVisible(false);
		}
	}
	
	private class ShowMsgListener extends MouseAdapter{
		private ShowDetailMsgFrame frame;
		private FunctionButton btn;
		//点击之后调到下一个面板去
		@Override
		public void mouseClicked(MouseEvent e) {
			FunctionMsgPanel preFunctionMsgPanel = functionMsgPanel;
			FunctionPanel.this.remove(functionMsgPanel);
			functionMsgPanel = (FunctionMsgPanel) PanelControler.nextPanel.get(btn.getText());
			
			FunctionPanel.this.add(functionMsgPanel, BorderLayout.CENTER);
			FunctionPanel.this.updateUI();
			//返回的步数加1
			++backStep;
			//显示后退按钮
			backBtn.setVisible(true);
			
			//功能页面有多个， 展示页面只有一个
			if(functionMsgPanel.getLayout() instanceof GridLayout) {//功能页面
				//只是初始化一遍 面板上的 按钮
				if(!functionMsgPanel.isInitBtns()){
					for(FunctionButton btn : functionMsgPanel.btns)
						btn.addMouseListener(new ShowMsgListener(new ShowDetailMsgFrame(btn.getText()+"功能预览"), btn));
					functionMsgPanel.initBtns();
				}
			} else { //展示页面
				//java 对内部类进行反射，并执行相应的算法模拟程序。
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
		
		panelWest = new BackgroundPanel("image/super_bg/left.png");
		add(panelWest, BorderLayout.WEST);
		
		panelNorth = new BackgroundPanel("image/super_bg/top.png");
		GridBagLayout northPaneLayout = new GridBagLayout();
		panelNorth.setLayout(northPaneLayout);
		backBtn = CreateIconButton.createBtn("返回", "image/btnIcon/back.png");
		backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//清除面板上的图案
				PanelControler.drawControler.getDrawModel().getShapeList().clear();
				PanelControler.drawControler.getCodePanel().setText("");
				PanelControler.drawControler.getMsgPanel().setText("");
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
		//设置菜单面板透明，以便显示背景图片
		menuPanel.setOpaque(false);
		menuPanel.setVisible(false);
		panelNorth.add(menuPanel);
		JPanel backBtnPanel = new JPanel();
		//设置返回按钮的面板背景图片透明
		backBtnPanel.setOpaque(false);
		panelNorth.add(backBtnPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		backBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		backBtnPanel.add(backBtn);
		northPaneLayout.setConstraints(backBtnPanel, gbc);
		gbc.weightx = 4;
		northPaneLayout.setConstraints(menuPanel, gbc);
		
		add(panelNorth, BorderLayout.NORTH);
		
		panelSouth = new BackgroundPanel("image/super_bg/bottom.png");
		add(panelSouth, BorderLayout.SOUTH);
		
		panelEast = new BackgroundPanel("image/super_bg/right.png");
		add(panelEast, BorderLayout.EAST);
		
		adjustPanelLayout(false);
		
		functionMsgPanel = PanelControler.SimulatePanel.FirstPanel.firstPanel;
		add(functionMsgPanel, BorderLayout.CENTER);
		for(FunctionButton btn : functionMsgPanel.btns)
			btn.addMouseListener(new ShowMsgListener(new ShowDetailMsgFrame(btn.getText()+"功能预览"), btn));
	}
}
