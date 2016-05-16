package com.ds.controler;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.sf.json.JSONObject;

import com.ds.button.FunctionButton;
import com.ds.main.MainFrame;
import com.ds.panel.FunctionPanel;

public class PanelControler {
	//��������һ�����
	public static Map<FunctionMsgPanel, FunctionMsgPanel> prePanel = new TreeMap<FunctionMsgPanel, FunctionMsgPanel>();
	//�����ť�����һ�����, �԰�ť�ϵ�Text��Ϊ�ؼ���
	public static Map<String, JPanel> nextPanel = new TreeMap<String, JPanel>();
	//��ͼ������
	public static DrawControler drawControler = null;
	//����ָ���
	public static JSplitPane splitPaneVer = null;
	public static JSplitPane splitPaneHor = null;
	//�������JFrame, MyDialog���õ�
	public static MainFrame mainFrame = null;
	//�㷨ģ�����
	public static class SimulatePanel{
		public static class FirstPanel{
			//���ˮƽ����
			public static final int hgap = 10;
			//�����ֱ����
			public static final int vgap = 10;
			//����
			public static final int cols = 3;
			//�����Ҫ��ʾ�İ�ť����Ϣ
			public static String[] btnsMsg = {"˳���", "����", "ջ", "����", "��", "�����", "������", "ͼ", "����", "���鼯", "ʮ������", "�߼���"};
			//�����
			public static FunctionMsgPanel firstPanel = new FunctionMsgPanel(new GridLayout(0, cols, hgap, vgap));
			
			public static void initFirstPanel(){
				firstPanel.setBtnsMsg(btnsMsg);
				firstPanel.initPanel();
			}
		}
		
		public static class SecondPanel{
			//���ˮƽ����
			public static final int hgap = 150;
			//�����ֱ����
			public static final int vgap = 20;
			//����
			public static final int cols = 2;
			//FirstPanel�е�ÿһ��btn����Ӧһ��SecondPanel���
			//SecondPanel.btnsMsg �� FirstPanel.btnsMsg����һ�£����һ����Ӧ!
			public static String[][] btnsMsg = {
				//˳�����
				{"������ʾ", "���鴴��", "����ɾ��", "�������"},
				//������
				{"������ʾ", "������", "����ɾ��", "�������", "����ϲ�(����������)", "����ϲ�(�͵غϲ�)"},
				//ջ����
				{"ջ��ʾ", "���ʽ��ֵ", "�Թ�DFS"},
				//���й���
				{"������ʾ", "�Թ�BFS"},
				//������
				{"��ƥ��(BF)", "��ƥ��(KMP_NEXT)", "��ƥ��(KMP_NEXTVAL)"},
				//�������
				{"�������ʾ"},
				//����������
				{"��������ʾ", "���򴴽�", "���򴴽�", "���򴴽�", "�������", "�������", "�������", "������ת��Ϊɭ��", "������������"},
				//ͼ����
				{"ͼ����ʾ", "��ȱ���ͼ", "��ȱ���ͼ", "�Ͻ�˹�����㷨", "Prim�㷨", "Kruskal�㷨", "Floyd�㷨"},
				//������
				{"ѡ������", "�򵥲�������", "���ֲ�������", "ð������", "��������", "�鲢����", "������", "ϣ������", "��������"},
				//���鼯����
				{"���鼯��ʾ"},
				//ʮ������
				{"ʮ��������ʾ"},
				//�߼�������
				{"�߶�����ʾ", "��״������ʾ", "ɭ����ʾ", "ɭ��ת��������"}
			};
			public static FunctionMsgPanel[] childFunctionPanel = null;
			public static void initSecondPanel() {
				childFunctionPanel = new FunctionMsgPanel[btnsMsg.length];
				for(int i=0; i<childFunctionPanel.length; ++i){
					childFunctionPanel[i] = new FunctionMsgPanel(new GridLayout(0, cols, hgap, vgap));
					childFunctionPanel[i].setBtnsMsg(btnsMsg[i]);
					childFunctionPanel[i].initPanel();
					nextPanel.put(FirstPanel.btnsMsg[i], childFunctionPanel[i]);
					prePanel.put(childFunctionPanel[i], FirstPanel.firstPanel);
				}
			}
		}
		
		//չʾ���
		public static class ThirdPanel{
			//ͨ��SecondPanel�ϵİ�ť����Ϣ���ҵ��ð�ť��Ӧ���㷨
			public static Map<String, String> btnMsgMapClassMsg = new TreeMap<String, String>();
			public static FunctionMsgPanel thirdPanel = new FunctionMsgPanel(new GridBagLayout());
			//ͨ��java�ķ�����ƻ��DrawControler���ڲ� ȷ�е� Controler
			public static String[][] classMsg = {
				//˳�����
				{"com.ds.controler.DrawControler$ArrayControler;showArrayView", "com.ds.controler.DrawControler$ArrayControler;showArrayCreate", "com.ds.controler.DrawControler$ArrayControler;showArrayDelete", "com.ds.controler.DrawControler$ArrayControler;showArrayInsert"},
				//������
				{"com.ds.controler.DrawControler$ListControler;showListView", "com.ds.controler.DrawControler$ListControler;showListCreate", "com.ds.controler.DrawControler$ListControler;showListDelete", "com.ds.controler.DrawControler$ListControler;showListInsert", "com.ds.controler.DrawControler$ListControler;showListMergeForThree", "com.ds.controler.DrawControler$ListControler;showListMergeForOne"},
				//ջ����
				{"com.ds.controler.DrawControler$StackControler;showStack", "com.ds.controler.DrawControler$FormulaControler;formula", "com.ds.controler.DrawControler$MazeControler;mazeShowByDfs"},
				//���й���
				{"com.ds.controler.DrawControler$QueueControler;showQueue", "com.ds.controler.DrawControler$MazeControler;mazeShowByBfs"},
				//������
				{"com.ds.controler.DrawControler$StringControler;BF", "com.ds.controler.DrawControler$StringControler;KMP_NEXT", "com.ds.controler.DrawControler$StringControler;KMP_NEXTVAL"},
				//�������
				{"com.ds.controler.DrawControler$GListControler;showGListView"},
				//����������
				{"com.ds.controler.DrawControler$BTreeControler;showBTreeView", "com.ds.controler.DrawControler$BTreeControler;showPreCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showInorCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showPostCreateBTree", "com.ds.controler.DrawControler$BTreeControler;showPreData", "com.ds.controler.DrawControler$BTreeControler;showInorData", "com.ds.controler.DrawControler$BTreeControler;showPostData", "com.ds.controler.DrawControler$BTreeControler;showBTreeToForest", "com.ds.controler.DrawControler$HuffmanTreeControler;huffmanTree"},
				//ͼ����
				{"com.ds.controler.DrawControler$GraphicControler;showGraphicView", "com.ds.controler.DrawControler$GraphicControler;BFSGraphic", "com.ds.controler.DrawControler$GraphicControler;DFSGraphic", "com.ds.controler.DrawControler$GraphicControler;dijkstra", "com.ds.controler.DrawControler$GraphicControler;prim", "com.ds.controler.DrawControler$GraphicControler;kruskal", "com.ds.controler.DrawControler$GraphicControler;floyd"},
				//������
				{"com.ds.controler.DrawControler$SelectiveSortControler;selectiveSort", "com.ds.controler.DrawControler$SampleInsertSortControler;insertSort", "com.ds.controler.DrawControler$BinaryInsertSortControler;binaryInsertSort", "com.ds.controler.DrawControler$BubbleSortControler;bubbleSort", "com.ds.controler.DrawControler$QuickSortControler;quickSort", "com.ds.controler.DrawControler$MergeSortControler;mergeSort", "com.ds.controler.DrawControler$HeapSortControler;heapSort", "com.ds.controler.DrawControler$ShellInsertSortControler;insertSort", "com.ds.controler.DrawControler$RadixSortControler;radixSort"},
				//���鼯����
				{"com.ds.controler.DrawControler$UnionFindSetControler;unionFindSet"},
				//ʮ������
				{"com.ds.controler.DrawControler$CrossListControler;showCrossListView"},
				//�߼�������
				{"com.ds.controler.DrawControler$SegmentTreeControler;showSegmentTreeView", "com.ds.controler.DrawControler$BinaryIndexedTreeControler;showBinaryIndexedTreeView", "com.ds.controler.DrawControler$ForestControler;showFroestCreataData", "com.ds.controler.DrawControler$ForestControler;showForestToBTree"}
			};
			
			public static void initThirdPanel(){
				for(int i=0; i < SecondPanel.btnsMsg.length; ++i)
					for(int j=0; j < SecondPanel.btnsMsg[i].length; ++j) {
						nextPanel.put(SecondPanel.btnsMsg[i][j], thirdPanel);
						btnMsgMapClassMsg.put(SecondPanel.btnsMsg[i][j], classMsg[i][j]);
						//�����㷨Դ���ļ�
						try {
							File file = new File("./algorithmCode/" + classMsg[i][j] + ".txt");
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				thirdPanel.initPanel();
			}
		}
	}
	
	
	//��ʼ��
	public static void initControler(DrawControler drawControler){
		PanelControler.drawControler = drawControler;
		//�㷨ģ������еĵڶ�������ʼ��
		SimulatePanel.FirstPanel.initFirstPanel();
		SimulatePanel.SecondPanel.initSecondPanel();
		SimulatePanel.ThirdPanel.initThirdPanel();
	}
	
	public static class FunctionMsgPanel extends JPanel implements Comparable<FunctionMsgPanel>{
		public FunctionButton[] btns = null;
		private boolean initBtns = false;
		public boolean isInitBtns(){
			return initBtns;
		}
		public void initBtns(){
			initBtns = true;
		}
		private String[] btnsMsg;
		public void setBtnsMsg(String[] btnsMsg){
			this.btnsMsg = btnsMsg;
		}
		public FunctionMsgPanel(LayoutManager layout) {
			super();
			this.setAlignmentY(Component.CENTER_ALIGNMENT);
			this.setLayout(layout);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon imageIcon = new ImageIcon("image/super_bg/center.jpg");
			Image image = imageIcon.getImage();
			if(image != null)
				g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getWidth(null), image.getHeight(null), null);
		}
		
		public void initPanel(){
			if(this.getLayout() instanceof GridLayout){//�������
				btns = new FunctionButton[btnsMsg.length];
				for(int i=0; i<btns.length; ++i){
					btns[i] = new FunctionButton(btnsMsg[i]);
					btns[i].setIcon(new ImageIcon(FunctionPanel.class.getResource("/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
					btns[i].setFont(new Font("΢���ź� Light", Font.BOLD, 18));
					this.add(btns[i]);
				}
			} else {//��ʾ���
				splitPaneVer = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				splitPaneVer.setContinuousLayout(true);
				splitPaneVer.setOneTouchExpandable(true);
				splitPaneVer.setResizeWeight(0.7);
				splitPaneVer.setTopComponent(drawControler.getMainCodePanel());
				splitPaneVer.setBottomComponent(drawControler.getMainMsgPanel());
				
				splitPaneHor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				splitPaneHor.setContinuousLayout(true);
				splitPaneHor.setOneTouchExpandable(true);
				splitPaneHor.setResizeWeight(0.8);
				splitPaneHor.setLeftComponent(drawControler.getDrawScrollPane());
				splitPaneHor.setRightComponent(splitPaneVer);
				this.add(splitPaneHor);
				
				GridBagLayout layout = (GridBagLayout) this.getLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 1;//�÷������������ˮƽ��ռ�õĸ����������Ϊ0����˵��������Ǹ��е����һ��
				gbc.gridheight = 1;
				gbc.weightx = 1;//�÷����������ˮƽ��������ȣ����Ϊ0��˵�������죬��Ϊ0�����Ŵ�������������죬0��1֮��
				gbc.weighty = 1;//�÷������������ֱ��������ȣ����Ϊ0��˵�������죬��Ϊ0�����Ŵ�������������죬0��1֮��
				layout.setConstraints(splitPaneHor, gbc);
			}
		}
		
		@Override
		public int compareTo(FunctionMsgPanel o) {
			return this.hashCode() - o.hashCode();
		}
	}
	
	public static void initMsgPanelText(JSONObject params){
		StringBuilder sb = new StringBuilder();
		for(Object key : params.keySet()){
			sb.append(key).append(params.get(key)).append("\n");
		}
		drawControler.getMsgPanel().setText(sb.toString());
	}
}
