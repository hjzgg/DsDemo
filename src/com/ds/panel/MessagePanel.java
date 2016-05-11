package com.ds.panel;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sf.json.JSONObject;

import com.ds.controler.TreeMenuControler;
import com.ds.page.MyMessage;
import com.ds.page.MyPage;
import com.ds.page.MyPagePanel;
import com.ds.panel.chat.ChatPanel;
import com.ds.tools.JavaRequest;

public class MessagePanel extends JPanel {
	private CommunicationPanel parentPanel;
	private ChatPanel chatPanel;
	public MessagePanel(CommunicationPanel parentPanelx){
		this.parentPanel = parentPanelx;
		chatPanel = new ChatPanel(parentPanel);
		//���Ϊ ͼ�β˵��� �Ҳ��ǽ�����Ϣ
		JSplitPane hxSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hxSplitePane.setContinuousLayout(true);
		hxSplitePane.setOneTouchExpandable(true);
		hxSplitePane.setDividerSize(10);
		hxSplitePane.setResizeWeight(0.1);
		 
		//���β˵�
		JTree menuTree = TreeMenuControler.getTreeMenu();
		hxSplitePane.setLeftComponent(new JScrollPane(menuTree));
		hxSplitePane.setRightComponent(chatPanel);
		//���ѡ�������
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				JTree treeRoot = (JTree)event.getSource(); 
				DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)treeRoot.getLastSelectedPathComponent();
				//�����Ҷ�ӽڵ�, �����������������Ӧ�Ľ�����Ϣ
				if(selectionNode != null && selectionNode.getChildCount() == 0) {
					try{
						String problemName = new TreePath(selectionNode.getPath()).toString();
						//���浱ǰѡ�������һ����Ŀ
						JavaRequest.problemName = problemName;
						chatPanel.getPagePanel().requestPage(0);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		this.setLayout(new BorderLayout());
		this.add(hxSplitePane, BorderLayout.CENTER);
	}
}
