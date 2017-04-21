package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel{
    JButton button1, button2;
    SwingAnime sw;
    String str;

    public MainPanel(SwingAnime swt, String s){
        sw = swt;
        str = s;
        this.setName(s);
        this.setLayout(null);
        this.setSize(400,400);

        button1 = new JButton("バッチへ");
        button1.setBounds(50,175,120,50);
        button1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[1]);
            }
        });
        this.add(button1);

        button2 = new JButton("ストリームへ");
        button2.setBounds(250,175,120,50);
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[2]);
            }
        });
        this.add(button2);
    }

    public void pc(String str){
        sw.panelChange((JPanel) this, str);
    }

}

