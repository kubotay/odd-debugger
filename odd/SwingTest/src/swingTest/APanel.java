package swingTest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class APanel extends JPanel{
	public APanel(){
		this.setSize(320, 160);
		this.add(new JLabel("APanel"));
		JButton button = new JButton("Change");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				Main.pc("A");
			}
		});
		this.add(button);
		this.setVisible(true);
	}
}
