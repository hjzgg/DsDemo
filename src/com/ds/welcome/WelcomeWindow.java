package com.ds.welcome;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.ds.controler.PanelControler;

public class WelcomeWindow extends JWindow{
	private static final int IMAGE_COUNT = 12;
	private String[] processBarMsg = {"ģ�⣺����ģ��ϵͳ��ѡ��ģ��", "ģ�⣺���嵽һ���㷨ģ��", "ģ�⣺��ʼģ��", "ģ�⣺��������", "ģ�⣺ģ������",
			"��ϰ��������ϰϵͳ", "��ϰ����д���룬�ύ����", "��������¼", "������ע��", "���������뽻������������Ϣ", "�����������ʼ�", "�������յ��ʼ�"};
	//��ʾ�ڽ�������
	private String[] imageTip = {};
	private String[] welcomeIconPath;
	private ImagePanel imagePanel;
	private JProgressBar progressBar;
	private AudioClip[] sounds;
	/**
	 * @param fileName ��ӭ��������Ҫ��ͼƬ
	 * @param frame	��ӭ���������Ĵ���
	 * @param waitTime	��ӭ�������ʾʱ��
	 * @throws MalformedURLException 
	 */
	public WelcomeWindow(final JFrame frame) throws MalformedURLException{
		super(frame);
		welcomeIconPath = new String[IMAGE_COUNT];
		for(int i=0; i < IMAGE_COUNT; ++i)
			welcomeIconPath[i] = "image/welcome/p" + i + ".png";
		//������Ƶ
		//URL dir = new URL(System.getProperty("user.dir"));
		File file = new File("user.dir");
		URL dir = file.toURL();
		sounds = new AudioClip[2];
		sounds[0] = Applet.newAudioClip(new URL(dir, "sound/sound1.au"));
		sounds[1] = Applet.newAudioClip(new URL(dir, "sound/sound2.au"));
		
		imagePanel = new ImagePanel();
		imagePanel.setImagePath(welcomeIconPath[0]);
		imagePanel.setPreferredSize(new Dimension(800, 500));
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
					for(int i=0; i<IMAGE_COUNT; ++i){
						sounds[i%2].play();
						imagePanel.setImagePath(welcomeIconPath[i]);
						progressBar.setValue(progressBar.getValue()+progressStep);
						progressBar.setString(processBarMsg[i]);
						WelcomeWindow.this.repaint();
						TimeUnit.MILLISECONDS.sleep(3000);
					}
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
