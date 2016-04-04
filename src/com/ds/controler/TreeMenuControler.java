package com.ds.controler;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class TreeMenuControler {
	
	private static final String[] treeMenuContent = {
		//��һ���˵�
		"���ݽṹ,#,",
		//�ڶ����˵�
		"˳���,����,ջ,����,��,�����,������,ͼ,����,���鼯,ʮ������,�߼���,#,",
		//�������˵�
		"������ʾ,���鴴��,����ɾ��,�������,#," +
		"������ʾ,������,����ɾ��,�������,����ϲ�(����������),����ϲ�(�͵غϲ�),#," +
		"ջ��ʾ,���ʽ��ֵ,#," +
		"������ʾ,#," +
		"��ƥ��(BF),��ƥ��(KMP_NEXT),��ƥ��(KMP_NEXTVAL),#," +
		"�������ʾ,#," +
		"��������ʾ,���򴴽�,���򴴽�,���򴴽�,�������,�������,�������,������ת��Ϊɭ��,������������,#," +
		"ͼ����ʾ,��ȱ���ͼ,��ȱ���ͼ,�Ͻ�˹�����㷨,Prim�㷨,Kruskal�㷨,Floyd�㷨,#," +
		"ѡ������,�򵥲�������,���ֲ�������,ð������,��������,�鲢����,������,ϣ������,��������,#," +
		"���鼯��ʾ,#," +
		"ʮ��������ʾ,#," +
		"�߶�����ʾ,��״������ʾ,ɭ����ʾ,ɭ��ת��������,#"
	};
	
	private static JTree buildTreeMenu(){
		//����bfs���� �����˵�,���߸��༶�Ĳ˵�
		String menuContents = "";
		for(int i=0; i<treeMenuContent.length; ++i){
			menuContents += treeMenuContent[i];
		}
		String[] menus = menuContents.split(",");
		if(menus.length <= 0 || "#".equals(menus[0])) return null;
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(menus[0]); 
		DefaultTreeModel treeModel = new DefaultTreeModel(root); 
		Queue<DefaultMutableTreeNode> queue = new LinkedList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode parent = null;
		queue.add(root);
		int index = 0;
		++index;
		while(index < menus.length){
			if("#".equals(menus[index])){
				parent = queue.poll();
			} else {
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(menus[index]);
				queue.add(newChild);
				treeModel.insertNodeInto(newChild, parent, parent.getChildCount());
			}
			++index;
		}
	    //���������� 
		JTree tree = new JTree(treeModel); 
		//Ĭ�����е�·��ȫ���ر�
		tree.collapsePath(new TreePath(treeModel.getPathToRoot(root)));
		//����Tree��ѡ��Ϊһ��ֻ��ѡ��һ���ڵ� 
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(20); 
		//�����ڵ���ƶ��� 
		DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
	 	 //�������� 
		cellRenderer.setFont(new Font("Serif",Font.PLAIN,14)); 
		cellRenderer.setBackgroundNonSelectionColor(Color.white); 
		cellRenderer.setBackgroundSelectionColor(Color.yellow); 
		cellRenderer.setBorderSelectionColor(Color.red); 
	  	//����ѡ��ѡʱ�����ֵı仯��ɫ 
		cellRenderer.setTextNonSelectionColor(Color.black); 
		cellRenderer.setTextSelectionColor(Color.blue); 
		return tree;
	}
	
	public static JTree getTreeMenu(){
		JTree tree = buildTreeMenu();
		return tree;
	}
	
	private static DefaultTreeModel treeModel;
	@SuppressWarnings("unused")
	private static void buildTitleFiles(TreeNode root){
		if(root.getChildCount() > 0){
			for(Enumeration<?> e = root.children(); e.hasMoreElements(); ){
				buildTitleFiles((TreeNode)e.nextElement());
			}
		} else { //Ҷ�ӽڵ�
			TreePath path = new TreePath(treeModel.getPathToRoot(root));
			try {
				File file = new File("./tests/" + path.toString() + ".txt");
				if(file.createNewFile()){}//����ļ������ڲ��Ҵ����ɹ�
				file = new File("./inputs/" + path.toString() + ".txt");
				if(file.createNewFile()){}//����ļ������ڲ��Ҵ����ɹ�
				file = new File("./outs/" + path.toString() + ".txt");
				if(file.createNewFile()){}//����ļ������ڲ��Ҵ����ɹ�
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		JTree tree = getTreeMenu();
		treeModel = (DefaultTreeModel)tree.getModel();
		//�����ļ�
		//buildTitleFiles((TreeNode)tree.getModel().getRoot());
	}
}
