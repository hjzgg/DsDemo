package com.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class Tree extends JFrame implements TreeSelectionListener 
{ 
 private JLabel label; 
  
 public Tree() 
 { 
  super("树形菜单"); setSize(400,400); 
  Container container = getContentPane(); 
  
  //创建根节点和子节点 
  DefaultMutableTreeNode root = new DefaultMutableTreeNode("文本编辑器"); 
  DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("文件"); 
  DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("编辑"); 
  //利用根节点创建TreeModel 
  DefaultTreeModel treeModel = new DefaultTreeModel(root); 
  //插入子节点node1,node2 
  treeModel.insertNodeInto(node1,root,root.getChildCount()); 
  treeModel.insertNodeInto(node2,root,root.getChildCount()); 
  
  //创建节点node1的子节点并插入 
  DefaultMutableTreeNode leafnode = new DefaultMutableTreeNode("打开"); 
  treeModel.insertNodeInto(leafnode,node1,node1.getChildCount()); 
  leafnode = new DefaultMutableTreeNode("保存"); 
  treeModel.insertNodeInto(leafnode,node1,node1.getChildCount()); 
  leafnode = new DefaultMutableTreeNode("另存为"); 
  treeModel.insertNodeInto(leafnode,node1,node1.getChildCount()); 
  leafnode = new DefaultMutableTreeNode("关闭"); 
  treeModel.insertNodeInto(leafnode,node1,node1.getChildCount()); 
  
  //创建节点node2的子节点并插入 
  leafnode = new DefaultMutableTreeNode("剪切"); 
  treeModel.insertNodeInto(leafnode,node2,node2.getChildCount()); 
  leafnode = new DefaultMutableTreeNode("复制"); 
  treeModel.insertNodeInto(leafnode,node2,node2.getChildCount()); 
  leafnode = new DefaultMutableTreeNode("粘贴"); 
  treeModel.insertNodeInto(leafnode,node2,node2.getChildCount()); 
  
  //创建树对象 
  JTree tree = new JTree(treeModel); 
  //设置Tree的选择为一次只能选择一个节点 
  tree.getSelectionModel().setSelectionMode( 
       TreeSelectionModel.SINGLE_TREE_SELECTION); 
  //注册监听器 
  tree.addTreeSelectionListener(this); 
  
  tree.setRowHeight(20); 
  
  //创建节点绘制对象 
  DefaultTreeCellRenderer cellRenderer = 
       (DefaultTreeCellRenderer)tree.getCellRenderer(); 
  
  //设置字体 
  cellRenderer.setFont(new Font("Serif",Font.PLAIN,14)); 
  cellRenderer.setBackgroundNonSelectionColor(Color.white); 
  cellRenderer.setBackgroundSelectionColor(Color.yellow); 
  cellRenderer.setBorderSelectionColor(Color.red); 
  
  //设置选或不选时，文字的变化颜色 
  cellRenderer.setTextNonSelectionColor(Color.black); 
  cellRenderer.setTextSelectionColor(Color.blue); 
    
  //把树对象添加到内容面板 
  container.add(new JScrollPane(tree)); 
  
  //创建标签 
  label = new JLabel("你当前选择的节点为：",JLabel.CENTER); 
  label.setFont(new Font("Serif",Font.PLAIN,14)); 
  container.add(label,BorderLayout.SOUTH); 
  
  setVisible(true); //设置可见 
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置窗口关闭动作 
 } 
  
 //处理TreeSelectionEvent事件 
 public void valueChanged(TreeSelectionEvent event) 
 { 
	  JTree tree = (JTree)event.getSource(); 
	  System.out.println(tree.getSelectionPath().getParentPath());
	  //获取目前选取的节点 
	  DefaultMutableTreeNode selectionNode = 
	   (DefaultMutableTreeNode)tree.getLastSelectedPathComponent(); 
	  String nodeName = selectionNode.toString(); 
	  label.setText("你当前选取的节点为： "+nodeName); 
 } 
 
 
  
 public static void main(String args[]) 
 { 
  Tree d = new Tree(); 
 } 
  
}
