package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */
import java.awt.event.*;
import javax.swing.*;

public class Panel2 extends JPanel{
    JButton button1 = new JButton("バッチへ"), button2 = new JButton("スタート"), button3 = new JButton("バッチファイル作成");
    SwingAnime sw;
    String str;

    public Panel2(SwingAnime swt, String s){
        sw = swt;
        str = s;
        this.setName(s);
        this.setLayout(null);
        this.setSize(400,400);

        button1.setBounds(150,20,120,50);
        button1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[1]);
            }
        });
        this.add(button1);

        button2.setBounds(150,250,120,50);
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[3]);
            }
        });
        this.add(button2);

        button3.setBounds(85,320,250,50);
        button3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[3]);
                sw.setBCreateMode();
            }
        });
        this.add(button3);
    }

    public void pc(String str){
        sw.panelChange((JPanel) this, str);
    }
}

