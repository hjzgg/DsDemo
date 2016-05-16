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
	//��ʾ�ڽ�������
	private String[] imageTip = {};
	private String[] welcomeIconPath;
	private ImagePanel imagePanel;
	private JProgressBar progressBar;
	/**
	 * @param fileName ��ӭ��������Ҫ��ͼƬ
	 * @param frame	��ӭ���������Ĵ���
	 * @param waitTime	��ӭ�������ʾʱ��
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
        progressBar.setStringPainted(true);  //���ý��������ֽ����ַ���,Ĭ��Ϊfalse  
        progressBar.setBorderPainted(false); //�����Ʊ߿�,Ĭ��Ϊtrue  
        progressBar.setPreferredSize(new Dimension(0, 20)); //������ѡ��С
        add(imagePanel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = imagePanel.getPreferredSize();
		//����ӭ���������Ļ�м�
		setLocation((screenSize.width-labelSize.width)/2, (screenSize.height-labelSize.height)/2);
		
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
					progressBar.setValue(0);
					int progressStep = 100/IMAGE_COUNT;
					//ִ��ͼƬ�л�
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
