package com.ds.controler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.net.MalformedURLException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import net.sf.json.JSONObject;

import com.ds.constants.MsgPanelConstants;
import com.ds.dialog.MyDialog;
import com.ds.main.MainFrame;
import com.ds.model.ArrayModel;
import com.ds.model.BTreeModel;
import com.ds.model.BinaryIndexedTreeModel;
import com.ds.model.BinaryInsertSortModel;
import com.ds.model.BubbleSortModel;
import com.ds.model.CrossListModel;
import com.ds.model.DrawModel;
import com.ds.model.ForestModel;
import com.ds.model.FormulaModel;
import com.ds.model.GListModel;
import com.ds.model.GraphicModel;
import com.ds.model.HeapSortModel;
import com.ds.model.HuffmanTreeModel;
import com.ds.model.ListModel;
import com.ds.model.MazeModel;
import com.ds.model.MergeSortModel;
import com.ds.model.QueueModel;
import com.ds.model.QuickSortModel;
import com.ds.model.RadixSortModel;
import com.ds.model.SampleInsertSortModel;
import com.ds.model.SegmentTreeModel;
import com.ds.model.SelectiveSortModel;
import com.ds.model.ShellInsertSortModel;
import com.ds.model.StackModel;
import com.ds.model.StringModel;
import com.ds.model.UnionFindSetModel;
import com.ds.panel.DrawPanel;
import com.ds.welcome.WelcomeWindow;

public class DrawControler {
	private DrawPanel drawPanel;
	private JTextPane codePanel;
	private JTextPane msgPanel;
	private DrawModel model;
	
	private JScrollPane drawScrollPane;
	private JPanel mainCodePanel;
	private JPanel mainMsgPanel;
	
	public DrawModel getDrawModel(){
		return model;
	}
	
	public JTextPane getCodePanel() {
		return codePanel;
	}

	public JTextPane getMsgPanel() {
		return msgPanel;
	}

	public JScrollPane getDrawScrollPane() {
		return drawScrollPane;
	}
	

	public JPanel getMainCodePanel() {
		return mainCodePanel;
	}

	public JPanel getMainMsgPanel() {
		return mainMsgPanel;
	}


	private String data = null;
	
	public void setData(String data){
		this.data = data;
	}
	
	/**
	 * @param drawPanel 视图
	 * @param model 模型
	 * @param data 模型所需要的数据， 默认是以逗号分隔
	 */
	public DrawControler(DrawPanel drawPanel, DrawModel model) {
		super();
		this.drawPanel = drawPanel;
		this.model = model;
		model.addObserver(drawPanel);
		this.drawPanel.setBackground(Color.WHITE);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		TabSet tabs = new TabSet(new TabStop[] { new TabStop(20) });
		AttributeSet paraSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
		codePanel = new JTextPane();
		codePanel.setEditable(false);
		codePanel.setParagraphAttributes(paraSet, false);
		
		msgPanel = new JTextPane();
		msgPanel.setFont(new Font("宋体", Font.BOLD, 16));
		msgPanel.setEditable(false);
		msgPanel.setParagraphAttributes(paraSet, false);
		
		JPanel backCodePanel = new JPanel(new BorderLayout());
		backCodePanel.add(codePanel, BorderLayout.CENTER);
		JPanel backMsgPanel = new JPanel(new BorderLayout());
		backMsgPanel.add(msgPanel, BorderLayout.CENTER);
		drawScrollPane = new JScrollPane(drawPanel);
		JScrollPane codeScrollPane = new JScrollPane(backCodePanel);
		mainCodePanel = new JPanel();
		mainCodePanel.setLayout(new BorderLayout());
		JLabel codeLabel = new JLabel("算法CODE", JLabel.CENTER);
		codeLabel.setForeground(Color.RED);
		mainCodePanel.add(codeLabel, BorderLayout.NORTH);
		mainCodePanel.add(codeScrollPane, BorderLayout.CENTER);
		JScrollPane msgScrollPane = new JScrollPane(backMsgPanel);
		mainMsgPanel = new JPanel();
		mainMsgPanel.setLayout(new BorderLayout());
		JLabel msgLabel = new JLabel("算法MESSAGE", JLabel.CENTER);
		msgLabel.setForeground(Color.RED);
		mainMsgPanel.add(msgLabel, BorderLayout.NORTH);
		mainMsgPanel.add(msgScrollPane, BorderLayout.CENTER);
	}
	
	public class StackControler{
		private StackModel stackModel = null;
		public StackControler(){
			stackModel = (StackModel)model.getModel("StackModel", new Object[]{model});
		}
		
		public void showStack(){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "栈模拟");
				jsono.put("模拟方式: ", "随机模拟"); 
				PanelControler.initMsgPanelText(jsono);
			}
			stackModel.showStack();
			model.setViewChanged();
		}
	}
	
	public class QueueControler{
		private QueueModel queueModel = null;
		public QueueControler(){
			queueModel = ((QueueModel)model.getModel("QueueModel", new Object[]{model}));
		}
		public void showQueue(){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "队列模拟");
				jsono.put("模拟方式: ", "随机模拟"); 
				PanelControler.initMsgPanelText(jsono);
			}
			queueModel.showQueue();
			model.setViewChanged();
		}
	}
	
	public class ArrayControler{
		private ArrayModel arrayModel = null;
		public ArrayControler(){
			arrayModel = ((ArrayModel)model.getModel("ArrayModel", new Object[]{model}));
		}
		public void showArrayView(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "显示数组", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("节点值,节点值,节点值....");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			setData(dialog.getData());
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "显示数组");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			arrayModel.createArrayData(data, false);
			model.setViewChanged();
		}
		
		public void showArrayCreate(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "创建数组", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("节点值,节点值,节点值....");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			dialog.setVisible(true);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "显示创建");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			setData(dialog.getData());
			arrayModel.showArrayCreate(data);
		}
		
		public void showArrayInsert(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "数组插入", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;X;6");
			dialog.setDataFormatContent("节点值,节点值..;插入值;位置");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 3) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "数组插入");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("插入值: ", datas[1]);
				jsono.put("插入位置: ", datas[2]);
				PanelControler.initMsgPanelText(jsono);
			}
			arrayModel.createArrayData(data, true);
			model.setViewChanged();
			arrayModel.showArrayInsert(datas[1], Integer.parseInt(datas[2]));
		}
		
		public void showArrayDelete(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "数组删除", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;4");
			dialog.setDataFormatContent("节点值,节点值..;删除位置");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 2) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "数组删除");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("删除位置: ", datas[1]);
				PanelControler.initMsgPanelText(jsono);
			}
			arrayModel.createArrayData(data, false);
			model.setViewChanged();
			arrayModel.showArrayDelete(Integer.parseInt(datas[1]));
		}
	}
	
	public class ListControler{
		private ListModel listModel = null;
		public ListControler(){
			listModel = ((ListModel)model.getModel("ListModel", new Object[]{model}));
		}
		public void showListView(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "显示链表", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("节点值,节点值,节点值...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "显示链表");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.createListData(data);
			model.setViewChanged();
		}
		
		public void showListCreate(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "创建链表", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("节点值,节点值,节点值...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "创建链表");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.showListCreate(data);
		}
		
		public void showListInsert(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "链表插入", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;X;6");
			dialog.setDataFormatContent("节点值,节点值..;插入值;位置");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 3) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "链表插入");
				String[] contents = datas[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("插入值", datas[1]);
				jsono.put("插入位置", datas[2]);
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.createListData(data);
			model.setViewChanged();
			listModel.showListInsert(datas[1], Integer.parseInt(datas[2]));
		}
		
		public void showListDelete(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "链表删除", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;6");
			dialog.setDataFormatContent("节点值,节点值..;删除位置");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 2) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "链表插入");
				String[] contents = datas[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("删除位置", datas[1]);
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.createListData(data);
			model.setViewChanged();
			listModel.showListDelete(Integer.parseInt(datas[1]));
		}
		
		public void showListMergeForThree(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "链表合并", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,F,G,I;B,C,D,X");
			dialog.setDataFormatContent("链表1;链表2");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);	if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "链表合并之生成新链表");
				String[] list = data.split(";");
				String[] contents = list[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("链表1: ", sb.toString());
				
				contents = list[1].split(",");
				sb.delete(0, sb.length());
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("链表2: ", sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.showListMergeForThree(data);
			model.setViewChanged();
			listModel.beginListMergeForThree();
		}
		
		public void showListMergeForOne(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "链表合并", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,F,G,I;B,C,D,X");
			dialog.setDataFormatContent("链表1;链表2");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);	if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "链表合并之就地合并");
				String[] list = data.split(";");
				String[] contents = list[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("链表1: ", sb.toString());
				
				contents = list[1].split(",");
				sb.delete(0, sb.length());
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("链表2: ", sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.showListMergeForOne(data);
		}
	}
	
	public class BTreeControler{
		private BTreeModel bTreeModel = null;
		
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A B C K ? ? M D ? ? Z ? ? X E ? ? F ? ? ?");
			dialog.setDataFormatContent("二叉树先序遍历('?'表示空节点)");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				jsono.put(MsgPanelConstants.DATA, data);
				jsono.put("注: ", "'?'表示空节点");
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public void showBTreeView() {
			setData(initDialog("二叉树显示"));
			initMsgPanelText("二叉树显示");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.createBTreeData(data);
			model.setViewChanged();
		}
		
		public void showPreCreateBTree() {
			setData(initDialog("二叉树先序创建"));
			initMsgPanelText("二叉树先序创建");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPreCreateBTree(data);
		}
		
		public void showInorCreateBTree(){
			setData(initDialog("二叉树中序创建"));
			initMsgPanelText("二叉树中序创建");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showInorCreateBTree(data);
		}
		
		public void showPostCreateBTree(){
			setData(initDialog("二叉树后序创建"));
			initMsgPanelText("二叉树后序创建");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPostCreateBTree(data);
		}
		
		public void showPreData(){
			setData(initDialog("二叉树先序遍历"));
			initMsgPanelText("二叉树先序遍历");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPreData(data);
		}
		
		public void showInorData(){
			setData(initDialog("二叉树中序遍历"));
			initMsgPanelText("二叉树中序遍历");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showInorData(data);
		}
		
		public void showPostData(){
			setData(initDialog("二叉树后序遍历"));
			initMsgPanelText("二叉树后序遍历");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPostData(data);
		}
		
		public void showBTreeToForest(){
			setData(initDialog("二叉树转化为森林"));
			initMsgPanelText("二叉树转换为森立");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showBTreeToForest(data);
		}
	}
	
	public class GraphicControler{
		private GraphicModel graphicModel = null;
		private boolean isDirected = false, isWeighted = false;
		
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_GRAPHIC);
			dialog.setDataDemoContent("2 1 4,2 3 5,3 4 6,2 4 7,4 5 8,5 6 9,6 7 2,2 6 3,6 2 4,6 2 6,7 8 9,10 11 12,1 2 5,1 2 6,7 8 5,7 8 8,5 7 5,7 5 6,7 5 8");
			dialog.setDataFormatContent("节点 节点 (权值),节点 节点 (权值)...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.isDirected = dialog.getIsDirected();
			this.isWeighted = dialog.getIsWeighted();
			return dialog.getData();
		}
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				jsono.put("图类型: ", this.isDirected ? "有向图" : "无向图");
				jsono.put("权值: ", this.isWeighted ? "有权值" : "无权值");
				StringBuilder sb = new StringBuilder();
				String[] contents = data.split(",");
				for(int i=0; i<contents.length; ++i){
					if(i == 0) sb.append("\n");
					sb.append(contents[i]);
					if(i+1 < contents.length){
						sb.append("      ").append(contents[++i]).append("\n");
					} else {
						sb.append("\n");
					}
				}
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public void showGraphicView(){
			setData(initDialog("图的创建"));
			initMsgPanelText("图的创建");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.createGraphicData(data);
			model.setViewChanged();
		}
		
		public void BFSGraphic(){
			setData(initDialog("BFS遍历"));
			initMsgPanelText("BFS遍历");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.BFSGraphic(data);
		}
		
		public void DFSGraphic(){
			setData(initDialog("DFS遍历"));
			initMsgPanelText("DFS遍历");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.DFSGraphic(data);
		}
		
		public void dijkstra(){
			setData(initDialog("Dijkstra算法"));
			initMsgPanelText("Dijkstra算法");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.dijkstra(data);
		}
		
		public void prim(){
			setData(initDialog("Prim算法"));
			initMsgPanelText("prim算法");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.prim(data);
		}
		
		public void kruskal(){
			setData(initDialog("Kruskal算法"));
			initMsgPanelText("Kruskal算法");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.kruskal(data);
		}
		
		public void floyd(){
			setData(initDialog("Floyd算法"));
			initMsgPanelText("Floyd算法");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.floyd(data);
		}
	}
	
	public class GListControler{
		private GListModel gListModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("X=(A,(b,c,d));A=(k,(a,x,y))");
			dialog.setDataFormatContent("节点=子表;节点=子表...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public GListControler(){
			gListModel = ((GListModel)model.getModel("GListModel", new Object[]{model}));
		}
		public void showGListView(){
			setData(initDialog("广义表显示"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "广义表显示");
				StringBuilder sb = new StringBuilder();
				String[] contents = data.split(";");
				for(int i=0; i<contents.length; ++i){
					if(i == 0) sb.append("\n");
					sb.append(contents[i]).append("\n");
				}
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			gListModel.createGListData(data);
			model.setViewChanged();
		}
	}
	
	public class CrossListControler{
		private CrossListModel crossListModel = null;
		private boolean isDirected = false;
		public void setIsDirected(boolean isDirected) {
			this.isDirected = isDirected;
		}
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_GRAPHIC);
			dialog.setDataDemoContent("4 7;1 2;4 2;4 1;1 3;3 1;3 4;4 3");
			dialog.setDataFormatContent("节点 节点,节点 节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.isDirected = dialog.getIsDirected();
			return dialog.getData();
		}
		public void showCrossListView(){
			setData(initDialog("十字链表显示"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "十字链表显示");
				jsono.put("图类型", this.isDirected ? "有向图" : "无向图");
				StringBuilder sb = new StringBuilder();
				String[] contents = data.split(";");
				for(int i=0; i<contents.length; ++i){
					if(i == 0) sb.append("\n");
					sb.append(contents[i]).append("\n");
				}
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			crossListModel = (CrossListModel) model.getModel("CrossListModel", new Object[]{model, isDirected});
			crossListModel.createCrossListData(data);
			model.setViewChanged();
		}
	}
	
	public class ForestControler{
		private ForestModel forestModel = null;
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				StringBuilder sb = new StringBuilder();
				String[] contents = data.split(";");
				for(int i=0; i<contents.length; ++i){
					if(i == 0) sb.append("\n");
					sb.append(contents[i]).append("\n");
				}
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public void showFroestCreataData(){
			setData(initDialog("森林创建"));
			initMsgPanelText("森林创建");
			forestModel = (ForestModel) model.getModel("ForestModel", new Object[]{model});
			forestModel.showFroestCreataData(data);
			model.setViewChanged();
		}
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("1 2;1 3;1 4;3 8;3 9;5 6;5 7;7 10;7 11;9 12;9 13;9 14;11 15;11 16;11 17;11 18");
			dialog.setDataFormatContent("节点 节点;节点 节点;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void showForestToBTree(){
			setData(initDialog("森林转二叉树"));
			initMsgPanelText("森林转二叉树");
			forestModel = (ForestModel) model.getModel("ForestModel", new Object[]{model});
			forestModel.showForestToBTree(data);
		}
	}
	
	public class SegmentTreeControler{
		private SegmentTreeModel segmentTreeModel = null;
		private boolean minOrMax = false;
		public void setMinOrMax(boolean minOrMax){
			this.minOrMax = minOrMax;
		}
		
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_TREE);
			dialog.setDataDemoContent("2 3 5 4 6 1 5 10 2 3 4 5");
			dialog.setDataFormatContent("节点 节点;节点 节点;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		
		public void showSegmentTreeView(){
			setData(initDialog("线段树显示"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "线段树显示");
				jsono.put("显示方式: ", !minOrMax ? "最小值" : "最大值");
				jsono.put(MsgPanelConstants.DATA, data);
				PanelControler.initMsgPanelText(jsono);
			}
			segmentTreeModel = (SegmentTreeModel) model.getModel("SegmentTreeModel", new Object[]{model, minOrMax});
			segmentTreeModel.createSegmentTreeData(data);
			model.setViewChanged();
		}
	}
	
	public class BinaryIndexedTreeControler{
		private BinaryIndexedTreeModel binaryIndexedTreeModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("2 3 5 4 6 1 5 10 2 3 4 5");
			dialog.setDataFormatContent("节点 节点;节点 节点;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void showBinaryIndexedTreeView(){
			setData(initDialog("树状数组展示"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "树状数组展示");
				jsono.put(MsgPanelConstants.DATA, data);
				PanelControler.initMsgPanelText(jsono);
			}
			binaryIndexedTreeModel = ((BinaryIndexedTreeModel)model.getModel("BinaryIndexedTreeModel", new Object[]{model}));
			binaryIndexedTreeModel.createBinaryIndexedTreeData(data);
			model.setViewChanged();
		}
	}
	
	public class StringControler{
		private StringModel stringModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("acabaabcaabaabcac;abaabcac");
			dialog.setDataFormatContent("原串;模式串");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				String[] strings = data.split(";");
				jsono.put("原串: ", strings[0]);
				jsono.put("目标串: ", strings[1]);
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public StringControler(){
			stringModel = (StringModel) model.getModel("StringModel", new Object[]{model});
		}
		public void BF(){
			setData(initDialog("字符串BF匹配算法"));
			initMsgPanelText("字符串BF匹配算法");
			String[] datas = data.split(";");
			stringModel.BF(datas[0], datas[1]);
		}
		
		public void KMP_NEXT(){
			setData(initDialog("字符串KMP_NEXT算法"));
			initMsgPanelText("字符串KMP_NEXT算法");
			String[] datas = data.split(";");
			stringModel.KMP_NEXTOrNEXTVAL(datas[0], datas[1], true);
		}
		
		public void KMP_NEXTVAL(){
			setData(initDialog("字符串KMP_NEXTVAL算法"));
			initMsgPanelText("字符串KMP_NEXTVAL算法");
			String[] datas = data.split(";");
			stringModel.KMP_NEXTOrNEXTVAL(datas[0], datas[1], false);
		}
	}
	
	public class SelectiveSortControler{
		private SelectiveSortModel selectiveSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void selectiveSort(){
			setData(initDialog("选择排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "选择排序模拟");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			selectiveSortModel = (SelectiveSortModel) model.getModel("SelectiveSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			selectiveSortModel.selectiveSort(data, minOrMax);
		}
	}
	
	public class SampleInsertSortControler{
		private SampleInsertSortModel sampleInsertSortModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		private boolean minOrMax = false;
		public void insertSort(){
			setData(initDialog("简单插入排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "简单插入排序模拟");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			sampleInsertSortModel = (SampleInsertSortModel) model.getModel("SampleInsertSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			sampleInsertSortModel.insertSort(data, minOrMax);
		}
	}
	
	public class BinaryInsertSortControler{
		private BinaryInsertSortModel binaryInsertSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void binaryInsertSort(){
			setData(initDialog("二分插入排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "二分插入排序模拟");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			binaryInsertSortModel = (BinaryInsertSortModel) model.getModel("BinaryInsertSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			binaryInsertSortModel.binaryInsertSort(data, minOrMax);
		}
	}
	
	public class BubbleSortControler{
		private BubbleSortModel bubbleSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void bubbleSort(){
			setData(initDialog("起泡排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "气泡排序");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			bubbleSortModel = (BubbleSortModel) model.getModel("BubbleSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			bubbleSortModel.bubbleSort(data, minOrMax);
		}
	}
	
	public class QuickSortControler{
		private QuickSortModel quickSortModel = null;
		private boolean minOrMax = false;
		private boolean firstOrSecond = false;
		
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setSortType(MyDialog.SORT_QUICK_TYPE);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			this.firstOrSecond = dialog.isqFirstOrSecond();
			return dialog.getData();
		}
		 
		public void quickSort(){
			setData(initDialog("快速排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "快速排序模拟");
				jsono.put("快排选择: ", firstOrSecond ? "两指针" : "一指针");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			//快排的方式1 或者 方式2
			quickSortModel = (QuickSortModel) model.getModel("QuickSortModel", new Object[]{model, firstOrSecond});
			//从小到大或者从大到小排序
			quickSortModel.quickSort(data, minOrMax);
		}
	}
	
	public class MergeSortControler{
		private MergeSortModel mergeSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void mergeSort(){
			setData(initDialog("归并排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "归并排序模拟");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			mergeSortModel = (MergeSortModel) model.getModel("MergeSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			mergeSortModel.mergeSort(data, minOrMax);
		}
	}
	
	public class HeapSortControler{
		private HeapSortModel heapSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void heapSort(){
			setData(initDialog("堆排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "堆排序模拟");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			heapSortModel = (HeapSortModel) model.getModel("HeapSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			heapSortModel.heapSort(data, minOrMax);
		}
	}
	
	public class ShellInsertSortControler{
		private ShellInsertSortModel shellInsertSortModel = null;
		private int[] dk = {5, 3, 1};
		public void setDK(int[] dk){
			this.dk = dk;
		}
		
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setSortType(MyDialog.SORT_SHELL_TYPE);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			this.dk = dialog.getSteps();
			return dialog.getData();
		}
		
		public void insertSort(){
			setData(initDialog("希尔排序"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "希尔排序");
				jsono.put("排序方式: ", minOrMax ? "由小到大" : "由大到小");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			shellInsertSortModel = (ShellInsertSortModel) model.getModel("ShellInsertSortModel", new Object[]{model});
			//从小到大或者从大到小排序
			shellInsertSortModel.insertSort(data, minOrMax, dk);
		}
	}
	
	public class RadixSortControler{
		private RadixSortModel radixSortModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setSortType(MyDialog.SORT_RADIX_TYPE);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("节点,节点,节点...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			int way = dialog.getrOneOfThree();
			if(way == 0){
				lsdOrMsd = true;
				oneOrTwo = true;
			} else if(way == 1){
				lsdOrMsd = true;
				oneOrTwo = false;
			} else if(way == 2){
				lsdOrMsd = false;
			}
			return dialog.getData();
		}
		private boolean lsdOrMsd = false;
		private boolean oneOrTwo = false;
		
		public void radixSort(){
			setData(initDialog("基数排序模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "基数排序模拟");
				jsono.put("排序方式: ", lsdOrMsd ? "LSD方式" : "MSD方式");
				if(lsdOrMsd) jsono.put("模拟方式: ", oneOrTwo ? "方式1" : "方式2");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			//LSD或者是MSD
			radixSortModel = (RadixSortModel) model.getModel("RadixSortModel", new Object[]{model, lsdOrMsd});
			//从小到大或者从大到小排序
			//lsd方式的基数排序有两种， msd（最高位关键字优先）的一种
			radixSortModel.radixSort(data, oneOrTwo);
		}
	}
	
	public class UnionFindSetControler{
		private UnionFindSetModel unionFindSetModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("1 2 3 4 5 6 7 8;1 2,2 5,6 7,7 2,6 8");
			dialog.setDataFormatContent("节点,节点,节点...;附属关系");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void unionFindSet(){
			setData(initDialog("并查集模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "并查集模拟");
				String[] datas = data.split(";"); 
				jsono.put("结点: ", datas[0]);
				String[] contents = datas[1].split(",");
				StringBuilder sb = new StringBuilder();
				sb.append("\n");
				for(String content : contents)
					sb.append(content).append("\n");
				jsono.put("附属关系: ", sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			unionFindSetModel = (UnionFindSetModel) model.getModel("UnionFindSetModel", new Object[]{model});
			unionFindSetModel.unionFindSet(data);
		}
	}
	
	public class FormulaControler{
		private FormulaModel formulaModel = null;
		public FormulaControler(){
			formulaModel = (FormulaModel) model.getModel("FormulaModel", new Object[]{model});
		}
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_FORMULA);
			dialog.setDataDemoContent("(2+4)+((2)+(2+(0))-(3))");
			dialog.setDataFormatContent("合法表达式");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.showWay = dialog.getFormulaShowWay();
			return dialog.getData();
		}
		public static final int FORMULA_TREE = 1;
		public static final int FORMULA_STACK = 2;
		private int showWay = 1;
		public void formula(){
			setData(initDialog("表达式计算模拟"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, showWay == 1 ? "表达式计算之栈方式模拟" : "表达式计算之二叉树方式模拟");
				jsono.put("表达式: ", data+"=");
				PanelControler.initMsgPanelText(jsono);
			}
			if(showWay == 1)
				formulaStack();
			else if(showWay == 2)
				formulaTree();
		}
		
		private void formulaTree(){
			formulaModel.formulaTree(data);
		}
		
		private void formulaStack(){
			formulaModel = (FormulaModel) model.getModel("FormulaModel", new Object[]{model});
			formulaModel.formulaStack(data);
		}
	}
	
	public class HuffmanTreeControler{
		private HuffmanTreeModel huffmanTreeModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("abcde");
			dialog.setDataFormatContent("编码字符串");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		public HuffmanTreeControler(){
			huffmanTreeModel = (HuffmanTreeModel) model.getModel("HuffmanTreeModel", new Object[]{model});
		}
		public void huffmanTree(){
			setData(initDialog("哈弗曼编码模拟"));
			huffmanTreeModel.huffmanTree(data);
		}
	}
	
	public class MazeControler{
		private MazeModel mazeModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("4 4 0 0 3 3");
			dialog.setDataFormatContent("宽度 高度 初始x 初始y 结束x 结束y");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public MazeControler() {
			mazeModel = (MazeModel)model.getModel("MazeModel", new Object[]{model});
		}

		public void mazeShowByDfs(){
			setData(initDialog("迷宫DFS模拟"));
			mazeModel.mazeShowByDfs(data);
		}
		
		public void mazeShowByBfs(){
			setData(initDialog("迷宫BFS模拟"));
			mazeModel.mazeShowByBfs(data);
		}
	}
	
	public static void main(String[] args) throws MalformedURLException {
		DrawPanel drawPanel = new DrawPanel();
		DrawModel model = new DrawModel();
		//通过PanelControler 来管理 DrawControler
		PanelControler.initControler(new DrawControler(drawPanel, model));
		MainFrame mainFrame = new MainFrame("数据结构算法模拟系统");
		PanelControler.mainFrame = mainFrame;
		WelcomeWindow welcome = new WelcomeWindow(mainFrame);
	}

}
