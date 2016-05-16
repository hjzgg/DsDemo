package com.ds.controler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
	 * @param drawPanel ��ͼ
	 * @param model ģ��
	 * @param data ģ������Ҫ�����ݣ� Ĭ�����Զ��ŷָ�
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
		msgPanel.setFont(new Font("����", Font.BOLD, 16));
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
		JLabel codeLabel = new JLabel("�㷨CODE", JLabel.CENTER);
		codeLabel.setForeground(Color.RED);
		mainCodePanel.add(codeLabel, BorderLayout.NORTH);
		mainCodePanel.add(codeScrollPane, BorderLayout.CENTER);
		JScrollPane msgScrollPane = new JScrollPane(backMsgPanel);
		mainMsgPanel = new JPanel();
		mainMsgPanel.setLayout(new BorderLayout());
		JLabel msgLabel = new JLabel("�㷨MESSAGE", JLabel.CENTER);
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
				jsono.put(MsgPanelConstants.TITLE, "ջģ��");
				jsono.put("ģ�ⷽʽ: ", "���ģ��"); 
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
				jsono.put(MsgPanelConstants.TITLE, "����ģ��");
				jsono.put("ģ�ⷽʽ: ", "���ģ��"); 
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "��ʾ����", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ,�ڵ�ֵ....");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			setData(dialog.getData());
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��ʾ����");
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "��������", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ,�ڵ�ֵ....");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			dialog.setVisible(true);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��ʾ����");
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "�������", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;X;6");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ..;����ֵ;λ��");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 3) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�������");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("����ֵ: ", datas[1]);
				jsono.put("����λ��: ", datas[2]);
				PanelControler.initMsgPanelText(jsono);
			}
			arrayModel.createArrayData(data, true);
			model.setViewChanged();
			arrayModel.showArrayInsert(datas[1], Integer.parseInt(datas[2]));
		}
		
		public void showArrayDelete(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "����ɾ��", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;4");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ..;ɾ��λ��");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 2) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "����ɾ��");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("ɾ��λ��: ", datas[1]);
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "��ʾ����", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ,�ڵ�ֵ...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��ʾ����");
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "��������", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ,�ڵ�ֵ...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��������");
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
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "�������", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;X;6");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ..;����ֵ;λ��");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 3) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�������");
				String[] contents = datas[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("����ֵ", datas[1]);
				jsono.put("����λ��", datas[2]);
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.createListData(data);
			model.setViewChanged();
			listModel.showListInsert(datas[1], Integer.parseInt(datas[2]));
		}
		
		public void showListDelete(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "����ɾ��", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,B,C,D,E,F,G,H;6");
			dialog.setDataFormatContent("�ڵ�ֵ,�ڵ�ֵ..;ɾ��λ��");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			String[] datas = tmpData.split(";");
			if(datas.length != 2) return;
			setData(datas[0]);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�������");
				String[] contents = datas[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				jsono.put("ɾ��λ��", datas[1]);
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.createListData(data);
			model.setViewChanged();
			listModel.showListDelete(Integer.parseInt(datas[1]));
		}
		
		public void showListMergeForThree(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "����ϲ�", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,F,G,I;B,C,D,X");
			dialog.setDataFormatContent("����1;����2");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);	if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "����ϲ�֮����������");
				String[] list = data.split(";");
				String[] contents = list[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("����1: ", sb.toString());
				
				contents = list[1].split(",");
				sb.delete(0, sb.length());
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("����2: ", sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			listModel.showListMergeForThree(data);
			model.setViewChanged();
			listModel.beginListMergeForThree();
		}
		
		public void showListMergeForOne(){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, "����ϲ�", true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("A,F,G,I;B,C,D,X");
			dialog.setDataFormatContent("����1;����2");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);	if(dialog.getData() == null || "".equals(dialog.getData())) return;
			String tmpData = dialog.getData();
			setData(tmpData);
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "����ϲ�֮�͵غϲ�");
				String[] list = data.split(";");
				String[] contents = list[0].split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("����1: ", sb.toString());
				
				contents = list[1].split(",");
				sb.delete(0, sb.length());
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("����2: ", sb.toString());
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
			dialog.setDataFormatContent("�������������('?'��ʾ�սڵ�)");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				jsono.put(MsgPanelConstants.DATA, data);
				jsono.put("ע: ", "'?'��ʾ�սڵ�");
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public void showBTreeView() {
			setData(initDialog("��������ʾ"));
			initMsgPanelText("��������ʾ");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.createBTreeData(data);
			model.setViewChanged();
		}
		
		public void showPreCreateBTree() {
			setData(initDialog("���������򴴽�"));
			initMsgPanelText("���������򴴽�");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPreCreateBTree(data);
		}
		
		public void showInorCreateBTree(){
			setData(initDialog("���������򴴽�"));
			initMsgPanelText("���������򴴽�");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showInorCreateBTree(data);
		}
		
		public void showPostCreateBTree(){
			setData(initDialog("���������򴴽�"));
			initMsgPanelText("���������򴴽�");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPostCreateBTree(data);
		}
		
		public void showPreData(){
			setData(initDialog("�������������"));
			initMsgPanelText("�������������");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPreData(data);
		}
		
		public void showInorData(){
			setData(initDialog("�������������"));
			initMsgPanelText("�������������");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showInorData(data);
		}
		
		public void showPostData(){
			setData(initDialog("�������������"));
			initMsgPanelText("�������������");
			bTreeModel = ((BTreeModel)model.getModel("BTreeModel", new Object[]{model}));
			bTreeModel.showPostData(data);
		}
		
		public void showBTreeToForest(){
			setData(initDialog("������ת��Ϊɭ��"));
			initMsgPanelText("������ת��Ϊɭ��");
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
			dialog.setDataFormatContent("�ڵ� �ڵ� (Ȩֵ),�ڵ� �ڵ� (Ȩֵ)...");
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
				jsono.put("ͼ����: ", this.isDirected ? "����ͼ" : "����ͼ");
				jsono.put("Ȩֵ: ", this.isWeighted ? "��Ȩֵ" : "��Ȩֵ");
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
			setData(initDialog("ͼ�Ĵ���"));
			initMsgPanelText("ͼ�Ĵ���");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.createGraphicData(data);
			model.setViewChanged();
		}
		
		public void BFSGraphic(){
			setData(initDialog("BFS����"));
			initMsgPanelText("BFS����");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.BFSGraphic(data);
		}
		
		public void DFSGraphic(){
			setData(initDialog("DFS����"));
			initMsgPanelText("DFS����");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.DFSGraphic(data);
		}
		
		public void dijkstra(){
			setData(initDialog("Dijkstra�㷨"));
			initMsgPanelText("Dijkstra�㷨");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.dijkstra(data);
		}
		
		public void prim(){
			setData(initDialog("Prim�㷨"));
			initMsgPanelText("prim�㷨");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.prim(data);
		}
		
		public void kruskal(){
			setData(initDialog("Kruskal�㷨"));
			initMsgPanelText("Kruskal�㷨");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.kruskal(data);
		}
		
		public void floyd(){
			setData(initDialog("Floyd�㷨"));
			initMsgPanelText("Floyd�㷨");
			graphicModel = (GraphicModel) model.getModel("GraphicModel", new Object[]{model, isDirected, isWeighted});
			graphicModel.floyd(data);
		}
	}
	
	public class GListControler{
		private GListModel gListModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("X=(A,(b,c,d));A=(k,(a,x,y))");
			dialog.setDataFormatContent("�ڵ�=�ӱ�;�ڵ�=�ӱ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public GListControler(){
			gListModel = ((GListModel)model.getModel("GListModel", new Object[]{model}));
		}
		public void showGListView(){
			setData(initDialog("�������ʾ"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�������ʾ");
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
			dialog.setDataFormatContent("�ڵ� �ڵ�,�ڵ� �ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.isDirected = dialog.getIsDirected();
			return dialog.getData();
		}
		public void showCrossListView(){
			setData(initDialog("ʮ��������ʾ"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "ʮ��������ʾ");
				jsono.put("ͼ����", this.isDirected ? "����ͼ" : "����ͼ");
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
			setData(initDialog("ɭ�ִ���"));
			initMsgPanelText("ɭ�ִ���");
			forestModel = (ForestModel) model.getModel("ForestModel", new Object[]{model});
			forestModel.showFroestCreataData(data);
			model.setViewChanged();
		}
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("1 2;1 3;1 4;3 8;3 9;5 6;5 7;7 10;7 11;9 12;9 13;9 14;11 15;11 16;11 17;11 18");
			dialog.setDataFormatContent("�ڵ� �ڵ�;�ڵ� �ڵ�;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void showForestToBTree(){
			setData(initDialog("ɭ��ת������"));
			initMsgPanelText("ɭ��ת������");
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
			dialog.setDataFormatContent("�ڵ� �ڵ�;�ڵ� �ڵ�;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		
		public void showSegmentTreeView(){
			setData(initDialog("�߶�����ʾ"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�߶�����ʾ");
				jsono.put("��ʾ��ʽ: ", !minOrMax ? "��Сֵ" : "���ֵ");
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
			dialog.setDataFormatContent("�ڵ� �ڵ�;�ڵ� �ڵ�;...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void showBinaryIndexedTreeView(){
			setData(initDialog("��״����չʾ"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��״����չʾ");
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
			dialog.setDataFormatContent("ԭ��;ģʽ��");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		private void initMsgPanelText(String title){
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, title);
				String[] strings = data.split(";");
				jsono.put("ԭ��: ", strings[0]);
				jsono.put("Ŀ�괮: ", strings[1]);
				PanelControler.initMsgPanelText(jsono);
			}
		}
		
		public StringControler(){
			stringModel = (StringModel) model.getModel("StringModel", new Object[]{model});
		}
		public void BF(){
			setData(initDialog("�ַ���BFƥ���㷨"));
			initMsgPanelText("�ַ���BFƥ���㷨");
			String[] datas = data.split(";");
			stringModel.BF(datas[0], datas[1]);
		}
		
		public void KMP_NEXT(){
			setData(initDialog("�ַ���KMP_NEXT�㷨"));
			initMsgPanelText("�ַ���KMP_NEXT�㷨");
			String[] datas = data.split(";");
			stringModel.KMP_NEXTOrNEXTVAL(datas[0], datas[1], true);
		}
		
		public void KMP_NEXTVAL(){
			setData(initDialog("�ַ���KMP_NEXTVAL�㷨"));
			initMsgPanelText("�ַ���KMP_NEXTVAL�㷨");
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
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void selectiveSort(){
			setData(initDialog("ѡ������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "ѡ������ģ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			selectiveSortModel = (SelectiveSortModel) model.getModel("SelectiveSortModel", new Object[]{model});
			//��С������ߴӴ�С����
			selectiveSortModel.selectiveSort(data, minOrMax);
		}
	}
	
	public class SampleInsertSortControler{
		private SampleInsertSortModel sampleInsertSortModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		private boolean minOrMax = false;
		public void insertSort(){
			setData(initDialog("�򵥲�������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�򵥲�������ģ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			sampleInsertSortModel = (SampleInsertSortModel) model.getModel("SampleInsertSortModel", new Object[]{model});
			//��С������ߴӴ�С����
			sampleInsertSortModel.insertSort(data, minOrMax);
		}
	}
	
	public class BinaryInsertSortControler{
		private BinaryInsertSortModel binaryInsertSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void binaryInsertSort(){
			setData(initDialog("���ֲ�������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "���ֲ�������ģ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			binaryInsertSortModel = (BinaryInsertSortModel) model.getModel("BinaryInsertSortModel", new Object[]{model});
			//��С������ߴӴ�С����
			binaryInsertSortModel.binaryInsertSort(data, minOrMax);
		}
	}
	
	public class BubbleSortControler{
		private BubbleSortModel bubbleSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void bubbleSort(){
			setData(initDialog("��������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��������");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			bubbleSortModel = (BubbleSortModel) model.getModel("BubbleSortModel", new Object[]{model});
			//��С������ߴӴ�С����
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
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			this.firstOrSecond = dialog.isqFirstOrSecond();
			return dialog.getData();
		}
		 
		public void quickSort(){
			setData(initDialog("��������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��������ģ��");
				jsono.put("����ѡ��: ", firstOrSecond ? "��ָ��" : "һָ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			//���ŵķ�ʽ1 ���� ��ʽ2
			quickSortModel = (QuickSortModel) model.getModel("QuickSortModel", new Object[]{model, firstOrSecond});
			//��С������ߴӴ�С����
			quickSortModel.quickSort(data, minOrMax);
		}
	}
	
	public class MergeSortControler{
		private MergeSortModel mergeSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void mergeSort(){
			setData(initDialog("�鲢����ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "�鲢����ģ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			mergeSortModel = (MergeSortModel) model.getModel("MergeSortModel", new Object[]{model});
			//��С������ߴӴ�С����
			mergeSortModel.mergeSort(data, minOrMax);
		}
	}
	
	public class HeapSortControler{
		private HeapSortModel heapSortModel = null;
		private boolean minOrMax = false;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			return dialog.getData();
		}
		public void heapSort(){
			setData(initDialog("������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "������ģ��");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			heapSortModel = (HeapSortModel) model.getModel("HeapSortModel", new Object[]{model});
			//��С������ߴӴ�С����
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
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.minOrMax = dialog.isMinOrMax();
			this.dk = dialog.getSteps();
			return dialog.getData();
		}
		
		public void insertSort(){
			setData(initDialog("ϣ������"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "ϣ������");
				jsono.put("����ʽ: ", minOrMax ? "��С����" : "�ɴ�С");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			shellInsertSortModel = (ShellInsertSortModel) model.getModel("ShellInsertSortModel", new Object[]{model});
			//��С������ߴӴ�С����
			shellInsertSortModel.insertSort(data, minOrMax, dk);
		}
	}
	
	public class RadixSortControler{
		private RadixSortModel radixSortModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_SORT);
			dialog.setSortType(MyDialog.SORT_RADIX_TYPE);
			dialog.setDataDemoContent("1,5,3,45,4,6,9,4,3,2,23");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...");
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
			setData(initDialog("��������ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "��������ģ��");
				jsono.put("����ʽ: ", lsdOrMsd ? "LSD��ʽ" : "MSD��ʽ");
				if(lsdOrMsd) jsono.put("ģ�ⷽʽ: ", oneOrTwo ? "��ʽ1" : "��ʽ2");
				String[] contents = data.split(",");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put(MsgPanelConstants.DATA, sb.toString());
				PanelControler.initMsgPanelText(jsono);
			}
			//LSD������MSD
			radixSortModel = (RadixSortModel) model.getModel("RadixSortModel", new Object[]{model, lsdOrMsd});
			//��С������ߴӴ�С����
			//lsd��ʽ�Ļ������������֣� msd�����λ�ؼ������ȣ���һ��
			radixSortModel.radixSort(data, oneOrTwo);
		}
	}
	
	public class UnionFindSetControler{
		private UnionFindSetModel unionFindSetModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("1 2 3 4 5 6 7 8;1 2,2 5,6 7,7 2,6 8");
			dialog.setDataFormatContent("�ڵ�,�ڵ�,�ڵ�...;������ϵ");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public void unionFindSet(){
			setData(initDialog("���鼯ģ��"));
			{
				JSONObject jsono = new JSONObject();
				jsono.put(MsgPanelConstants.TITLE, "���鼯ģ��");
				String[] contents = data.split(";");
				StringBuilder sb = new StringBuilder();
				for(String content : contents)
					sb.append(content).append(" ");
				jsono.put("������ϵ", sb.toString());
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
			dialog.setDataFormatContent("�Ϸ����ʽ");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			this.showWay = dialog.getFormulaShowWay();
			return dialog.getData();
		}
		public static final int FORMULA_TREE = 1;
		public static final int FORMULA_STACK = 2;
		private int showWay = 1;
		public void formula(){
			setData(initDialog("���ʽ����ģ��"));
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
			dialog.setDataFormatContent("�����ַ���");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		
		public HuffmanTreeControler(){
			huffmanTreeModel = (HuffmanTreeModel) model.getModel("HuffmanTreeModel", new Object[]{model});
		}
		public void huffmanTree(){
			setData(initDialog("����������ģ��"));
			huffmanTreeModel.huffmanTree(data);
		}
	}
	
	public class MazeControler{
		private MazeModel mazeModel = null;
		private String initDialog(String title){
			MyDialog dialog = new MyDialog(PanelControler.mainFrame, title, true, MyDialog.MODEL_TYPE_OTHER);
			dialog.setDataDemoContent("4 4 0 0 3 3");
			dialog.setDataFormatContent("��� �߶� ��ʼx ��ʼy ����x ����y");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			return dialog.getData();
		}
		public MazeControler() {
			mazeModel = (MazeModel)model.getModel("MazeModel", new Object[]{model});
		}

		public void mazeShowByDfs(){
			setData(initDialog("�Թ�DFSģ��"));
			mazeModel.mazeShowByDfs(data);
		}
		
		public void mazeShowByBfs(){
			setData(initDialog("�Թ�BFSģ��"));
			mazeModel.mazeShowByBfs(data);
		}
	}
	
	public static void main(String[] args) {
		DrawPanel drawPanel = new DrawPanel();
		DrawModel model = new DrawModel();
		//ͨ��PanelControler ������ DrawControler
		PanelControler.initControler(new DrawControler(drawPanel, model));
		MainFrame mainFrame = new MainFrame("���ݽṹ�㷨ģ��ϵͳ");
		PanelControler.mainFrame = mainFrame;
		WelcomeWindow welcome = new WelcomeWindow("image/huaqiangu.jpg", mainFrame, 2000);
	}

}
