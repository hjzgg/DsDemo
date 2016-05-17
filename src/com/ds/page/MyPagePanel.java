package com.ds.page;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.PagesPerMinute;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.StyleConstants;

import net.sf.json.JSONObject;

import com.ds.panel.CommunicationPanel;
import com.ds.panel.chat.ChatPanel;
import com.ds.tools.JavaRequest;

public class MyPagePanel extends JPanel{
	private final int PAGE_BTN_NUM = 6;//��ť����Ŀ�����ֵ, һ��Ϊż��
	private JButton[] pageBtns = new JButton[PAGE_BTN_NUM];
	private JButton preBtn = new JButton("<<");//ǰһҳ
	private JButton nextBtn = new JButton(">>");//��һҳ
	
	private JButton jumpBtn = new JButton("��ת");
	private JTextField jumpText = new JTextField(5);//����ҳ��
	private JLabel totPageLabel = new JLabel("��0ҳ");//��ʾһ������ҳ
	
	public static final int PAGE_SIZE = 20;
	private CommunicationPanel parentPanel;
	private ChatPanel chatPanel;
	//ˢ��ĳҳ
	public void refreshPage(){
		if(curPage == -1){
			JOptionPane.showMessageDialog(null, "��ѡ����Ŀ��Ȼ����ˢ��!", "��ܰ��ʾ",JOptionPane.WARNING_MESSAGE);
		} else {
			requestPage(curPage);
		}
	}
	//����ĳҳ
	public void requestPage(int pageNumber){
		//��ǰҳ��ť��Ϊ true
		if(curBtnPos >= 0)
			pageBtns[curBtnPos].setEnabled(true);
		
		JSONObject jsono = new JSONObject();
		jsono.put("problemName", JavaRequest.problemName);
		jsono.put("pageSize", String.valueOf(PAGE_SIZE));
		jsono.put("pageNumber", String.valueOf(pageNumber));
		String data = JavaRequest.sendPost("getCurProblemComments", jsono);
		
		//��ȡ��ǰ��Ŀ�� ���ۼ�¼
		JSONObject result = JSONObject.fromObject(data);
		if((Boolean) result.get("success")){//��ҳ��ʾ
			JSONObject message = JSONObject.fromObject(result.get("message"));
			Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
			classMap.put("content", MyMessage.class);
			MyPage myPage = (MyPage) JSONObject.toBean(message, MyPage.class, classMap);
			this.updatePage(myPage);
		} else {
			JOptionPane.showMessageDialog(null, result.getString("message"), "��ܰ��ʾ",JOptionPane.WARNING_MESSAGE);
			if(result.get("returnLogin") != null && (Boolean)result.get("returnLogin")){//����û���¼��ʱ�����û���½����
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		}
	}
	//��ת��ҳ��
	private void jumpPage(int pageNumber) throws Exception{
		if(pageNumber < 0 || pageNumber >= totalPages)
			throw new Exception("�����ڸ�ҳ��!");
		requestPage(pageNumber);
	}
	
	public MyPagePanel(CommunicationPanel parentPanelx, ChatPanel chatPanelx){
		this.parentPanel = parentPanelx;
		this.chatPanel = chatPanelx;
		
		preBtn.setEnabled(false);
		this.add(preBtn);
		preBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requestPage(curPage-1);
			}
		});
		
		for(int i=0; i<pageBtns.length; ++i) {
			pageBtns[i] = new JButton();
			this.add(pageBtns[i]);
			pageBtns[i].setVisible(false);
			pageBtns[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//JButton text ��ʾ����ҳ��
					String text = ((JButton)e.getSource()).getText();
					int pageNumber = Integer.valueOf(text);
					requestPage(pageNumber);
				}
			});
		}
		
		nextBtn.setEnabled(false);
		this.add(nextBtn);
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requestPage(curPage+1);
			}
		});
		
		jumpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					if(jumpText.getText().isEmpty()) throw new Exception("ҳ������Ϊ��!");
					int pageNumber = Integer.valueOf(jumpText.getText());
					jumpPage(pageNumber);
				} catch (Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "��ȷ��ҳ���Ƿ���ȷ!", "��ܰ��ʾ",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		this.add(totPageLabel);
		this.add(jumpText);
		this.add(jumpBtn);
	}
	private int curPage = -1, totalPages, curBtnPos = -1;
	public void updatePage(MyPage myPage){
		//�����ҳ�����Ϣ
		chatPanel.getJpChat().setText("");
		//������ܵ��� ������Ϣ
		for(MyMessage message : myPage.getContent()){
			chatPanel.addRecMsg(message.getCcontent());
		}
		
		curPage = myPage.getNumber();
		totalPages = myPage.getTotalPages();
		totPageLabel.setText("��" + totalPages + "ҳ");
		
		if(curPage == 0) preBtn.setEnabled(false);
		else preBtn.setEnabled(true);
		
		if(curPage == totalPages-1 || totalPages == 0) nextBtn.setEnabled(false);
		else nextBtn.setEnabled(true);
		
		List<Integer> pageNum = new LinkedList<Integer>();
		if(totalPages > 0) {
			int step = PAGE_BTN_NUM/2-1;
			for(int i=step; i>=1; --i) {
				if(curPage-i>=0)
					pageNum.add(curPage-i);
			}
			
			pageNum.add(curPage);
			
			for(int i=1; i<=step+1 && curPage+i<totalPages; ++i)
				pageNum.add(curPage+i);
			
			if(pageNum.size() < PAGE_BTN_NUM){
				while(pageNum.size() < PAGE_BTN_NUM && pageNum.get(0)-1 >= 0)
					pageNum.add(0, pageNum.get(0)-1);
				
				while(pageNum.size() < PAGE_BTN_NUM && pageNum.get(pageNum.size()-1)+1 < totalPages)
					pageNum.add(pageNum.get(pageNum.size()-1)+1);
			}
		}
		
		int bi = 0;
		for(int pi=0; pi < pageNum.size(); ++pi, ++bi){
			pageBtns[bi].setText(String.valueOf(pageNum.get(pi)));
			if(pageNum.get(pi) == curPage){
				curBtnPos = bi;//��¼�����ť��ҳ��ֵ �� ��ǰҳ��ֵ���
			}
		}
		//������ʾ��ť
		for(int i=0; i<bi; ++i)
			pageBtns[i].setVisible(true);
			
		//������Щ��ť����û�� ���ϵ�
		for(int i=bi; i < pageBtns.length; ++i){
			pageBtns[i].setVisible(false);
		}
		
		if(totalPages > 0)
			pageBtns[curBtnPos].setEnabled(false);
	}
}
