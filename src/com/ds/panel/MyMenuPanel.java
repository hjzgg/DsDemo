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
	private JButton suspendBtn = CreateIconButton.createBtn("开始", "image/btnIcon/begin.png");
	private JButton resumeBtn = CreateIconButton.createBtn("继续", "image/btnIcon/continue.png");
	private JButton stopBtn = CreateIconButton.createBtn("停止", "image/btnIcon/stop.png");
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
					suspendBtn.setText("暂停");
					modelThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {  
								if(classAndMethodMsg == null) return;
								//分别获得 内部类的信息 和 即将调用的方法
								String[] classAndMethod = classAndMethodMsg.split(";");
								if(classAndMethod.length == 2){
									String classMsg = classAndMethod[0];
									String methodMsg = classAndMethod[1];
										Class<?> cls = Class.forName(classMsg);  
										if (cls != null) {  
											Constructor<?> constructor = cls.getDeclaredConstructor(new Class[] { DrawControler.class });  
											if (constructor != null) {
												//显示算法代码
												BufferedReader br = new BufferedReader(new FileReader("./algorithmCode/" + classAndMethodMsg + ".txt"));
												String code = null;
												StringBuilder sb = new StringBuilder();
												while((code = br.readLine()) != null)
													sb.append(code).append("\n");
												br.close();
												PanelControler.drawControler.getCodePanel().setText(sb.toString());
												
												Object instance = constructor.newInstance(PanelControler.drawControler);  
												Method method = cls.getMethod(methodMsg, new Class[]{});
												//执行相应的算法进行演示
												method.invoke(instance, new Object[]{});
											}  
										}  
								}
							} catch (Exception ex) {  
								ex.printStackTrace();  
							} finally {
								//算法模拟运行结束
								JOptionPane.showMessageDialog(null, "算法模拟结束!!!", "提示消息",JOptionPane.WARNING_MESSAGE);
								modelThread = null;
								stopBtn.setEnabled(false);
								suspendBtn.setText("开始");
								suspendBtn.setEnabled(true);
								backBtn.setEnabled(true);
							}
						}
					});
					modelThread.start();
				} else {
					modelThread.suspend();
					//暂停所有的子线程
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
				//继续所有的子线程
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
				//终止所有启动的子线程
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
				suspendBtn.setText("开始");
				suspendBtn.setEnabled(true);
				resumeBtn.setEnabled(false);
				stopBtn.setEnabled(false);
				backBtn.setEnabled(true);
			}
		});
	}
}
