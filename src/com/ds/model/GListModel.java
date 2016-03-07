package com.ds.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.ds.shape.DsGListRect;
import com.ds.shape.DsLine;
import com.ds.shape.Shape;
import com.ds.size.ShapeSize;

public class GListModel{
	private DrawModel model;
	private GLNode gListH = null;
	private Map<String, String> mp = new TreeMap<String, String>();
    void initMap(String[] datas){
        for(String s : datas){
		   String first, second;
		   int pos = s.indexOf("=");
		   if(pos != -1){
		       first = s.substring(0, pos);
		       second = s.substring(pos+1, s.length());
		   } else {
		       throw new IllegalArgumentException("���ݴ���!");
		   }
		   mp.put(first, second);
        }
    }
    
    GLNode createGListModel(String s){
    	GLNode L;
        if("()".equals(s)){
            //�����ձ�
            L = null; 
        } else { 
            L = new GLNode();
            if(s.indexOf(",")==-1 && s.indexOf("(")==-1 && s.indexOf(")")==-1){//ԭ�ӽ�� 
                if(!mp.containsKey(s)){
                    L.tag = GLNode.NODE;
                    L.content = s;
                } else {//��s��ֻ��һ����д��ĸ��ɵ�ʱ��˵������һ���ӱ�������չ 
                    return createGListModel(mp.get(s));
                }
            } else {//��ԭ�ӽ�� 
                L.tag = GLNode.LIST;
                GLNode p = L, q;
                s = s.substring(1, s.length()-1);
                do{
                    //�����ͷ
                	String hs = null;
    	            int k = 0;//��¼��δƥ����������ĸ���
    	            int i;
    	            for(i = 0; i<s.length(); ++i) {
    	                if(s.charAt(i)=='(') ++k;
    	                if(s.charAt(i)==')') --k;
    	                if(k==0 && s.charAt(i)==',') break;
    	            }
    	            if(i < s.length()){
    	                hs = s.substring(0, i);
    	                s = s.substring(i+1, s.length());
    	            } else {
    	                hs = s;
    	                s = "";
    	            }
                    p.ptr.hp = createGListModel(hs);
                    q = p;
                    if(!"".equals(s)){//��β���� 
                        p = new GLNode();
                        p.tag = GLNode.LIST;
                        q.ptr.tp = p;    
                    }
                }while(!"".equals(s));
                q.ptr.tp = null;
            }
        }
        return L;
    }
    
    private int leftDist = ShapeSize.GraphicModel.LEFT_MARGIN; 
    /**
     * @param f	 Lx�ڵ��ϲ�ĸ��ڵ�
     * @param Lx	�����Ľڵ�
     * @param topDist	�����ڵ�ͼ�ξ�������ϱߵľ���
     */
    void createGListGraphic(GLNode f, GLNode Lx, int topDist) {
    	ArrayList<Shape> shapeList = model.getShapeList();
    	if(Lx == null) return;
    	GLNode endNode = null;
        for(GLNode p=Lx, q=null; p != null; p=p.ptr.tp) {
        	DsGListRect shape = new DsGListRect(leftDist, topDist, ShapeSize.GListModel.NODE_WIDTH, ShapeSize.GListModel.NODE_HEIGHT);
        	shape.contentValue = p.content;
            if(p.tag==GLNode.NODE) {
            	shape.contentTag = "0";
            } else {
            	shape.contentTag = "1";
            	if(p.ptr.tp == null)
            		shape.contentPtrContent = "^";
            }
            shapeList.add(shape);
            p.shape = shape;
            if(q != null) {//��ˮƽ��ֱ��
            	DsLine line = new DsLine(q.shape.lx+q.shape.lw/6*5, q.shape.ly+q.shape.lh/2, p.shape.lx, p.shape.ly+p.shape.lh/2, true);
            	shapeList.add(line);
            } else {//����ֱ��ֱ��
            	if(f != null) {
            		DsLine line = new DsLine(f.shape.lx+f.shape.lw/2, f.shape.ly+f.shape.lh/2, p.shape.lx+p.shape.lw/2, p.shape.ly, true);
            		shapeList.add(line);
            	}
            }
        	createGListGraphic(p, p.ptr.hp, topDist+ShapeSize.GListModel.NODE_HEIGHT + ShapeSize.GListModel.NODES_VER_DIST);
        	q = p;
        	endNode = p;
        }
        if(endNode.tag != GLNode.NODE) return;
        leftDist += ShapeSize.GListModel.NODE_WIDTH + ShapeSize.GListModel.NODES_HOR_DIST;
    }
    
    public void createGListData(String s){
    	String[] datas = s.split(";");
    	if(datas.length == 0) return;
    	//����������model
    	initMap(datas);
    	int pos = datas[0].indexOf("=");
    	if(pos != -1)
    		gListH = createGListModel(datas[0].substring(pos+1, datas[0].length()));
    	//ͼ�ε����
    	createGListGraphic(null, gListH, ShapeSize.GListModel.TOP_MARGIN);
    }

	public GListModel(DrawModel model) {
		super();
		this.model = model;
	}
}
