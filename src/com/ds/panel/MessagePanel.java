package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ds.controler.TreeMenuControler;

public class MessagePanel extends JPanel {
	private CommunicationPanel parentPanel;
	private JTextArea messageArea;
	private JTextPane messagePanel;
	public MessagePanel(CommunicationPanel parentPanel){
		this.parentPanel = parentPanel;
		//���Ϊ ͼ�β˵��� �Ҳ��ǽ�����Ϣ
		JSplitPane hxSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hxSplitePane.setContinuousLayout(true);
		hxSplitePane.setOneTouchExpandable(true);
		hxSplitePane.setDividerSize(10);
		hxSplitePane.setResizeWeight(0.1);
		JSplitPane vxSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vxSplitPane.setContinuousLayout(true);
		vxSplitPane.setOneTouchExpandable(true);
		vxSplitPane.setDividerSize(10);
		vxSplitPane.setResizeWeight(0.8);
		//������Ϣ
		messagePanel = new JTextPane();
		//������Ϣ
		messageArea = new JTextArea();
		//ctrl + enter������Ϣ
		messageArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.isControlDown()){
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						
					}
				}
			}
		});
		
		messagePanel.setEditable(false);
		JScrollPane messagePanelScroll = new JScrollPane(messagePanel);
		JScrollPane messageAreaScroll = new JScrollPane(messageArea);
		
		vxSplitPane.setTopComponent(messagePanelScroll);
		vxSplitPane.setBottomComponent(messageAreaScroll);
		//���β˵�
		JTree menuTree = TreeMenuControler.getTreeMenu();
		hxSplitePane.setLeftComponent(new JScrollPane(menuTree));
		hxSplitePane.setRightComponent(vxSplitPane);
		//���ѡ�������
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				JTree treeRoot = (JTree)event.getSource(); 
				DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)treeRoot.getLastSelectedPathComponent();
				//�����Ҷ�ӽڵ�, �����������������Ӧ�Ľ�����Ϣ
				if(selectionNode.getChildCount() == 0){
					
				}
			}
		});
		
		this.setLayout(new BorderLayout());
		this.add(hxSplitePane, BorderLayout.CENTER);
	}
}
