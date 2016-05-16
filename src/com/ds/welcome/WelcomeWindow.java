package com.ds.welcome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.ds.controler.PanelControler;

public class WelcomeWindow extends JWindow{
	private static final int IMAGE_COUNT = 8;
	//显示在进度条上
	private String[] imageTip = {};
	private String[] welcomeIconPath;
	private ImagePanel imagePanel;
	private JProgressBar progressBar;
	/**
	 * @param fileName 欢迎画面所需要的图片
	 * @param frame	欢迎画面所属的窗体
	 * @param waitTime	欢迎画面的显示时间
	 */
	public WelcomeWindow(final JFrame frame){
		super(frame);
		welcomeIconPath = new String[IMAGE_COUNT];
		for(int i=0; i < IMAGE_COUNT; ++i)
			welcomeIconPath[i] = "image/welcome/p" + i + ".jpg";
		imagePanel = new ImagePanel();
		imagePanel.setImagePath(welcomeIconPath[0]);
		imagePanel.setPreferredSize(new Dimension(400, 300));
	    progressBar = new JProgressBar();  
        progressBar.setStringPainted(true);  //设置进度条呈现进度字符串,默认为false  
        progressBar.setBorderPainted(false); //不绘制边框,默认为true  
        progressBar.setPreferredSize(new Dimension(0, 20)); //设置首选大小
        add(imagePanel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = imagePanel.getPreferredSize();
		//将欢迎画面放在屏幕中间
		setLocation((screenSize.width-labelSize.width)/2, (screenSize.height-labelSize.height)/2);
		
		//关闭欢迎画面的线程
		final Runnable closeRunner = new Runnable() {
			@Override
			public void run() {
				setVisible(false);
				dispose();
				//设置frame的大小, 填充屏幕的大小
				Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();  
		        Insets scrInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()); 
		        frame.setBounds(scrInsets.left, scrInsets.top, scrSize.width-scrInsets.left-scrInsets.right, scrSize.height-scrInsets.top-scrInsets.bottom);
				frame.setVisible(true);
				
				//当面板的大小发生变化的时候，同样保持 分割面板的分割线的位置不变
				frame.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						super.componentResized(e);
						//设置分割面板的分割线的位置
						if(PanelControler.splitPaneVer != null)
							PanelControler.splitPaneVer.setResizeWeight(PanelControler.splitPaneVer.getResizeWeight());
						if(PanelControler.splitPaneHor != null)
							PanelControler.splitPaneHor.setDividerLocation(PanelControler.splitPaneHor.getResizeWeight());
					}
				});
			}
		};
		
		//等待关闭欢迎画面的线程
		Runnable waitRunner = new Runnable() {
			@Override
			public void run() {
				try{
					progressBar.setValue(0);
					int progressStep = 100/IMAGE_COUNT;
					//执行图片切换
//					for(int i=0; i<IMAGE_COUNT; ++i){
//						imagePanel.setImagePath(welcomeIconPath[i]);
//						progressBar.setValue(progressBar.getValue()+progressStep);
//						WelcomeWindow.this.repaint();
//						TimeUnit.MILLISECONDS.sleep(3000);
//					}
					progressBar.setValue(100);
					SwingUtilities.invokeAndWait(closeRunner);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		setVisible(true);
		Thread welcomeThread = new Thread(waitRunner, "welcomeThread");
		welcomeThread.start();
	}
}
