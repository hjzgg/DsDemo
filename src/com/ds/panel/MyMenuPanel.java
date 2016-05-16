package com.ds.panel;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ds.button.CreateIconButton;
import com.ds.controler.DrawControler;
import com.ds.controler.PanelControler;

public class MyMenuPanel extends JPanel{
	private JButton suspendBtn = CreateIconButton.createBtn("��ʼ", "image/btnIcon/begin.png");
	private JButton resumeBtn = CreateIconButton.createBtn("����", "image/btnIcon/continue.png");
	private JButton stopBtn = CreateIconButton.createBtn("ֹͣ", "image/btnIcon/stop.png");
	private String classAndMethodMsg = null;
	private Thread modelThread = null;
	public void setClassAndMethodMsg(String classAndMethodMsg){
		this.classAndMethodMsg = classAndMethodMsg;
	}
	
	public MyMenuPanel(final JButton backBtn){
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));
		resumeBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		this.add(suspendBtn);
		this.add(resumeBtn);
		this.add(stopBtn);
		
		suspendBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if(modelThread == null) {
					stopBtn.setEnabled(true);
					backBtn.setEnabled(false);
					suspendBtn.setText("��ͣ");
					modelThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {  
								if(classAndMethodMsg == null) return;
								//�ֱ��� �ڲ������Ϣ �� �������õķ���
								String[] classAndMethod = classAndMethodMsg.split(";");
								if(classAndMethod.length == 2){
									String classMsg = classAndMethod[0];
									String methodMsg = classAndMethod[1];
										Class<?> cls = Class.forName(classMsg);  
										if (cls != null) {  
											Constructor<?> constructor = cls.getDeclaredConstructor(new Class[] { DrawControler.class });  
											if (constructor != null) {
												//��ʾ�㷨����
												BufferedReader br = new BufferedReader(new FileReader("./algorithmCode/" + classAndMethodMsg + ".txt"));
												String code = null;
												StringBuilder sb = new StringBuilder();
												while((code = br.readLine()) != null)
													sb.append(code).append("\n");
												br.close();
												PanelControler.drawControler.getCodePanel().setText(sb.toString());
												
												Object instance = constructor.newInstance(PanelControler.drawControler);  
												Method method = cls.getMethod(methodMsg, new Class[]{});
												//ִ����Ӧ���㷨������ʾ
												method.invoke(instance, new Object[]{});
											}  
										}  
								}
							} catch (Exception ex) {  
								ex.printStackTrace();  
							} finally {
								//�㷨ģ�����н���
								JOptionPane.showMessageDialog(null, "�㷨ģ�����!!!", "��ʾ��Ϣ",JOptionPane.WARNING_MESSAGE);
								modelThread = null;
								stopBtn.setEnabled(false);
								suspendBtn.setText("��ʼ");
								suspendBtn.setEnabled(true);
								backBtn.setEnabled(true);
							}
						}
					});
					modelThread.start();
				} else {
					modelThread.suspend();
					//��ͣ���е����߳�
					ThreadGroup group = Thread.currentThread().getThreadGroup();
					ThreadGroup topGroup;
					while (group != null) {
						topGroup = group;
						Thread[] threads = new Thread[topGroup.activeCount()];
						topGroup.enumerate(threads);
						for(Thread thread : threads){
							if(thread.getName().startsWith("childThread"))
								thread.suspend();
						}
						group = group.getParent();
					}
					suspendBtn.setEnabled(false);
					resumeBtn.setEnabled(true);
				}
			}
		});
		
		resumeBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				resumeBtn.setEnabled(false);
				suspendBtn.setEnabled(true);
				//�������е����߳�
				ThreadGroup group = Thread.currentThread().getThreadGroup();
				ThreadGroup topGroup;
				while (group != null) {
					topGroup = group;
					Thread[] threads = new Thread[topGroup.activeCount()];
					topGroup.enumerate(threads);
					for(Thread thread : threads){
						if(thread.getName().startsWith("childThread"))
							thread.resume();
					}
					group = group.getParent();
				}
				modelThread.resume();
			}
		});
		
		stopBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				modelThread.stop();
				//��ֹ�������������߳�
				ThreadGroup group = Thread.currentThread().getThreadGroup();
				ThreadGroup topGroup;
				while (group != null) {
					topGroup = group;
					Thread[] threads = new Thread[topGroup.activeCount()];
					topGroup.enumerate(threads);
					for(Thread thread : threads){
						if(thread.getName().startsWith("childThread"))
							thread.stop();
					}
					group = group.getParent();
				}
				//
				modelThread = null;
				suspendBtn.setText("��ʼ");
				suspendBtn.setEnabled(true);
				resumeBtn.setEnabled(false);
				stopBtn.setEnabled(false);
				backBtn.setEnabled(true);
			}
		});
	}
}
