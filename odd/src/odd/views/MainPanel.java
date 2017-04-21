package odd.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MainPanel extends JPanel{
	static javanimation anime;
    File file;
    boolean clearedFlag = true; // trueならファイルを開いたりできる
    boolean breakSetFlag = false; // trueならブレークポイントモード falseならアニメモード
    static boolean animationFlag = true; // trueならアニメーションする
    int animeMode = 0;
    int waitState = 0; // 1~3がそれぞれ対応するラベルの入力待ち
    JLabel label1, label2, label3, fieldNameLabel, arrowLabel;
    JComboBox comboBox1, comboBox2, classBox;
    JRadioButton objectButton, classButton;
    JScrollPane sp1, sp2;
    ArrayList<String[]> fieldList = new ArrayList<String[]>(); // フィールドのブレークポイント
    ArrayList<String[]> methodList = new ArrayList<String[]>(); // メソッドのブレークポイント


	public MainPanel(){
		this.setLayout(new BorderLayout());
        this.setSize(820,670);

        JTabbedPane tabbedPane = new JTabbedPane();
		JLabel loadLog = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\loadLog.png"));
		loadLog.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		if(clearedFlag){
		    		JFileChooser filechooser = new JFileChooser();
		            int selected = filechooser.showOpenDialog(loadLog);
		            if (selected == JFileChooser.APPROVE_OPTION){ // 開くが選択された
		              file = filechooser.getSelectedFile();
		              anime.loadLogFile(file);
		              animeMode = 1;
		              clearedFlag = false;
		            }else if (selected == JFileChooser.CANCEL_OPTION){ // 取消しが選択された
		            }else if (selected == JFileChooser.ERROR_OPTION){ // エラー時
		            }
        		}
        	}
		});
		ImageIcon imageIcon = new ImageIcon("animationOff.png");
		//JLabel writeLog = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\writeLog.png"));
		JLabel writeLog = new JLabel(imageIcon);
		writeLog.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		if(clearedFlag){
	        		JFileChooser filechooser = new JFileChooser();
	                int selected = filechooser.showSaveDialog(writeLog);
	                if (selected == JFileChooser.APPROVE_OPTION){ // 開くが選択された
	                  file = filechooser.getSelectedFile();
	                  clearedFlag = false;
	                }else if (selected == JFileChooser.CANCEL_OPTION){ // 取消しが選択された
	                }else if (selected == JFileChooser.ERROR_OPTION){ // エラー時
	                }
        		}
        	}
		});
		ImageIcon animeOn = new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\animationOn.png");
		ImageIcon animeOff = new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\animationOff.png");
		JLabel animeButton = new JLabel(animeOn);
		animeButton.addMouseListener(new MouseAdapter(){ // animationFlagがfalseのときにログを取っておくか、最新の状態だけ出すか聞く
			public void mouseClicked(MouseEvent e){
				animationFlag = !animationFlag;
				if(animationFlag){
					animeButton.setIcon(animeOn);
				}else{
					animeButton.setIcon(animeOff);
				}
			}
		});
		ImageIcon play = new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\play.png");
		ImageIcon stop = new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\stop.png");
		JLabel psButton = new JLabel(stop);
		psButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		int playFlag = anime.pressPlayButton();
                if(playFlag == 1){
                	psButton.setIcon(stop);
                }else if(playFlag == 0){
                	psButton.setIcon(play);
                }
        	}
		});
		JLabel setBreak = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\setBreak.png"));
		setBreak.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		if(anime.getMode() == 2 || animeMode == 2){
	        		breakSetFlag = !breakSetFlag;
	        		tabbedPane.setVisible(breakSetFlag);
	        		if(breakSetFlag){
	        			animeMode = anime.getMode();
	        			anime.setMode(4);
	        			anime.changeFrameSize(anime.width,anime.height-120);
	        		}else{
	        			anime.setMode(animeMode);
	        			anime.changeFrameSize(anime.width,anime.height+120);
	        		}
        		}
        	}
		});
		JLabel breakList = new JLabel("BreakList");
		breakList.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		if(!BreakPanel.createdFlag){
        			JFrame breakWindow = new JFrame("ブレークポイント一覧");
	        		BreakPanel panel = new BreakPanel(fieldList, methodList);
	        		breakWindow.setSize(400,300);
	        		breakWindow.add(panel);
	        		breakWindow.setVisible(true);
	        		breakWindow.addWindowListener(new WindowAdapter() {
	        			// ウィンドウが閉じるときに呼ばれる
	        			public void windowClosing(WindowEvent e) {
	        				BreakPanel.createdFlag = false;
	        			}
	        		});
        		}
        	}
		});
		JLabel clear = new JLabel("Clear");
		clear.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		anime.returnMenu();
        		animeMode = 0;
        		clearedFlag = true;
        		classBox.removeAllItems();
        		fieldNameLabel.setText("Field Name");
        		fieldNameLabel.setForeground(Color.black);
        		label1.setText("*****");
        		label1.setForeground(Color.black);
        		label2.setText("*****");
        		label2.setForeground(Color.black);
        		label3.setText("-----");
        		label3.setForeground(Color.black);
        		comboBox1.removeAllItems();
        		comboBox2.removeAllItems();
        		waitState = 0;
        		fieldList.clear();
        		methodList.clear();
        	}
		});
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0,100,0);
		Box box = Box.createHorizontalBox();
		box.add(loadLog);
		box.add(Box.createRigidArea(new Dimension(10,1)));
		box.add(writeLog);
		box.add(Box.createRigidArea(new Dimension(10,1)));
		box.add(animeButton);
		box.add(Box.createRigidArea(new Dimension(20,1)));
		box.add(psButton);
		box.add(Box.createRigidArea(new Dimension(10,1)));
		box.add(setBreak);
		box.add(Box.createRigidArea(new Dimension(10,1)));
		box.add(breakList);
		box.add(Box.createRigidArea(new Dimension(20,1)));
		box.add(clear);
		box.add(slider);
		add(box, BorderLayout.NORTH);

		anime = new javanimation(this);
        anime.setBounds(0, 0, 800, 630);
        this.add(anime, BorderLayout.CENTER);
        anime.setBounds(0, 0, 800, 630);
        slider.addChangeListener(anime);
        anime.getSlider(slider);
        anime.init();

        Box sideBox = Box.createVerticalBox();
		JLabel plus = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\plus.png"));
		plus.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		anime.pressZoomUp();
        	}
		});
		JLabel minus = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\minus.png"));
		minus.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		anime.pressZoomDown();
        	}
		});
		JLabel token = new JLabel(new ImageIcon("C:\\Users\\shou\\work_plag\\odd\\src\\data\\token.png"));
		token.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		anime.pressTokenButton();
        	}
		});
		JSlider speedSlider = new JSlider(JSlider.VERTICAL, 10,100,40);
		speedSlider.setMaximumSize(new Dimension(15,100));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!speedSlider.getValueIsAdjusting()) {
                    anime.changeDelay((int)source.getValue());
                }
            }
        });
        sideBox.add(Box.createRigidArea(new Dimension(1,30)));
		sideBox.add(plus);
		sideBox.add(Box.createRigidArea(new Dimension(1,10)));
		sideBox.add(minus);
		sideBox.add(Box.createRigidArea(new Dimension(1,10)));
		sideBox.add(token);
		sideBox.add(Box.createRigidArea(new Dimension(1,10)));
		sideBox.add(new JLabel("-"));
		sideBox.add(speedSlider);
		sideBox.add(new JLabel("+"));
		sideBox.add(Box.createRigidArea(new Dimension(1,10)));
        add(sideBox, BorderLayout.WEST);

        Box fieldBox = Box.createHorizontalBox();
        fieldNameLabel = new JLabel("Field Name");
        fieldNameLabel.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		waitState = 4;
        		fieldNameLabel.setForeground(Color.red);
        		label1.setForeground(Color.black);
        		label2.setForeground(Color.black);
        		label3.setForeground(Color.black);
        	}
		});
        JCheckBox accessBox = new JCheckBox("Access");
        JCheckBox modificationBox = new JCheckBox("Modification");
        //JCheckBox hitCountBox1 = new JCheckBox("Hit Count:");
        //JTextField setHitCount1 = new JTextField("0");
        //setHitCount1.setMaximumSize(new Dimension(40,20));
        //ButtonGroup bg1 = new ButtonGroup();
        //JRadioButton threadButton1 = new JRadioButton("Thread");
        //JRadioButton vmButton1 = new JRadioButton("VM");
        //bg1.add(vmButton1);
        //bg1.add(threadButton1);
        //vmButton1.setSelected(true);
        objectButton = new JRadioButton("Object");
        classButton = new JRadioButton("Class");
        ButtonGroup bg3 = new ButtonGroup();
        bg3.add(objectButton);
        bg3.add(classButton);
        objectButton.setSelected(true);
        JButton addFieldButton = new JButton("Add");
        addFieldButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	String[] s = {fieldNameLabel.getText(), String.valueOf(accessBox.isSelected()), String.valueOf(modificationBox.isSelected()),
            			      ""};
            	/*if(hitCountBox1.isSelected()){
            		s[3] = setHitCount1.getText();
            	}else{
            		s[3] = "null";
            	}*/
            	if(objectButton.isSelected()){
            		s[3] = "Object";
            	}else{
            		s[3] = "Class";
            	}
            	/*if(vmButton1.isSelected()){
            		s[5] = "VM";
            	}else{
            		s[5] = "Thread";
            	}*/
            	fieldList.add(s);
            	if(modificationBox.isSelected()){
        			String[] str = fieldNameLabel.getText().split(":");
        			anime.setModificationWatchpointListener(Long.parseLong(str[1]), str[0], s[3]);
        		}
            	if(accessBox.isSelected()){
            		String[] str = fieldNameLabel.getText().split(":");
        			anime.setAccessWatchpointListener(Long.parseLong(str[1]), str[0], s[3]);
            	}

            	fieldNameLabel.setText("Field Name");
        		fieldNameLabel.setForeground(Color.black);
        		waitState = 0;
            }
        });
        fieldBox.add(fieldNameLabel);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        fieldBox.add(accessBox);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        fieldBox.add(modificationBox);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        //fieldBox.add(hitCountBox1);
        //fieldBox.add(setHitCount1);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        //fieldBox.add(threadButton1);
        //fieldBox.add(vmButton1);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        fieldBox.add(objectButton);
        fieldBox.add(classButton);
        fieldBox.add(Box.createRigidArea(new Dimension(20,1)));
        fieldBox.add(addFieldButton);
        sp1 = new JScrollPane(fieldBox);
        sp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Field", sp1);
        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new GridLayout(2,1));
        Box methodBox1 = Box.createHorizontalBox();
        Box methodBox2 = Box.createHorizontalBox();
        label1 = new JLabel("*****");
        comboBox1 = new JComboBox();
        label2 = new JLabel("*****");
        comboBox2 = new JComboBox();
        comboBox1.setMaximumSize(new Dimension(200,20));
        comboBox2.setMaximumSize(new Dimension(200,20));
        label3 = new JLabel("-----");
        label1.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		waitState = 1;
        		label1.setForeground(Color.red);
        		label2.setForeground(Color.black);
        		label3.setForeground(Color.black);
        		fieldNameLabel.setForeground(Color.black);
        	}
		});
        label2.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		waitState = 2;
        		label2.setForeground(Color.red);
        		label1.setForeground(Color.black);
        		label3.setForeground(Color.black);
        		fieldNameLabel.setForeground(Color.black);
        	}
		});
        label3.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		waitState = 3;
        		label3.setForeground(Color.red);
        		label1.setForeground(Color.black);
        		label2.setForeground(Color.black);
        		fieldNameLabel.setForeground(Color.black);
        	}
		});
        arrowLabel = new JLabel("-->");
        arrowLabel.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e){
        		if(arrowLabel.getText().equals("-->")){
        			arrowLabel.setText("<---->");
        		}else{
        			arrowLabel.setText("-->");
        		}
        	}
		});
        //JCheckBox hitCountBox2 = new JCheckBox("Hit Count:");
        //JTextField setHitCount2 = new JTextField("0");
        //setHitCount2.setMaximumSize(new Dimension(40,20));
        //JRadioButton threadButton2 = new JRadioButton("Thread");
        //JRadioButton vmButton2 = new JRadioButton("VM");
        //ButtonGroup bg2 = new ButtonGroup();
        //bg2.add(threadButton2);
        //bg2.add(vmButton2);
        //vmButton2.setSelected(true);
        JButton addMethodButton = new JButton("Add");
        addMethodButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            	String[] s = {label1.getText(),(String)comboBox1.getSelectedItem(),label2.getText(),(String)comboBox2.getSelectedItem(),label3.getText()};
            	if(s[1] == null){
            		s[1] = "*****";
            	}
            	if(s[3] == null){
            		s[3] = "*****";
            	}
            	/*if(hitCountBox2.isSelected()){
            		s[5] = setHitCount2.getText();
            	}else{
            		s[5] = "null";
            	}
            	if(vmButton2.isSelected()){
            		s[6] = "VM";
            	}else{
            		s[6] = "Thread";
            	}*/
            	methodList.add(s);
            	if(arrowLabel.getText().equals("<---->")){
            		String[] s2 = {s[2], s[3], s[0], s[1], s[4]};
            		methodList.add(s2);
            	}
            	label1.setText("*****");
        		label1.setForeground(Color.black);
        		label2.setText("*****");
        		label2.setForeground(Color.black);
        		label3.setText("-----");
        		label3.setForeground(Color.black);
        		comboBox1.removeAllItems();
        		comboBox2.removeAllItems();
        		waitState = 0;
        		System.out.println(s);
            }
        });
        classBox = new JComboBox();
        classBox.setMaximumSize(new Dimension(150,20));
        methodBox1.add(label1);
        methodBox1.add(new JLabel(":"));
        methodBox1.add(comboBox1);
        methodBox1.add(Box.createRigidArea(new Dimension(20,1)));
        methodBox1.add(arrowLabel);
        methodBox1.add(label2);
        methodBox1.add(new JLabel(":"));
        methodBox1.add(comboBox2);
        methodBox1.add(Box.createRigidArea(new Dimension(20,1)));
        methodBox2.add(new JLabel("Argument:"));
        methodBox2.add(label3);
        //methodBox2.add(hitCountBox2);
        //methodBox2.add(setHitCount2);
        methodBox2.add(Box.createRigidArea(new Dimension(20,1)));
        //methodBox2.add(threadButton2);
        //methodBox2.add(vmButton2);
        methodBox2.add(Box.createRigidArea(new Dimension(20,1)));
        //methodBox2.add(classBox);
        methodBox2.add(Box.createRigidArea(new Dimension(20, 1)));
        methodBox2.add(addMethodButton);
        methodPanel.add(methodBox1);
        methodPanel.add(methodBox2);
        methodPanel.setVisible(true);
        sp2 = new JScrollPane(methodPanel);
        sp2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Method", sp2);
        add(tabbedPane, BorderLayout.SOUTH);
        tabbedPane.setVisible(false);


	}

	public void run(){

	}

	public void setObjectName(String name){
    	switch(waitState){
    		case 1:
    			label1.setText(name);
    			label1.setForeground(Color.black);
    			setMethodList(1);
    			waitState = 0;
    			sp2.doLayout();
    			break;
    		case 2:
    			label2.setText(name);
    			label2.setForeground(Color.black);
    			setMethodList(2);
    			waitState = 0;
    			sp2.doLayout();
    			break;
    		case 3:
    			label3.setText(name);
    			label3.setForeground(Color.black);
    			waitState = 0;
    			sp2.doLayout();
    			break;
    		case 4:
    			fieldNameLabel.setText(name);
    			fieldNameLabel.setForeground(Color.black);
    			waitState = 0;
    			sp1.doLayout();
    			break;
    	}
    }

	public ArrayList<String[]> getFieldBreakPoint(){return this.fieldList;}
	public ArrayList<String[]> getMethodBreakPoint(){return this.methodList;}

	public void setMethodList(int num){
    	if(num == 1){
    		comboBox1.removeAllItems();
    		comboBox1.addItem("*****");
    		long id = Long.parseLong(label1.getText());
    		ArrayList<String> methods = anime.getObjectMethods(id);
    		for(String s : methods){
    			comboBox1.addItem(s);
    		}
    	}else if(num == 2){
    		comboBox2.removeAllItems();
    		comboBox2.addItem("*****");
    		long id = Long.parseLong(label2.getText());
    		ArrayList<String> methods = anime.getObjectMethods(id);
    		for(String s : methods){
    			comboBox2.addItem(s);
    		}
    	}
        comboBox1.setMaximumSize(new Dimension(200,20));
        comboBox2.setMaximumSize(new Dimension(200,20));
    }

	public int getWateState(){return this.waitState;}

	public boolean getbg3(){
		if(classButton.isSelected()){
			return true;
		}
		return false;
	}

	public void addClassName(String cName){
		classBox.addItem(cName);
    }

	public void changeFrameSize(int x, int y){
    	this.setSize(x, y);
    	anime.changeFrameSize(x-20, y-30);
    }

	public static javanimation getAnimation(){
		return anime;
	}

	public void setClearedFlag(boolean f){this.clearedFlag = f;	}
	public static boolean getAnimationFlag(){return animationFlag;}

}
