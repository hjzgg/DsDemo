package com.ds.panel.chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sf.json.JSONObject;

import com.ds.page.MyMessage;
import com.ds.page.MyPage;
import com.ds.page.MyPagePanel;
import com.ds.panel.CommunicationPanel;
import com.ds.tools.JavaRequest;

public class ChatPanel extends JPanel implements MouseListener{
	private static final long serialVersionUID = 1L;
	
	//显示分页按钮
	private MyPagePanel pagePanel;
	
	/* 左边和右边要显示的界面 */
	private JLabel left = new JLabel();
	private JScrollPane jspChat;
	/*聊天内容*/
	private JTextPane jpChat;
	/*要发送的内容*/
	private JTextPane jpMsg;
	JScrollPane jspMsg;
	/* 插入文字样式就靠它了*/
	private StyledDocument docChat = null;
	private StyledDocument docMsg = null; 

	private JButton btnSend;
	/*好友的ip*/
	/*private String friendIP;*/
	/*好友接收消息的端口*/
	/*private int friendPort;*/
	/*字体名称;字号大小;文字颜色*/
	private JComboBox fontName = null, fontSize = null,fontColor = null; 
	/*插入按钮;清除按钮;插入图片按钮*/
	private JButton b_pic = null;
	 
	/*表情框*/
	private PicsJWindow picWindow;
	private List<PicInfo> myPicInfo = new LinkedList<PicInfo>();
	private List<PicInfo> receivdPicInfo = new LinkedList<PicInfo>();
	class PicInfo{
		/* 图片信息*/
		int pos;
		String val;
		public PicInfo(int pos,String val){
			this.pos = pos;
			this.val = val;
		}
		public int getPos() {
			return pos;
		}
		public void setPos(int pos) {
			this.pos = pos;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		
	}
	
	public JTextPane getJpChat(){
		return jpChat;
	}
	
	public JButton getPicBtn(){
		return b_pic;
	}
	private CommunicationPanel parentPanel;
	public ChatPanel(CommunicationPanel parentPanelx) {
		this.parentPanel = parentPanelx;
		init();
	}
	/**
	 * 插入图片
	 * 
	 * @param icon
	 */
	public void insertSendPic(ImageIcon imgIc) {
		jpMsg.insertIcon(imgIc); // 插入图片
		System.out.print(imgIc.toString());
	}
	/*
	 * 重组收到的表情信息串
	 */
	public void receivedPicInfo(String picInfos){
		String[] infos = picInfos.split("#####");
		for(int i = 0 ; i < infos.length ; i++){
			String[] tem = infos[i].split("\\$\\$\\$\\$\\$");
			if(tem.length==2){
				PicInfo pic = new PicInfo(Integer.parseInt(tem[0]),tem[1]);
				receivdPicInfo.add(pic);
			}
		}
	}
	/**
	 * 重组发送的表情信息
	 * @return 重组后的信息串  格式为   位置|代号+位置|代号+……
	 */
	private String buildPicInfo(){
		StringBuilder sb = new StringBuilder("");
		//遍历jtextpane找出所有的图片信息封装成指定格式
		  for(int i = 0; i < this.jpMsg.getText().length(); i++){ 
              if(docMsg.getCharacterElement(i).getName().equals("icon")){
            	  Icon icon = StyleConstants.getIcon(jpMsg.getStyledDocument().getCharacterElement(i).getAttributes());
            	  ChatPic cupic = (ChatPic)icon;
            	  PicInfo picInfo= new PicInfo(i,cupic.getIm()+"");
            	  myPicInfo.add(picInfo);
            	  sb.append(i+"$$$$$"+cupic.getIm()+"#####");
             } 
          }
		  System.out.println(sb.toString());
		  return sb.toString();
	}
	
	/**
	 * 初始化窗体
	 */
	private void init() {
		setLayout(new BorderLayout());
		setBackground(Color.GRAY);
		this.addComponentListener(new ComponentAdapter(){

			@Override
			public void componentResized(ComponentEvent e) {
				ChatPanel.this.picWindow.dispose();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				ChatPanel.this.picWindow.dispose();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				ChatPanel.this.picWindow.dispose();
			}
			
		});
		/*聊天信息框*/
		jpChat = new JTextPane();
		jpChat.setEditable(false);
		jpChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_F5){//重新请求
					pagePanel.refreshPage();
				}
			}
		});
		jspChat = new JScrollPane(jpChat,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/*用户聊天信息输入框*/
		jpMsg = new JTextPane();
		jpMsg.setFont(new Font("宋体", Font.PLAIN, 16));
		jpMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER){
					sendMsg();
				}
			}
		});
		jspMsg = new JScrollPane(jpMsg,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		jspMsg.setPreferredSize(new Dimension(100, 100));

		/*发送按钮*/
		btnSend = new JButton("发送");
		btnSend.addMouseListener(this);
		btnSend.setFocusable(false);
		/*获得JTextPane的Document用于设置字体*/
		docChat = jpChat.getStyledDocument();
		docMsg = jpMsg.getStyledDocument();

		/*字体区*/
		JLabel lblSend = new JLabel();
		lblSend.setLayout(new FlowLayout(FlowLayout.RIGHT));
		String[] str_name = { "宋体", "黑体", "Dialog", "Gulim" };
		String[] str_Size = { "12", "14", "18", "22", "30", "40" };
		//String[] str_Style = { "常规", "斜体", "粗体", "粗斜体" };
		String[] str_Color = { "黑色", "红色", "蓝色", "黄色", "绿色" };
		fontName = new JComboBox(str_name);
		fontSize = new JComboBox(str_Size);
		fontColor = new JComboBox(str_Color);
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		box.add(new JLabel("字体:"));
		box.add(fontName);

		box.add(Box.createHorizontalStrut(3));
		box.add(new JLabel("字号:"));
		box.add(fontSize);
		box.add(Box.createHorizontalStrut(3));
		box.add(new JLabel("颜色:"));
		box.add(fontColor);
		box.add(Box.createHorizontalStrut(3));
		box.add(btnSend);

		JPanel PaneLeftSouth = new JPanel();
		PaneLeftSouth.setLayout(new BorderLayout());

		b_pic = new JButton("表情");
		b_pic.addMouseListener(this);
		b_pic.setFocusable(false);
		picWindow = new PicsJWindow(this);
		 
		Box box_1 = Box.createHorizontalBox();
		pagePanel = new MyPagePanel(parentPanel, this);
		pagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		pagePanel.setOpaque(false);
		box_1.add(b_pic);
		box_1.add(pagePanel);
		
		PaneLeftSouth.add(box_1, BorderLayout.NORTH);//字体、表情、震动
		PaneLeftSouth.add(jspMsg, BorderLayout.CENTER);
		PaneLeftSouth.add(box, BorderLayout.SOUTH);
		left.setLayout(new BorderLayout());
		left.setOpaque(false);
		PaneLeftSouth.setBackground(Color.CYAN);
		left.add(jspChat, BorderLayout.CENTER);
		left.add(PaneLeftSouth, BorderLayout.SOUTH);
		add(left, BorderLayout.CENTER);
	}
	 
	/**
	 * 发送消息
	 */
	private FontAndText myFont = null;
	public void sendMsg() {
		if(JavaRequest.problemName == null){
			JOptionPane.showMessageDialog(null, "请选择题目，然后重新发送消息!", "温馨提示",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(jpMsg.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, "输入内容不能为空!", "温馨提示",JOptionPane.WARNING_MESSAGE);
			return;
		}
		myFont = getFontAttrib();
		String uname = JavaRequest.username;
		String userMsg = uname + " " + sf.format(new Date());
		
		String curMsg = userMsg + "*****" + myFont.toString();// 将要想服务器发送的信息
		JSONObject jsono = new JSONObject();
		jsono.put("username", JavaRequest.username);
		jsono.put("problemName", JavaRequest.problemName);
		jsono.put("content", curMsg);
		jsono.put("pageSize", MyPagePanel.PAGE_SIZE);
		String data = JavaRequest.sendPost("addComment", jsono);
		JSONObject result = JSONObject.fromObject(data);
		if((Boolean) result.get("success")){//像消息 框发送 刚刚发送的消息
			//然后page跳转到组后一页，消息内容会在最后一页出现
			JSONObject message = JSONObject.fromObject(result.get("message"));
			Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
			classMap.put("content", MyMessage.class);
			MyPage myPage = (MyPage) JSONObject.toBean(message, MyPage.class, classMap);
			pagePanel.requestPage(myPage.getTotalPages()-1);//请求最后一页
			jpMsg.setText("");
		} else {
			JOptionPane.showMessageDialog(null, result.getString("message"), "温馨提示",JOptionPane.WARNING_MESSAGE);
			if(result.get("returnLogin") != null && (Boolean)result.get("returnLogin")){//如果用户登录超时返回用户登陆界面
				parentPanel.switchPanel(CommunicationPanel.LOGIN_PANEL);
			}
		}
	}
	/**
	 * 追加新消息到聊天窗体
	 */
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	FontAndText dateFont = new FontAndText("","宋体", 20, Color.BLUE);
	public void addMsg(String userMsg, int alignment) {
		dateFont.setText(userMsg);
		insert(dateFont, alignment);
		pos2 = jpChat.getCaretPosition();
		myFont.setText(jpMsg.getText());
		insert(myFont, alignment);
		insertPics(false);
		jpMsg.setText("");
	}
	
	public void addRecMsg(String content){
		try {
			int alignment = StyleConstants.ALIGN_LEFT;
			String userMsg = content.substring(0, content.indexOf("*****"));//获取用户信息，和时间信息
			if(userMsg.split(" ")[0].equals(JavaRequest.username)){//如果是当前用户
				alignment = StyleConstants.ALIGN_RIGHT;
			}
			String message = content.substring(content.indexOf("*****")+5);
			dateFont.setText(userMsg);
			insert(dateFont, alignment);/*时间和用户信息*/
			int index = message.lastIndexOf("*****");
			
			/*很重要的，记录下聊天区域要插入聊天消息的开始位置，*/
			pos1 = jpChat.getCaretPosition();
			FontAndText attr = getRecivedFont(message.substring(0,index));
			insert(attr, alignment);
			if(index>0 && index < message.length()-1){/*存在表情信息*/
				receivedPicInfo(message.substring(index+5));
				insertPics(true);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "消息解析出错!", "温馨提示",JOptionPane.WARNING_MESSAGE);
		}
	}
	/**
	 * 将收到的消息转化为自定义的字体类对象
	 * @param message 收到的聊天信息
	 * @return  字体类对象
	 */
	private FontAndText getRecivedFont(String message){
		String[] msgs = message.split("[|]");
		String fontName = "";
		int fontSize = 0;
		String[] color;
		String text = message;
		Color fontC = new Color(222,222,222);
		if(msgs.length>=4){/*这里简单处理，表示存在字体信息*/
			fontName = msgs[0];
			fontSize = Integer.parseInt(msgs[1]);
			color = msgs[2].split("[-]");
			if(color.length==3){
				int r = Integer.parseInt(color[0]);
				int g = Integer.parseInt(color[1]);
				int b = Integer.parseInt(color[2]);
				fontC = new Color(r,g,b);
			}
			text = "";
			for(int i = 3; i < msgs.length ; i++){
				text = text + msgs[i];
			}
		}
		FontAndText attr = new FontAndText();
		
		attr.setName(fontName);
		attr.setSize(fontSize);
		attr.setColor(fontC);
		
		attr.setText(text);
		
		System.out.println("getRecivedFont(String message):"+attr.toString());
		return attr;
	}
	/**
	 * 插入图片
	 * 
	 * @param isFriend 是否为朋友发过来的消息
	 */
	int pos1;
	int pos2;
	private void insertPics(boolean isFriend) {
		if(isFriend){
			if(this.receivdPicInfo.size()<=0){
				return;
			}else{
				for(int i = 0 ; i < receivdPicInfo.size() ; i++){
					PicInfo pic = receivdPicInfo.get(i);
					String fileName;
					jpChat.setCaretPosition(pos1+pic.getPos()); /*设置插入位置*/
		            fileName= "qqImage/"+pic.getVal()+".gif";/*修改图片路径*/ 
					jpChat.insertIcon(new  ImageIcon(PicsJWindow.class.getResource(fileName))); /*插入图片*/
				}
				receivdPicInfo.clear();
			}
		} else {
			if(myPicInfo.size()<=0) {
				return;
			} else {
				for(int i = 0 ; i < myPicInfo.size() ; i++){
					PicInfo pic = myPicInfo.get(i);
					jpChat.setCaretPosition(pos2+pic.getPos()); /*设置插入位置*/
					String fileName;
		            fileName= "qqImage/"+pic.getVal()+".gif";/*修改图片路径*/ 
					jpChat.insertIcon(new  ImageIcon(PicsJWindow.class.getResource(fileName))); /*插入图片*/
				}
				myPicInfo.clear();
			}
		}
		jpChat.setCaretPosition(docChat.getLength()); /*设置滚动到最下边*/
	}
	/**
	 * 将带格式的文本插入JTextPane
	 * 
	 * @param attrib
	 * @param alignment 对齐方式
	 */
	
	private void insert(FontAndText attrib, int alignment) {
		try { // 插入文本
			int lenBeforeInsert = docChat.getLength();
			docChat.insertString(docChat.getLength(), attrib.getText() + "\n", attrib.getAttrSet());
			//设置当前这一段的位置
			SimpleAttributeSet posAttrSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(posAttrSet, alignment);
			docChat.setParagraphAttributes(lenBeforeInsert, docChat.getLength(), posAttrSet, true);
			jpChat.setCaretPosition(docChat.getLength()); // 设置滚动到最下边
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public MyPagePanel getPagePanel(){
		return pagePanel;
	}
	
	/**
	 *字体属性辅助类
	 */
	class FontAndText{
		public static final int GENERAL = 0; // 常规
		private String msg = "", name = "宋体"; // 要输入的文本和字体名称

		private int size = 0; //字号

		private Color color = new Color(225,225,225); // 文字颜色

		private SimpleAttributeSet attrSet = null; // 属性集
		/**
		 * 一个空的构造（可当做换行使用）
		 */
		
		public FontAndText() {
		}
		public FontAndText(String msg,String fontName,int fontSize,Color color){
			this.msg = msg;
			this.name = fontName;
			this.size = fontSize;
			this.color = color;
		}

		/**
		 * 返回属性集
		 * 
		 * @return
		 */
		public SimpleAttributeSet getAttrSet() {
			attrSet = new SimpleAttributeSet();
			if (name != null){
				StyleConstants.setFontFamily(attrSet, name);
			}
			StyleConstants.setBold(attrSet, false);
			StyleConstants.setItalic(attrSet, false);
			StyleConstants.setFontSize(attrSet, size);
			if (color != null)
				StyleConstants.setForeground(attrSet, color);
			return attrSet;
		}
		public String toString(){
			//将消息分为四块便于在网络上传播
			return name+"|"
			+size+"|"
			+color.getRed()+"-"+color.getGreen()+"-"+color.getBlue()+"|"
			+msg;
		}
		
		public String getText() {
			return msg;
		}

		public void setText(String text) {
			this.msg = text;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}
	}
	/**
	 * 获取所需要的文字设置
	 * 
	 * @return FontAttrib
	 */
	private FontAndText getFontAttrib() {
		FontAndText att = new FontAndText();
		att.setText(jpMsg.getText()+"*****"+buildPicInfo());//文本和表情信息
		att.setName((String) fontName.getSelectedItem());
		att.setSize(Integer.parseInt((String) fontSize.getSelectedItem()));
		String temp_color = (String) fontColor.getSelectedItem();
		if (temp_color.equals("黑色")) {
			att.setColor(new Color(0, 0, 0));
		} else if (temp_color.equals("红色")) {
			att.setColor(new Color(255, 0, 0));
		} else if (temp_color.equals("蓝色")) {
			att.setColor(new Color(0, 0, 255));
		} else if (temp_color.equals("黄色")) {
			att.setColor(new Color(255, 255, 0));
		} else if (temp_color.equals("绿色")) {
			att.setColor(new Color(0, 255, 0));
		}
		return att;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		picWindow.setVisible(false);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (getY() <= 0) {
			setLocation(getX(), 0);
		}
		if (e.getButton() != 1)
			return;/*不是左键*/

		JComponent source = (JComponent) e.getSource();
		/*鼠标释放时在事件源内,才响应单击事件*/
		if (e.getX() >= 0 && e.getX() <= source.getWidth() && e.getY() >= 0
				&& e.getY() <= source.getHeight()) {
			if (source == btnSend){
				sendMsg();
			} else if (source == this.b_pic){
				picWindow.setVisible(true);
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}