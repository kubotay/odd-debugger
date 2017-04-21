package swingTest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BPanel extends JPanel{
	public BPanel(){
		this.setSize(320, 160);
		this.add(new JLabel("BPanel"));
		JButton button = new JButton("Change");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				Main.pc("B");
			}
		});
		this.add(button);
		this.setVisible(false);
	}
}
