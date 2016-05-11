package com.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * ���촰����
 * @author yangxing zou
 * @version 0.1
 */
public class ChatFrame extends JFrame implements MouseListener{
	private static final long serialVersionUID = 1L;
	public final int fwidth = 550;
	public final int fheight = 500;
	/* ��ߺ��ұ�Ҫ��ʾ�Ľ��� */
	public JLabel left = new JLabel();
	JScrollPane jspChat;
	/*��������*/
	private JTextPane jpChat;
	/*Ҫ���͵�����*/
	private JTextPane jpMsg;
	JScrollPane jspMsg;
	/* ����������ʽ�Ϳ�����*/
	private StyledDocument docChat = null;
	private StyledDocument docMsg = null; 

	private JButton btnSend;
	/*���ѵ�ip*/
	/*private String friendIP;*/
	/*���ѽ�����Ϣ�Ķ˿�*/
	/*private int friendPort;*/
	/*��������;�ֺŴ�С;������ɫ*/
	private JComboBox fontName = null, fontSize = null,fontColor = null; 
	/*���밴ť;�����ť;����ͼƬ��ť*/
	private JButton b_shake=null,b_pic, b_remove = null;
	private static final Color TIP_COLOR = new Color(255, 255, 225);
	 
	/*�����*/
	private PicsJWindow picWindow;
	private List<PicInfo> myPicInfo = new LinkedList<PicInfo>();
	private List<PicInfo> receivdPicInfo = new LinkedList<PicInfo>();
	class PicInfo{
		/* ͼƬ��Ϣ*/
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
	public JButton getPicBtn(){
		return b_pic;
	}
	public ChatFrame() {
		init();
	}
	/**
	 * ����ͼƬ
	 * 
	 * @param icon
	 */
	public void insertSendPic(ImageIcon imgIc) {
		jpMsg.insertIcon(imgIc); // ����ͼƬ
		System.out.print(imgIc.toString());
	}
	/*
	 * �����յ��ı�����Ϣ��
	 */
	public void receivedPicInfo(String picInfos){
		String[] infos = picInfos.split("[+]");
		for(int i = 0 ; i < infos.length ; i++){
			String[] tem = infos[i].split("[&]");
			if(tem.length==2){
				PicInfo pic = new PicInfo(Integer.parseInt(tem[0]),tem[1]);
				receivdPicInfo.add(pic);
			}
		}
	}
	/**
	 * ���鷢�͵ı�����Ϣ
	 * @return ��������Ϣ��  ��ʽΪ   λ��|����+λ��|����+����
	 */
	private String buildPicInfo(){
		StringBuilder sb = new StringBuilder("");
		//����jtextpane�ҳ����е�ͼƬ��Ϣ��װ��ָ����ʽ
		  for(int i = 0; i < this.jpMsg.getText().length(); i++){ 
              if(docMsg.getCharacterElement(i).getName().equals("icon")){
            	  //ChatPic = (ChatPic)
            	  Icon icon = StyleConstants.getIcon(jpMsg.getStyledDocument().getCharacterElement(i).getAttributes());
            	  ChatPic cupic = (ChatPic)icon;
            	  PicInfo picInfo= new PicInfo(i,cupic.getIm()+"");
            	  myPicInfo.add(picInfo);
            	  sb.append(i+"&"+cupic.getIm()+"+");
             } 
          }
		  System.out.println(sb.toString());
		  return sb.toString();
		  //return null;
	}
	
	/**
	 * ��ʼ������
	 */
	private void init() {
		setLayout(new BorderLayout());
		setSize(fwidth, fheight);
		this.setMinimumSize(new Dimension(fwidth, fheight));
		this.getContentPane().setBackground(Color.GRAY);
		setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
			
		});
		this.addComponentListener(new ComponentAdapter(){

			@Override
			public void componentResized(ComponentEvent e) {
				ChatFrame.this.picWindow.dispose();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				ChatFrame.this.picWindow.dispose();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				ChatFrame.this.picWindow.dispose();
			}
			
		});
		/*����ǰ��*/
		setAlwaysOnTop(true); 
		/*������Ϣ��*/
		jpChat = new JTextPane();
		jpChat.setEditable(false);
		jspChat = new JScrollPane(jpChat,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/*�û�������Ϣ�����*/
		jpMsg = new JTextPane();
		jspMsg = new JScrollPane(jpMsg,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		jspMsg.setPreferredSize(new Dimension(100, 100));

		/*���Ͱ�ť*/
		btnSend = new JButton("����");
		btnSend.addMouseListener(this);
		btnSend.setFocusable(false);
		/*���JTextPane��Document������������*/
		docChat = jpChat.getStyledDocument();
		docMsg = jpMsg.getStyledDocument();

		/*������*/
		JLabel lblSend = new JLabel();
		lblSend.setLayout(new FlowLayout(FlowLayout.RIGHT));
		String[] str_name = { "����", "����", "Dialog", "Gulim" };
		String[] str_Size = { "12", "14", "18", "22", "30", "40" };
		//String[] str_Style = { "����", "б��", "����", "��б��" };
		String[] str_Color = { "��ɫ", "��ɫ", "��ɫ", "��ɫ", "��ɫ" };
		fontName = new JComboBox(str_name);
		fontSize = new JComboBox(str_Size);
		fontColor = new JComboBox(str_Color);
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		box.add(new JLabel("����:"));
		box.add(fontName);

		box.add(Box.createHorizontalStrut(3));
		box.add(new JLabel("�ֺ�:"));
		box.add(fontSize);
		box.add(Box.createHorizontalStrut(3));
		box.add(new JLabel("��ɫ:"));
		box.add(fontColor);
		box.add(Box.createHorizontalStrut(3));
		box.add(btnSend);

		JPanel PaneLeftSouth = new JPanel();
		PaneLeftSouth.setLayout(new BorderLayout());

		b_pic = new JButton("����");
		b_pic.addMouseListener(this);
		b_pic.setFocusable(false);
		b_shake = new JButton("��");
		b_shake.setFocusable(false);
		b_remove = new JButton("���");
		b_remove.setFocusable(false);
		picWindow = new PicsJWindow(this);
		 
		Box box_1 = Box.createHorizontalBox();
		box_1.add(b_pic);
		box_1.add(b_shake);
		box_1.add(b_remove);
		
		PaneLeftSouth.add(box_1, BorderLayout.NORTH);//���塢���顢��
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
	 * ������Ϣ
	 */
	private FontAndText myFont = null;
	boolean flag = true;
	public void sendMsg() {
		if(flag)
			addMsg("hjzgg", StyleConstants.ALIGN_RIGHT);
		else 
			addMsg("hjzgg", StyleConstants.ALIGN_LEFT);
		flag = !flag;
	}
	/**
	 * ׷������Ϣ�����촰��
	 */
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	FontAndText dateFont = new FontAndText("","����",20,Color.BLUE);
	public void addMsg(String uname, int alignment) {
		myFont = getFontAttrib();
		String msg =  uname + " " + sf.format(new Date());
		dateFont.setText(msg);
		insert(dateFont, alignment);
		pos2 = jpChat.getCaretPosition();
		myFont.setText(jpMsg.getText());
		insert(myFont, alignment);
		insertPics(false);
		jpMsg.setText("");
	}
	public void addRecMsg(String uname,String message, int alignment){
		setExtendedState(Frame.NORMAL);
		//setVisible(true);
		String msg =  uname + " " + sf.format(new Date());
		dateFont.setText(msg);
		insert(dateFont, alignment);/*ʱ����û���Ϣ*/
		int index = message.lastIndexOf("*");
		
		System.out.println("index="+index);
		/*����Ҫ�ģ���¼����������Ҫ����������Ϣ�Ŀ�ʼλ�ã�*/
		pos1 = jpChat.getCaretPosition();
		if(index>0 && index < message.length()-1){/*���ڱ�����Ϣ*/
			FontAndText attr = getRecivedFont(message.substring(0,index));
			insert(attr, alignment);
			receivedPicInfo(message.substring(index+1,message.length()));
			insertPics(true);
		}else{
			FontAndText attr = getRecivedFont(message);
			insert(attr, alignment);
		}
	}
	/**
	 * ���յ�����Ϣת��Ϊ�Զ�������������
	 * @param message �յ���������Ϣ
	 * @return  ���������
	 */
	public FontAndText getRecivedFont(String message){
		String[] msgs = message.split("[|]");
		String fontName = "";
		int fontSize = 0;
		String[] color;
		String text = message;
		Color fontC = new Color(222,222,222);
		if(msgs.length>=4){/*����򵥴�����ʾ����������Ϣ*/
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
	 * ����ͼƬ
	 * 
	 * @param isFriend �Ƿ�Ϊ���ѷ���������Ϣ
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
					jpChat.setCaretPosition(pos1+pic.getPos()); /*���ò���λ��*/
		            fileName= "qqImage/"+pic.getVal()+".gif";/*�޸�ͼƬ·��*/ 
					jpChat.insertIcon(new  ImageIcon(PicsJWindow.class.getResource(fileName))); /*����ͼƬ*/
				}
				receivdPicInfo.clear();
			}
		}else{
			
			if(myPicInfo.size()<=0){
				return;
			}else{
				for(int i = 0 ; i < myPicInfo.size() ; i++){
					PicInfo pic = myPicInfo.get(i);
					jpChat.setCaretPosition(pos2+pic.getPos()); /*���ò���λ��*/
					String fileName;
		            fileName= "qqImage/"+pic.getVal()+".gif";/*�޸�ͼƬ·��*/ 
					jpChat.insertIcon(new  ImageIcon(PicsJWindow.class.getResource(fileName))); /*����ͼƬ*/
				}
				myPicInfo.clear();
			}
		}
		jpChat.setCaretPosition(docChat.getLength()); /*���ù��������±�*/
	}
	/**
	 * ������ʽ���ı�����JTextPane
	 * 
	 * @param attrib
	 * @param alignment ���뷽ʽ
	 */
	private void insert(FontAndText attrib, int alignment) {
		try { // �����ı�
			int lenBeforeInsert = docChat.getLength();
			docChat.insertString(docChat.getLength(), attrib.getText() + "\n", attrib.getAttrSet());
			//���õ�ǰ��һ�ε�λ��
			SimpleAttributeSet posAttrSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(posAttrSet, alignment);
			docChat.setParagraphAttributes(lenBeforeInsert, docChat.getLength(), posAttrSet, true);
			jpChat.setCaretPosition(docChat.getLength()); // ���ù��������±�
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**�˷�����û�б��õ���û�и�ʽ
	 * @param text
	 * @param alignment
	 */
	private void insert(String text, int alignment) {
		try { // �����ı�
			SimpleAttributeSet posAttrSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(posAttrSet, alignment);
			docChat.setParagraphAttributes(0, docChat.getLength(), posAttrSet, false);
			docChat.insertString(docChat.getLength(), text + "\n", dateFont.getAttrSet());
			jpChat.setCaretPosition(docChat.getLength()); // ���ò���λ��
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	/**
	 *�������Ը�����
	 */
	class FontAndText{
		public static final int GENERAL = 0; // ����
		private String msg = "", name = "����"; // Ҫ������ı�����������

		private int size = 0; //�ֺ�

		private Color color = new Color(225,225,225); // ������ɫ

		private SimpleAttributeSet attrSet = null; // ���Լ�
		/**
		 * һ���յĹ��죨�ɵ�������ʹ�ã�
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
		 * �������Լ�
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
			StyleConstants.setAlignment(attrSet, StyleConstants.ALIGN_RIGHT);
			if (color != null)
				StyleConstants.setForeground(attrSet, color);
			return attrSet;
		}
		public String toString(){
			//����Ϣ��Ϊ�Ŀ�����������ϴ���
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
	 * ��ȡ����Ҫ����������
	 * 
	 * @return FontAttrib
	 */
	private FontAndText getFontAttrib() {
		FontAndText att = new FontAndText();
		att.setText(jpMsg.getText()+"*"+buildPicInfo());//�ı��ͱ�����Ϣ
		att.setName((String) fontName.getSelectedItem());
		att.setSize(Integer.parseInt((String) fontSize.getSelectedItem()));
		String temp_color = (String) fontColor.getSelectedItem();
		if (temp_color.equals("��ɫ")) {
			att.setColor(new Color(0, 0, 0));
		} else if (temp_color.equals("��ɫ")) {
			att.setColor(new Color(255, 0, 0));
		} else if (temp_color.equals("��ɫ")) {
			att.setColor(new Color(0, 0, 255));
		} else if (temp_color.equals("��ɫ")) {
			att.setColor(new Color(255, 255, 0));
		} else if (temp_color.equals("��ɫ")) {
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
			return;/*�������*/

		JComponent source = (JComponent) e.getSource();
		/*����ͷ�ʱ���¼�Դ��,����Ӧ�����¼�*/
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
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		new ChatFrame().setVisible(true);
	}
}