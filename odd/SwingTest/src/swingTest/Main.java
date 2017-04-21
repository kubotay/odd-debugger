package swingTest;

import javax.swing.JFrame;

public class Main extends JFrame{

	static APanel aPanel = new APanel();
	static BPanel bPanel = new BPanel();

	public static void main(String[] args){
		Main main = new Main();
		main.setVisible(true);
	}
	
	public Main(){
		setTitle("サンプル");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 160);
        setLocationRelativeTo(null);
        add(aPanel);
        add(bPanel);
	}

	public static void pc(String str){
		if(str.equals("A")){
			aPanel.setVisible(false);
			bPanel.setVisible(true);
		}else if(str.equals("B")){
			aPanel.setVisible(true);
			bPanel.setVisible(false);
		}
	}
}
