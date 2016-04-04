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
		//第一级菜单
		"数据结构,#,",
		//第二级菜单
		"顺序表,链表,栈,队列,串,广义表,二叉树,图,排序,并查集,十字链表,高级树,#,",
		//第三级菜单
		"数组显示,数组创建,数组删除,数组插入,#," +
		"链表显示,链表创建,链表删除,链表插入,链表合并(生成新链表),链表合并(就地合并),#," +
		"栈演示,表达式求值,#," +
		"队列演示,#," +
		"串匹配(BF),串匹配(KMP_NEXT),串匹配(KMP_NEXTVAL),#," +
		"广义表显示,#," +
		"二叉树显示,先序创建,中序创建,后序创建,先序遍历,中序遍历,后序遍历,二叉树转化为森林,哈夫曼树编码,#," +
		"图的显示,广度遍历图,深度遍历图,迪杰斯特拉算法,Prim算法,Kruskal算法,Floyd算法,#," +
		"选择排序,简单插入排序,二分插入排序,冒泡排序,快速排序,归并排序,推排序,希尔排序,基数排序,#," +
		"并查集演示,#," +
		"十字链表显示,#," +
		"线段树显示,树状数组显示,森林显示,森林转换二叉树,#"
	};
	
	private static JTree buildTreeMenu(){
		//采用bfs生成 三级菜单,或者更多级的菜单
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
	    //创建树对象 
		JTree tree = new JTree(treeModel); 
		//默认所有的路径全部关闭
		tree.collapsePath(new TreePath(treeModel.getPathToRoot(root)));
		//设置Tree的选择为一次只能选择一个节点 
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(20); 
		//创建节点绘制对象 
		DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
	 	 //设置字体 
		cellRenderer.setFont(new Font("Serif",Font.PLAIN,14)); 
		cellRenderer.setBackgroundNonSelectionColor(Color.white); 
		cellRenderer.setBackgroundSelectionColor(Color.yellow); 
		cellRenderer.setBorderSelectionColor(Color.red); 
	  	//设置选或不选时，文字的变化颜色 
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
		} else { //叶子节点
			TreePath path = new TreePath(treeModel.getPathToRoot(root));
			try {
				File file = new File("./tests/" + path.toString() + ".txt");
				if(file.createNewFile()){}//如果文件不存在并且创建成功
				file = new File("./inputs/" + path.toString() + ".txt");
				if(file.createNewFile()){}//如果文件不存在并且创建成功
				file = new File("./outs/" + path.toString() + ".txt");
				if(file.createNewFile()){}//如果文件不存在并且创建成功
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		JTree tree = getTreeMenu();
		treeModel = (DefaultTreeModel)tree.getModel();
		//建立文件
		//buildTitleFiles((TreeNode)tree.getModel().getRoot());
	}
}
