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
	 * @param fileName ��ӭ��������Ҫ��ͼƬ
	 * @param frame	��ӭ���������Ĵ���
	 * @param waitTime	��ӭ�������ʾʱ��
	 */
	public WelcomeWindow(String fileName, final JFrame frame, int waitTime){
		super(frame);
		
		JLabel label = new JLabel(new ImageIcon(fileName));
		add(label, BorderLayout.CENTER);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = label.getPreferredSize();
		//����ӭ���������Ļ�м�
		setLocation((screenSize.width-labelSize.width)/2, (screenSize.height-labelSize.height)/2);
		//��괦���¼�������û������˻�ӭ���棬��ӭ����ر�
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		final int pause = waitTime;
		//�رջ�ӭ������߳�
		final Runnable closeRunner = new Runnable() {
			@Override
			public void run() {
				setVisible(false);
				dispose();
				//����frame�Ĵ�С, �����Ļ�Ĵ�С
				Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();  
		        Insets scrInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()); 
		        frame.setBounds(scrInsets.left, scrInsets.top, scrSize.width-scrInsets.left-scrInsets.right, scrSize.height-scrInsets.top-scrInsets.bottom);
				frame.setVisible(true);
				
				//�����Ĵ�С�����仯��ʱ��ͬ������ �ָ����ķָ��ߵ�λ�ò���
				frame.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						super.componentResized(e);
						//���÷ָ����ķָ��ߵ�λ��
						if(PanelControler.splitPaneVer != null)
							PanelControler.splitPaneVer.setResizeWeight(PanelControler.splitPaneVer.getResizeWeight());
						if(PanelControler.splitPaneHor != null)
							PanelControler.splitPaneHor.setDividerLocation(PanelControler.splitPaneHor.getResizeWeight());
					}
				});
				
				
			}
		};
		
		//�ȴ��رջ�ӭ������߳�
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
