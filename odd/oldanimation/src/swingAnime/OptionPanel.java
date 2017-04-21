package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javanimation.*;

public class OptionPanel extends JPanel{
    JButton button1 = new JButton("クラス名のみ"), button2 = new JButton("あり"),
            button3 = new JButton("拡大"), button4 = new JButton("縮小");

    public OptionPanel(final javanimation anime){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(100, 500);

        JLabel label1 = new JLabel("クラス名表示");
        this.add(label1);
        button1.setBounds(410,100,20,80);
        button1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                anime.pressClassButton();
                if(button1.getText().equals("クラス名のみ")){
                    button1.setText("すべて表示");
                } else if(button1.getText().equals("すべて表示")){
                    button1.setText("クラス名のみ");
                }
            }
        });
        this.add(button1);

        JLabel label2 = new JLabel("トークン表示");
        this.add(label2);
        button2.setBounds(410,300,20,30);
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                anime.pressTokenButton();
                if(button2.getText().equals("あり")){
                    button2.setText("なし");
                } else if(button2.getText().equals("なし")){
                    button2.setText("あり");
                }
            }
        });
        this.add(button2);

        JLabel label3 = new JLabel("拡大率操作");
        this.add(label3);
        button3.setBounds(410,300,20,30);
        button3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                anime.pressZoomUp();
            }
        });
        this.add(button3);

        button4.setBounds(410,300,20,30);
        button4.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                anime.pressZoomDown();
            }
        });
        this.add(button4);

        JLabel label4 = new JLabel("再生速度(速←→遅)");
        this.add(label4);

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 10,100,50);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!slider.getValueIsAdjusting()) {
                    anime.changeDelay((int)source.getValue());
                }
            }
        });
        this.add(slider);
    }
}
