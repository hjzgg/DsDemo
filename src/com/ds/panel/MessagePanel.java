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
		//左侧为 图形菜单， 右侧是交流信息
		JSplitPane hxSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hxSplitePane.setContinuousLayout(true);
		hxSplitePane.setOneTouchExpandable(true);
		hxSplitePane.setDividerSize(10);
		hxSplitePane.setResizeWeight(0.1);
		 
		//树形菜单
		JTree menuTree = TreeMenuControler.getTreeMenu();
		hxSplitePane.setLeftComponent(new JScrollPane(menuTree));
		hxSplitePane.setRightComponent(chatPanel);
		//添加选择监听器
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				JTree treeRoot = (JTree)event.getSource(); 
				DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)treeRoot.getLastSelectedPathComponent();
				//如果是叶子节点, 请求服务器，返回相应的交流信息
				if(selectionNode != null && selectionNode.getChildCount() == 0) {
					try{
						String problemName = new TreePath(selectionNode.getPath()).toString();
						//保存当前选择的是哪一个题目
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
