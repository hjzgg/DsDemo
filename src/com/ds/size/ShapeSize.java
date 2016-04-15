package com.ds.size;

public class ShapeSize {
	public class ArrayModel{
		public static final int ROW_NUM = 4;
		public static final int RECT_WIDTH = 50;
		public static final int RECT_HEIGHT = 50;
		public static final int LINE_DIST_X = 50;
		public static final int LINE_DIST_XX = (ROW_NUM*2-1)*RECT_WIDTH + 2*LINE_DIST_X;//����ͼ��һ�д����ҵĿ��
		public static final int LINE_DIST_Y = RECT_HEIGHT;//���ߵĳ���
		public static final int ROW_HEIGHT = RECT_HEIGHT*4;//������м��
		
		public static final int LEFT_MARGIN = 60;//������˾���panel����ߵĴ�С
		public static final int RIGHT_MARGIN = LINE_DIST_XX+LEFT_MARGIN;
		public static final int TOP_MARGIN = 60;
	}
	
	public class ListModel{
		public static final int ROW_NUM = 4;
		public static final int RECT_WIDTH = 75;
		public static final int SMALL_RECT_WIDTH = 25;
		public static final int RECT_HEIGHT = 50;
		public static final int LINE_DIST_X = RECT_WIDTH+SMALL_RECT_WIDTH*2;// = 110
		public static final int LINE_DIST_XX = ROW_NUM*RECT_WIDTH + (ROW_NUM+1)*LINE_DIST_X;//����ͼ��һ�д����ҵĿ��
		public static final int LINE_DIST_Y = RECT_HEIGHT;//���ߵĳ���
		public static final int ROW_HEIGHT = RECT_HEIGHT*4;//������м��
		
		public static final int LEFT_MARGIN = 20;//������˾���panel����ߵĴ�С
		public static final int RIGHT_MARGIN = LINE_DIST_XX+LEFT_MARGIN;//ͼ�ξ�����ߵ�������
		public static final int TOP_MARGIN = 120;
		
		public static final int NEW_RECT_TOPDIST = 20;
	}
	
	public class TreeModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 20;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int NODES_HOR_DIST = 20;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
	}
	
	public class ForestModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 20;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int NODES_HOR_DIST = 20;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
	}
	
	public class GraphicModel{
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		public static final int NODES_DIST = 150;//ͼ�������ڵ�Բ��֮��ľ���
		public static final int LEFT_MARGIN = 45;
		public static final int TOP_MARGIN = 45;
		public static final int LINES_DIST = 10;//����ߵľ���
		
		public static final int SMALL_RECT_WIDTH = 35;
		public static final int SMALL_RECT_HEIGHT = 35;
		
		public static final int SMALL_CIRCLE_WIDTH = 30;
		public static final int SMALL_CIRCLE_HEIGHT = 30;
	}
	
	//�����
	public class GListModel{
		public static final int NODE_WIDTH = 150;
		public static final int NODE_HEIGHT = 50;
		public static final int NODES_HOR_DIST = 50;
		public static final int NODES_VER_DIST = 50;
		public static final int LEFT_MARGIN = 45;
		public static final int TOP_MARGIN = 45;
	}
	
	//ʮ������
	public class CrossListModel{
		public static final int NODE_ARCBOX_WIDTH = 100;
		public static final int NODE_VECTOR_WIDTH = 75;
		public static final int NODE_HEIGHT = 25;
		public static final int NODES_HOR_DIST = 50;
		public static final int NODES_VER_DIST = 50;
		public static final int LEFT_MARGIN = 45;
		public static final int TOP_MARGIN = 45;
	}
	
	public class SegmentTreeModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 20;
		public static final int NODE_WIDTH = 75;
		public static final int NODE_HEIGHT = 25;
		
		public static final int NODES_HOR_DIST = 25;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 25;//��������Ĵ�ֱ����
	}
	
	public class BinaryIndexedTreeModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 20;
		public static final int NODE_WIDTH = 120;
		public static final int NODE_HEIGHT = 30;
		public static final int NODES_HOR_DIST = 30;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 30;//��������Ĵ�ֱ����
	}
	
	public class StringModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 20;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
		public static final int FIRST_NODE_WIDTH = 150;
	}
	
	public class SelectiveSortModel{
		public static final int LEFT_MARGIN = 100;
		public static final int TOP_MARGIN = 100;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
	}
	
	public class InsertSortModel{
		public static final int LEFT_MARGIN = 100;
		public static final int TOP_MARGIN = 150;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
	}
	
	public class BubbleSortModel{
		public static final int LEFT_MARGIN = 100;
		public static final int TOP_MARGIN = 150;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
	}
	
	public class QuickSortModel{
		public static final int LEFT_MARGIN = 100;
		public static final int TOP_MARGIN = 150;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
	}
	
	public class MergeSortModel{
		public static final int LEFT_MARGIN = 200;
		public static final int TOP_MARGIN = 50;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
		public static final int LINE_DIST = NODE_HEIGHT*3;
	}
	
	public class HeapSortModel{
		public static final int TOP_MARGIN = 50;
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 50;
		
		public static final int LEFT_MARGIN = 50;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int NODES_HOR_DIST = 20;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
	}
	
	public class RadixSortModel{
		public static final int TOP_MARGIN = 150;
		public static final int NODE_WIDTH = 80;
		public static final int NODE_HEIGHT = 30;
		public static final int LEFT_MARGIN = 50;
		public static final int BUCKET_WIDTH = 60;
		public static final int BUCKET_DIST = 30 + NODE_WIDTH;
		
		public static final int TIP_AND_BUCKET_DIST = 10;
	}
	
	public class UnionFindSetModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TOP_MARGIN = 150;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int NODES_HOR_DIST = 50;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
	}
	
	public class FormulaModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TREE_TOP_MARGIN = 200;
		public static final int ARRAY_TOP_MARGIN = 50;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int RECT_WIDTH = 50;
		public static final int RECT_HEIGHT = 30;
		
		public static final int STACK_WIDTH = 70;
		
		public static final int NODES_HOR_DIST = 10;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
		public static final int STACK_TOP_MARGIN = LEFT_MARGIN+RECT_HEIGHT+LEVEL_DIST*2;
		public static final int STACK_DIST = RECT_WIDTH*5;
	}
	
	public class HuffmanTreeModel{
		public static final int LEFT_MARGIN = 20;
		public static final int TREE_LEFT_MARGIN = 360;
		//�߿�ľ���
		public static final int ARRAY_TREE_LD = 10;
		public static final int TREE_TOP_MARGIN = 200;
		public static final int ARRAY_TOP_MARGIN = 20;
		public static final int CIRCLE_WIDTH = 50;
		public static final int CIRCLE_HEIGHT = 50;
		
		public static final int RECT_WIDTH = 50;
		public static final int RECT_HEIGHT = 50;
		
		public static final int NODES_HOR_DIST = 50;//�ڵ�֮���ˮƽ����
		public static final int LEVEL_DIST = 50;//��������Ĵ�ֱ����
		
		public static final int ARRAY_TREE_WIDTH = RECT_WIDTH*5+ARRAY_TREE_LD*2;
	}
	
	public class QueueModel{
		public static final int QUEUE_LEFT_MARGIN = 100;
		public static final int QUEUE_TOP_MARGIN = 100;
		
		public static final int NODE_WIDTH = 50;
		public static final int NODE_HEIGHT = 85;
		
		public static final int NUMBER_OF_LINE = 8;//һ�����8����Ŷ
	}
	
	public class StackModel{
		public static final int STACK_LEFT_MARGIN = 400;
		public static final int STACK_TOP_MARGIN = 150;
		
		public static final int NODE_WIDTH = 70;
		public static final int NODE_HEIGHT = 60;
	}
}
