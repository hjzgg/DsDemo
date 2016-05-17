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
	private final int PAGE_BTN_NUM = 6;//按钮的数目的最大值, 一定为偶数
	private JButton[] pageBtns = new JButton[PAGE_BTN_NUM];
	private JButton preBtn = new JButton("<<");//前一页
	private JButton nextBtn = new JButton(">>");//后一页
	
	private JButton jumpBtn = new JButton("跳转");
	private JTextField jumpText = new JTextField(5);//输入页号
	private JLabel totPageLabel = new JLabel("共0页");//提示一共多少页
	
	public static final int PAGE_SIZE = 20;
	private CommunicationPanel parentPanel;
	private ChatPanel chatPanel;
	//刷新某页
	public void refreshPage(){
		if(curPage == -1){
			JOptionPane.showMessageDialog(null, "请选择题目，然后再刷新!", "温馨提示",JOptionPane.WARNING_MESSAGE);
		} else {
			requestPage(curPage);
		}
	}
	//请求某页
	public void requestPage(int pageNumber){
		//当前页按钮设为 true
		if(curBtnPos >= 0)
			pageBtns[curBtnPos].setEnabled(true);
		
		JSONObject jsono = new JSONObject();
		jsono.put("problemName", JavaRequest.problemName);
		jsono.put("pageSize", String.valueOf(PAGE_SIZE));
		jsono.put("pageNumber", String.valueOf(pageNumber));
		String data = JavaRequest.sendPost("getCurProblemComments", jsono);
		
		//获取当前题目的 评论记录
		JSONObject result = JSONObject.fromObject(data);
		if((Boolean) result.get("success")){//分页显示
			JSONObject message = JSONObject.fromObject(result.get("message"));
			Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
			classMap.put("content", MyMessage.class);
			MyPage myPage = (MyPage) JSONObject.toBean(message, MyPage.class, classMap);
			this.updatePage(myPage);
		} else {
			JOptionPane.showMessageDialog(null, result.getString("message"), "温馨提示",JOptionPane.WARNING_MESSAGE);
			if(result.get("returnLogin") != null && (Boolean)result.get("returnLogin")){//如果用户登录超时返回用户登陆界面
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		}
	}
	//跳转都页面
	private void jumpPage(int pageNumber) throws Exception{
		if(pageNumber < 0 || pageNumber >= totalPages)
			throw new Exception("不存在该页号!");
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
					//JButton text 表示的是页号
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
					if(jumpText.getText().isEmpty()) throw new Exception("页数不能为空!");
					int pageNumber = Integer.valueOf(jumpText.getText());
					jumpPage(pageNumber);
				} catch (Exception ex){
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "请确定页号是否正确!", "温馨提示",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		this.add(totPageLabel);
		this.add(jumpText);
		this.add(jumpBtn);
	}
	private int curPage = -1, totalPages, curBtnPos = -1;
	public void updatePage(MyPage myPage){
		//清除当页面的信息
		chatPanel.getJpChat().setText("");
		//处理接受到的 评论消息
		for(MyMessage message : myPage.getContent()){
			chatPanel.addRecMsg(message.getCcontent());
		}
		
		curPage = myPage.getNumber();
		totalPages = myPage.getTotalPages();
		totPageLabel.setText("共" + totalPages + "页");
		
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
				curBtnPos = bi;//记录这个按钮的页号值 和 当前页号值相等
			}
		}
		//重新显示按钮
		for(int i=0; i<bi; ++i)
			pageBtns[i].setVisible(true);
			
		//后面这些按钮都是没有 用上的
		for(int i=bi; i < pageBtns.length; ++i){
			pageBtns[i].setVisible(false);
		}
		
		if(totalPages > 0)
			pageBtns[curBtnPos].setEnabled(false);
	}
}
