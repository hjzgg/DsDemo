package com.ds.panel.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

 
public class PicsJWindow  extends JWindow { 
	private static final long serialVersionUID = 1L;
	GridLayout gridLayout1 = new GridLayout(7,15); 
  JLabel[] ico=new JLabel[105]; /*放表情*/
  int  i;
  ChatPanel owner;
  String[] intro = {"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",
  		"","","","","","","","","","","","","","","",};/*图片描述*/
  public PicsJWindow(ChatPanel owner) { 
    this.owner=owner; 
    try { 
    init(); 
    this.setAlwaysOnTop(true); 
    } 
    catch (Exception exception) { 
    exception.printStackTrace(); 
    } 
  } 
  private void init() throws Exception { 
	this.setPreferredSize(new Dimension(28*15,28*7));
	JPanel p = new JPanel();
	p.setOpaque(true);
	this.setContentPane(p);
  	p.setLayout(gridLayout1);
    p.setBackground(SystemColor.text);		
		String fileName = "";
    for(i=0;i <ico.length;i++){ 
    fileName= "qqImage/"+i+".gif";/*修改图片路径*/ 
    ico[i] =new JLabel(new  ChatPic(PicsJWindow.class.getResource(fileName),i),SwingConstants.CENTER);
    ico[i].setBorder(BorderFactory.createLineBorder(new Color(225,225,225), 1));
    ico[i].setToolTipText(i+"");
    ico[i].addMouseListener(new MouseAdapter(){ 
      public void mouseClicked(MouseEvent  e){ 
      	if(e.getButton()==1){
      		JLabel cubl = (JLabel)(e.getSource());
      		ChatPic cupic = (ChatPic)(cubl.getIcon());
      		owner.insertSendPic(cupic);
      		cubl.setBorder(BorderFactory.createLineBorder(new Color(225,225,225), 1));
        	getObj().dispose();
      	}
      }
		@Override
		public void mouseEntered(MouseEvent e) {
			((JLabel)e.getSource()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			((JLabel)e.getSource()).setBorder(BorderFactory.createLineBorder(new Color(225,225,225), 1));
		} 
      
    }); 
    p.add(ico[i]); 
    } 
    p.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseExited(MouseEvent e) {
    	getObj().dispose(); 
			}
    	
    });
  } 
	@Override
	public void setVisible(boolean show) {
		if (show) {
			determineAndSetLocation();
		}
		super.setVisible(show);
	}	
	private void determineAndSetLocation() {
		Point loc = owner.getPicBtn().getLocationOnScreen();/*控件相对于屏幕的位置*/
		setBounds(loc.x-getPreferredSize().width/3, loc.y-getPreferredSize().height,
				getPreferredSize().width, getPreferredSize().height);
	}
  private JWindow getObj(){ 
    return this; 
  } 

} 