package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;

public class Panel1 extends JPanel implements ListSelectionListener{
    JButton button1 = new JButton("ストリームへ"), button2 = new JButton("スタート");
    SwingAnime sw;
    String str;
    static String selected;
    JList dataList;
    JLabel label;

    public Panel1(SwingAnime swt, String s){
        sw = swt;
        str = s;
        this.setName(s);
        this.setLayout(null);
        this.setSize(820,670);

        button1.setBounds(150,20,120,50);
        button1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[2]);
            }
        });
        this.add(button1);

        File file = new File("C:\\Users\\shou\\IdeaProjects\\javanimation\\src\\data\\log");
        String[] files = file.list();
        dataList = new JList(files);
        JScrollPane sPane = new JScrollPane();
        sPane.getViewport().setView(dataList);
        sPane.setPreferredSize(new Dimension(200, 80));
        sPane.setBounds(100, 120, 200, 80);
        this.add(sPane);

        label = new JLabel();
        label.setText("選択されたファイル：");
        label.setBounds(100,220,600,30);
        this.add(label);

        dataList.addListSelectionListener(this);

        button2.setBounds(150,300,120,50);
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pc(sw.panelNames[3]);
            }
        });
        this.add(button2);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object[] arr = dataList.getSelectedValues();
        String result = "";
        for(Object obj:arr){
            result += (String)obj;
        }
        label.setText("選択されたファイル：" + result);
        selected = result;
    }

    public void pc(String str){
        sw.panelChange((JPanel) this, str);
    }

    public static String getSelected(){
        return selected;
    }
}

