package com.ds.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ds.controler.TreeMenuControler;
import com.ds.panel.EditCodePane.NewAction;
import com.ds.tools.CompileAndRunJavaFile;
import com.ds.tools.MyTimer;

import sun.font.FontDesignMetrics;

//����༭ �Լ� ��Ŀ��� �����
public class EditCodeAndBrowse extends JPanel{
	private BrowsePane browsePane = new BrowsePane();
	private EditCodePane editCodePane = new EditCodePane();
	private JTextPane resultPane = new JTextPane();
	private JTextPane rightDataInputPane = new JTextPane();
	private JTextPane rightDataOuputPane = new JTextPane();
	private String selectTest = null;
	public  EditCodeAndBrowse(){
		setLayout(new GridLayout(1, 1));
		JSplitPane hSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplitePane.setContinuousLayout(true);
		hSplitePane.setOneTouchExpandable(true);
		hSplitePane.setDividerSize(10);
		hSplitePane.setResizeWeight(0.5);
		
		JSplitPane vSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplitePane.setContinuousLayout(true);
		vSplitePane.setOneTouchExpandable(true);
		vSplitePane.setDividerSize(10);
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
		resultPane.setEditable(false);
		rightDataInputPane.setEditable(false);
		rightDataOuputPane.setEditable(false);
		tabPane.addTab("���н��", null, new JScrollPane(resultPane), "Run Result");
		tabPane.addTab("��������", null, new JScrollPane(rightDataInputPane), "Input Data");
		tabPane.addTab("�������", null, new JScrollPane(rightDataOuputPane), "Out Data");
		vSplitePane.setTopComponent(browsePane);
		vSplitePane.setBottomComponent(tabPane);
		vSplitePane.setResizeWeight(0.85);
		
		hSplitePane.setLeftComponent(vSplitePane);
		hSplitePane.setRightComponent(editCodePane);
		//�ύ��ť �ļ����¼�
		editCodePane.getSubmitBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectTest == null) return;
				//�õ�Դ�������
				String code = editCodePane.getTextPane().getText();
				//ʵ����������
				CompileAndRunJavaFile cr = new CompileAndRunJavaFile();
				cr.compileAndRunJavaFile(code, selectTest);
				if(cr.isCompileAndRunOK()) {
					resultPane.setText("����ʱ��: " + cr.getUseTime() + "ms\n" +
							"�ڴ�ʹ��: " + cr.getUseMemory() + "kb\n" +
							"���н��: " + cr.getOutMsg());
				} else if(cr.isCompilerError()) {
					resultPane.setText("�������: " + cr.getCE());
				} else if(cr.isRunningError()) {
					resultPane.setText("���д���: " + cr.getError());
				}  
			}
		});
		
		//���Ϊ ͼ�β˵��� �Ҳ���hSplitePane
		JSplitPane hxSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hxSplitePane.setContinuousLayout(true);
		hxSplitePane.setOneTouchExpandable(true);
		hxSplitePane.setDividerSize(10);
		hxSplitePane.setResizeWeight(0.02);
		//���β˵�
		JTree menuTree = TreeMenuControler.getTreeMenu();
		hxSplitePane.setLeftComponent(new JScrollPane(menuTree));
		hxSplitePane.setRightComponent(hSplitePane);
		//���ѡ�������
		menuTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				JTree treeRoot = (JTree)event.getSource(); 
				DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)treeRoot.getLastSelectedPathComponent();
				//�����Ҷ�ӽڵ�
				if(selectionNode.getChildCount() == 0){
					try {
						ReaderXML readerXml = new ReaderXML();
						ReaderText rederText = new ReaderText();
						String str = readerXml.read("./xmlTemplate/htmlTemplate.xml");
						//ȷ����ǰѡ�����Ŀ
						selectTest = new TreePath(selectionNode.getPath()).toString();
						Object[] args = rederText.read("./tests/" + selectTest + ".txt");
						String content = MessageFormat.format(str, args);
						browsePane.getTextPane().setText(content);
						
						//��ȡ��ȷ������������������
						BufferedReader br = new BufferedReader(new FileReader(new File("./outs/" + selectTest + ".txt")));
				    	String msg = null;
				    	StringBuilder sb = new StringBuilder();
				    	while((msg = br.readLine()) != null)
				    		sb.append(msg).append("\n");
				    	br.close();
				    	rightDataOuputPane.setText(sb.toString());
				    	sb.delete(0, sb.length());
				    	
				    	br = new BufferedReader(new FileReader(new File("./inputs/" + selectTest + ".txt")));
				    	msg = null;
				    	while((msg = br.readLine()) != null)
				    		sb.append(msg).append("\n");
				    	br.close();
				    	rightDataInputPane.setText(sb.toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		add(hxSplitePane);
	}
}

class BrowsePane extends JPanel{
	private JTextPane textPane = new JTextPane(); 
	
	public JTextPane getTextPane() {
		return textPane;
	}

	public BrowsePane(){
		setLayout(new GridLayout(1, 1));
		textPane.setBackground(Color.WHITE);
		textPane.setEditable(false);
		textPane.setEditorKit(new HTMLEditorKit());
		textPane.setContentType("text/html");
		add(new JScrollPane(textPane));
	}
	
}

class ReaderText{
	public Object[] read(String path){
		Object[] objs = getInsertContents(path).toArray(new Object[]{});
		return objs;
	}
	
	private List<String> getInsertContents(String path){
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String content = null;
			StringBuilder sb = new StringBuilder();
			while((content = br.readLine()) != null){
				if("".equals(content)) { //�հ���
					list.add(sb.toString());
					sb.delete(0, sb.length());
				} else {
					if(sb.length() > 0)
						sb.append("\n");
					sb.append(content);
				}
			}
			if(sb.length() > 0)
				list.add(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

class ReaderXML {
    public String read(String path){
        String str=null;
        str=reader(path);   
        return str;
    }    
    private String reader(String path){        
        SAXReader reader=new SAXReader();
        String str=null;        
        try {            
            Document d=reader.read(new File(path));
            Element e=d.getRootElement();
            Element htmle=e.element("html");
            str=htmle.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return str;
    }
}

class EditCodePane extends JPanel{
	 public static final String MAX_LINE_NUM = "9999";
	 private JTextPane textPane = new JTextPane(); //�ı����񣬱༭����
	 private JLabel timeStatusBar = new JLabel(); //ʱ��״̬��
	 private JLabel caretStatusBar = new JLabel(); //���λ��״̬��
	 private JFileChooser filechooser = new JFileChooser(); //�ļ�ѡ����
	 private JPanel linePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
	 private JButton submitBtn = new JButton("�ύ����");
	 private int lineNum = 0;
	 private  MyFont myFont = null;
	 
	 public JButton getSubmitBtn(){
		 return submitBtn;
	 }
	 
	 public JTextPane getTextPane(){
		 return textPane;
	 }
	 
	 private void initTextPaneDocument(){
		 textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(final DocumentEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						adjustLinePane();
						DecorateKeyWords.decorateKeyWords(textPane, myFont);
					}
				}).start();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				new Thread(new Runnable() {  
					@Override
					public void run() {
						adjustLinePane();
						String text;
						try {
							text = textPane.getDocument().getText(0, textPane.getDocument().getLength()).replaceAll("\\r", "");
							Pattern pattern = Pattern.compile("\\n");
							Matcher matcher = pattern.matcher(text);
							int lineRow = 1;
							while(matcher.find()){
								//��������
								++lineRow;
							}
							while(lineRow < linePane.getComponentCount()) {
								--lineNum;
								linePane.remove(linePane.getComponentCount()-1);
							}
							linePane.updateUI();
							
						} catch (BadLocationException ex) {
							ex.printStackTrace();
						} finally {
							DecorateKeyWords.decorateKeyWords(textPane, myFont);
						}
					}
				}).start();
			}
		});
	 }
	 
	 public EditCodePane() { //���캯��
		 setLayout(new BorderLayout());
		 //��ʼ����
		 myFont = new MyFont();
		 myFont.setColor(Color.black);
		 myFont.setFont(new Font("����", Font.PLAIN, 24));
		 myFont.setSizeIndex(19);
		 myFont.setStyleIndex(0);
		 myFont.setColorIndex(0);
		 
		 Action[] actions =  //Action����,���ֲ�������
	     {
		    new NewAction(),
		    new OpenAction(),
		    new SaveAction(),
		    new CutAction(),
		    new CopyAction(),
		    new PasteAction(),
		    new NewFontStyle(),
		    new AboutAction()
		  };
		 textPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					//����µ��к�
					addLineNum();
				}
			}
		 });
		 
		 textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				 try {
					String text = textPane.getDocument().getText(0, e.getDot()).replaceAll("\\r", "");
					Pattern pattern = Pattern.compile("\\n");
					Matcher matcher = pattern.matcher(text);
					int lineRow = 1;
					int lastLineBeginPos = -1;//��¼�ı������һ�еĿ�ʼ��λ�� 
					while(matcher.find()){
						//��������
						++lineRow;
						lastLineBeginPos = matcher.start();//�õ���һ�й�����ڵ�λ�ã�������һ�еĻ��з���
					}
					int lineCol = e.getDot() - lastLineBeginPos;
					//��ʾ�кź��к�
					caretStatusBar.setText("��� " + lineRow + " : " + lineCol);
				 } catch (BadLocationException ey) {
				 	ey.printStackTrace();
				 }
			}
		 });
		 initTextPaneDocument();

		 add(createJToolBar(actions), BorderLayout.NORTH); //���ӹ�����
		 JPanel textBackPanel = new JPanel(new BorderLayout());
		 textBackPanel.add(linePane, BorderLayout.WEST);//�����к����
		 textBackPanel.add(textPane, BorderLayout.CENTER);//�����ı����
		 add(new JScrollPane(textBackPanel), BorderLayout.CENTER); //�ı�����Ƕ�뵽JscrollPane
		 JPanel statusPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 10));
		 //����ύ��ť
		 statusPane.add(submitBtn);
		 //���������Ϣ
		 statusPane.add(caretStatusBar);
		 //���ʱ����Ϣ
		 statusPane.add(timeStatusBar);
		 
		 //��ʼ�����λ��
		 caretStatusBar.setText("��� 1 : 1");
		 //��ʼ��ϵͳʱ����ʾ
		 MyTimer.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				Date now = new Date(); 
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//���Է�����޸����ڸ�ʽ
				timeStatusBar.setText(dateFormat.format(now)); 
			}
		}, 0, 1000);
		add(statusPane, BorderLayout.SOUTH); //����״̬��
		 
		 FontMetrics fm = FontDesignMetrics.getMetrics(myFont.getFont());
		 //���ù��Ĵ�С
		 textPane.setFont(myFont.getFont());
		 //�����������Ŀ��
		 linePane.setPreferredSize(new Dimension(fm.stringWidth(MAX_LINE_NUM), 0));
		 addLineNum();
	 }
	 
	 private void addLineNum(){
		 //ΪtextPane����к�
		 String numText = String.valueOf(++lineNum);
		 int tmpNum = MAX_LINE_NUM.length() - (int)(Math.log10(lineNum*1.0)+1);
		 String spaces = "";
		 while(tmpNum > 0){
			 spaces += " ";
			 --tmpNum;
		 }
		 JLabel lineLabel = new JLabel(numText.replaceAll("(\\d+)", spaces+"$1"), JLabel.RIGHT);
		 lineLabel.setForeground(Color.GRAY);
		 lineLabel.setFont(myFont.getFont());
		 linePane.add(lineLabel);
		 linePane.updateUI();
	 }


	 private JToolBar createJToolBar(Action[] actions) { //����������
		 JToolBar toolBar = new JToolBar(); //ʵ����������
		 for (int i = 0; i < actions.length; i++) {
			 JButton bt = new JButton(actions[i]); //ʵ�����µİ�ť
			 bt.setRequestFocusEnabled(false); //���ò���Ҫ����
			 toolBar.add(bt); //���Ӱ�ť��������
		 }
		 return toolBar;  //���ع�����
	 }
	 
	 private synchronized void adjustLinePane(){
		 try {
			LineNumberReader lnr = new LineNumberReader(new StringReader(textPane.getText()));
			lnr.skip(Long.MAX_VALUE);
			int newLineNum = lnr.getLineNumber()+1;
			  lnr.close();
			  if(lineNum < newLineNum){
				  while(lineNum < newLineNum)
					  addLineNum();
			  } else {
				  while(lineNum > newLineNum && lineNum > 1){
					  linePane.remove(linePane.getComponentCount()-1);
					  --lineNum;
				  }
				  linePane.updateUI();
			  }
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 class NewFontStyle extends AbstractAction{
		public NewFontStyle() {
			 super("����");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFontChooser one = new JFontChooser(myFont);
	        MyFont tmpFont = one.showDialog(null, "����ѡ����", textPane.getLocationOnScreen().x, textPane.getLocationOnScreen().y); 
	        if(tmpFont == null) return;
	        myFont = tmpFont;
	        //�������� textPane�����壬�ı���Ĵ�С
	        textPane.setFont(myFont.getFont());
	        FontMetrics fm = FontDesignMetrics.getMetrics(myFont.getFont());
	        //�������������������Ŀ��
	        linePane.setPreferredSize(new Dimension(fm.stringWidth(MAX_LINE_NUM), 0));
	        //���������кŵ�����
	        for(int i=0; i < linePane.getComponentCount(); ++i)
	        	linePane.getComponent(i).setFont(myFont.getFont());
	        
	        StyledDocument doc = textPane.getStyledDocument();  
		  	SimpleAttributeSet wordAttr = new SimpleAttributeSet(); 
		  	DecorateKeyWords.decorateStyleConstants(wordAttr, myFont.getFont());
		  	doc.setCharacterAttributes(0, doc.getLength(), wordAttr, false);
		}
	 }

	 class NewAction extends AbstractAction { //�½��ļ�����
		 public NewAction() {
			 super("�½�");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.setDocument(new DefaultStyledDocument()); //����ĵ�
			 while(linePane.getComponentCount() > 1)
				 linePane.remove(linePane.getComponent(linePane.getComponentCount()-1));
			 linePane.updateUI();
			 lineNum = 1;
			 initTextPaneDocument();
		 }
	 }

	 class OpenAction extends AbstractAction { //���ļ�����
		  public OpenAction() {
			  super("��");
		  }
		  public void actionPerformed(ActionEvent e) {
			  int i = filechooser.showOpenDialog(EditCodePane.this); //��ʾ���ļ��Ի���
			  if (i == JFileChooser.APPROVE_OPTION) { //����Ի����д�ѡ��
				  File f = filechooser.getSelectedFile(); //�õ�ѡ����ļ�
				  try {
					  InputStream is = new FileInputStream(f); //�õ��ļ�������
					  textPane.read(is, "d"); //�����ļ����ı�����
					  is.close();
					  adjustLinePane();
					  
				  } catch (Exception ex) {
					  ex.printStackTrace();  //���������Ϣ
				  }
			  }
			  DecorateKeyWords.decorateKeyWords(textPane, myFont);
			  initTextPaneDocument();
		  }
	 }

	 class SaveAction extends AbstractAction {  //��������
		 public SaveAction() {
			 super("����");
		 }
		 public void actionPerformed(ActionEvent e) {
			 int i = filechooser.showSaveDialog(EditCodePane.this); //��ʾ�����ļ��Ի���
			 if (i == JFileChooser.APPROVE_OPTION) {  //����Ի����б��水ť
				 File f = filechooser.getSelectedFile(); //�õ�ѡ����ļ�
				 try {
					 FileOutputStream out = new FileOutputStream(f);  //�õ��ļ������
					 out.write(textPane.getText().getBytes()); //д���ļ�    
				 } catch (Exception ex) {
					 ex.printStackTrace(); //���������Ϣ
				 }
			 }
		 }
	 }

	 class CutAction extends AbstractAction {  //��������
	 	 public CutAction() {
	 		 super("����");
	 	 }
	 	 public void actionPerformed(ActionEvent e) {
	 		 textPane.cut();  //�����ı�����ļ�������
	 		 adjustLinePane();
	 	 }
	 }

	 class CopyAction extends AbstractAction {  //��������
		 public CopyAction() {
			 super("����");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.copy();  //�����ı�����Ŀ�������
			 adjustLinePane();
		 }
	 }

	 class PasteAction extends AbstractAction {  //ճ������
		 public PasteAction() {
			 super("ճ��");
		 }
		 public void actionPerformed(ActionEvent e) {
			 textPane.paste();  //�����ı������ճ������
		 }
	 }

	 class AboutAction extends AbstractAction { //����ѡ������
		 public AboutAction() {
			 super("����");
		 }
		 public void actionPerformed(ActionEvent e) {
			 JOptionPane.showMessageDialog(EditCodePane.this, "�򵥵��ı��༭����ʾ"); //��ʾ�����Ϣ
		 }
	 }
}

class WordNode {  
	 private int location;  
	 private String word;  
	 public WordNode(int location, String str) {  
		 super();  
		 this.location = location;  
		 this.word = str;  
	 }  
	 public int getLocation() {  
		 return location;  
	 }  
	 public String getWord() {  
		 return word;  
	 }  
}  

class DecorateKeyWords{
	//java�еĹؼ���
	private static final String KEYWORDS[]={"abstract","assert","boolen","break","byte","case","catch","char","class","const",  
        "continue","default","do","double","else","enum","extends","final","finally","float","for",  
        "if","implements","import","instanceof","int","interface","long","native","new","package",  
        "private","protected","public","return","short","static","strictfp","super","switch","synchrpnized",  
        "this","throw","throws","transient","try","void","volatile","while"
   };  
    // ׼���ؼ���     
	private static HashSet<String> keywords = new HashSet<String>(); 
	
	public static void decorateStyleConstants(SimpleAttributeSet attr, Font font){
		StyleConstants.setFontFamily(attr, font.getFamily());
	    StyleConstants.setFontSize(attr, font.getSize());
	    switch(font.getStyle()) {
	    	case Font.BOLD :
	    		StyleConstants.setBold(attr, true);
	    		break;
	    	case Font.ITALIC :
	    		StyleConstants.setItalic(attr, true);
	    		break;
	    	case Font.PLAIN :
	    		StyleConstants.setItalic(attr, false);
	    		StyleConstants.setBold(attr, false);
	    		break;
	    	case Font.BOLD | Font.ITALIC :
	    		StyleConstants.setItalic(attr, true);
   			StyleConstants.setBold(attr, true);
	    		break;
	    	default :
	    		break;
	    }
	}
	
	public static void decorateKeyWords(JTextPane tp, MyFont myFont) {  
	  //��ʼ���ؼ���
	  for(int i = 0; i<KEYWORDS.length; i++)  
		 keywords.add(KEYWORDS[i]);
	  // �����йؼ��ʽ���������ɫ  
	  String text = tp.getText().replaceAll("\\r", "");  
	  StyledDocument doc = tp.getStyledDocument();  
	  SimpleAttributeSet keyWordAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(keyWordAttr, Color.cyan);
	  decorateStyleConstants(keyWordAttr, myFont.getFont());
	  SimpleAttributeSet otherWordAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(otherWordAttr, myFont.getColor());
	  decorateStyleConstants(otherWordAttr, myFont.getFont());
	  ListIterator<WordNode> iterator1 = split(text, "\\s|\\{|\\}|\\(|\\)|\\<|\\>|\\.|\\n");  
	  while (iterator1.hasNext()) {  
		  WordNode wn = iterator1.next(); 
		  if (keywords.contains(wn.getWord())) { 
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord().length(), keyWordAttr, true);  
		  } else {
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord().length(), otherWordAttr, true);
		  }
	  }  
	  // ��ע���н������β�ͬ����ɫ  
	  SimpleAttributeSet annotationAttr = new SimpleAttributeSet();  
	  StyleConstants.setForeground(annotationAttr, Color.green); 
	  decorateStyleConstants(annotationAttr, myFont.getFont());
	  ListIterator<WordNode> iterator2 = split(text, "\\n");  
	  boolean exegesis = false; // �Ƿ����/*��ע��  
	  while (iterator2.hasNext()) {  
		  WordNode wn = iterator2.next();  
		  if (wn.getWord().startsWith("//")) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (wn.getWord().startsWith("/*")  && wn.getWord().endsWith("*/")) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (wn.getWord().startsWith("/*")  && !wn.getWord().endsWith("*/")) {  
			  exegesis = true;  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  } else if (!wn.getWord().startsWith("/*") && wn.getWord().endsWith("*/") && true == exegesis) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
			  exegesis = false;  
		  } else if (true == exegesis) {  
			  doc.setCharacterAttributes(wn.getLocation(), wn.getWord()  
					  .length(), annotationAttr, true);  
		  }  
	  	}  
	 }  
	 
	 /**  
	   * ����ָ���Ķ���ַ������ַ����ָ�确 ����,����  
	  * @param str   
	  *            ���ָ���ַ���  
	  * @param regexs  
	  *            Ҫ�ָ�����ķ���  
	  * @return ����WordNodeList�����iterator  
	  */  
	 private static ListIterator<WordNode> split(String str,String regex) {  
		 Pattern p = Pattern.compile(regex);  
		 Matcher m = p.matcher(str); 
		 List<WordNode> nodeList = new ArrayList<WordNode>();  
		 int strStart = 0; // �ָ�ʵ���λ��  
		 String s; // �ָ�ĵ���  
		 WordNode wn; // StringNode�ڵ�  
		 while (m.find()) {  
			 s = str.substring(strStart, m.start());  
			 if (!s.equals(new String())) {  
				 wn = new WordNode(strStart, s);  
				 nodeList.add(wn);  
			 }  
			 strStart = m.start() + 1;  
		 }  
		 s = str.substring(strStart, str.length());  
		 wn = new WordNode(strStart, s);  
		 nodeList.add(wn);  
		 return nodeList.listIterator();  
	 }  
}