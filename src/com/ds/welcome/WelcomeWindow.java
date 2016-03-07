package com.ds.welcome;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.ds.controler.PanelControler;
import com.ds.main.MainFrame;

public class WelcomeWindow extends JWindow{
	
	/**
	 * @param fileName 欢迎画面所需要的图片
	 * @param frame	欢迎画面所属的窗体
	 * @param waitTime	欢迎画面的显示时间
	 */
	public WelcomeWindow(String fileName, final JFrame frame, int waitTime){
		super(frame);
		
		JLabel label = new JLabel(new ImageIcon(fileName));
		add(label, BorderLayout.CENTER);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		//将欢迎画面放在屏幕中间
		setLocation((screenSize.width-labelSize.width)/2, (screenSize.height-labelSize.height)/2);
		//鼠标处理事件，如果用户单机了欢迎界面，则欢迎界面关闭
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		final int pause = waitTime;
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
					TimeUnit.MILLISECONDS.sleep(pause);
					SwingUtilities.invokeAndWait(closeRunner);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		
		setVisible(true);
		Thread welcomeThread = new Thread(waitRunner, "welcomeThread");
		welcomeThread.start();
	}
}
