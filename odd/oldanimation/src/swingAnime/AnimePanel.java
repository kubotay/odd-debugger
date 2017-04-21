package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */

import java.awt.*;
import javax.swing.*;
import javanimation.*;
import jdi2diagram.event_info.FinishInfo;
import jdi2diagram.event_info.ModifierInfo;
import jdi2diagram.event_info.method_event.MethodEntryEventInfo;
import jdi2diagram.event_info.method_event.MethodExitEventInfo;
import jdi2diagram.event_info.prepare_event.ClassPrepareEventInfo;
import jdi2diagram.event_info.prepare_event.InterfacePrepareEventInfo;

import javax.swing.border.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class AnimePanel extends JPanel implements Runnable{
    Thread th;
    SwingAnime sw;
    Socket socket;
    String str;
    static javanimation anime;
    int animeMode = 0;
    JButton button2;
    JList list;
    JButton button3, button4, button5, button6;
    JLabel label1, label2, label3, label4;
    JComboBox comboBox1, comboBox2;

    public AnimePanel(SwingAnime swt, String s){
        th = new Thread(this);
        sw = swt;
        str = s;
        anime = new javanimation(this);
        this.setName(s);
        this.setLayout(new BorderLayout());
        this.setSize(800,630);

        Box box = Box.createHorizontalBox();
        final JButton button = new JButton("(再生中)");
        JButton button3 = new JButton("モード切替"); // アニメモードとブレークポイントモードの切り替え
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                anime.pressPlayButton();
                if(button.getText().equals("(再生中)")){
                    button.setText("(停止中)");
                } else if(button.getText().equals("(停止中)")){
                    button.setText("(再生中)");
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        box.add(button);
        box.add(button3);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0,100,0);
        box.add(slider);
        box.setBorder(new EmptyBorder(5,10,5,10));
        add(box, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        String[] strings = {"aaa","bbb","ccc"};
        list = new JList(strings);
        button4 = new JButton("編集");
        button5 = new JButton("削除");
        button6 = new JButton("追加");
        label1 = new JLabel("Class");
        label1.setFont(new Font("Arial", Font.PLAIN, 24));
        label2 = new JLabel("Class");
        label2.setFont(new Font("Arial", Font.PLAIN, 24));
        label3 = new JLabel("Class");
        label3.setFont(new Font("Arial", Font.PLAIN, 24));
        label4 = new JLabel("→");
        label4.setFont(new Font("Arial", Font.PLAIN, 24));
        comboBox1 = new JComboBox();
        comboBox2 = new JComboBox();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.gridheight = 3;
        layout.setConstraints(list, gbc);
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridwidth = 1; gbc.gridheight = 1;
        layout.setConstraints(button4, gbc);
        gbc.gridx = 4; gbc.gridy = 1;
        layout.setConstraints(button5, gbc);
        gbc.gridx = 9; gbc.gridy = 2;
        layout.setConstraints(button6, gbc);
        gbc.gridx = 5; gbc.gridy = 1;
        layout.setConstraints(label1, gbc);
        gbc.gridx = 8; gbc.gridy = 1;
        layout.setConstraints(label2, gbc);
        gbc.gridx = 7; gbc.gridy = 0;
        layout.setConstraints(label3, gbc);
        gbc.gridx = 7; gbc.gridy = 1;
        layout.setConstraints(label4, gbc);
        gbc.gridx = 6; gbc.gridy = 1;
        layout.setConstraints(comboBox1, gbc);
        gbc.gridx = 9; gbc.gridy = 1;
        layout.setConstraints(comboBox2, gbc);
        panel.add(list);
        panel.add(button4);
        panel.add(button5);
        panel.add(button6);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);
        panel.add(comboBox1);
        panel.add(comboBox2);
        panel.setVisible(true);
        add(panel, BorderLayout.NORTH);


        this.add(anime, BorderLayout.CENTER);
        slider.addChangeListener(anime);
        anime.getSlider(slider);
        anime.init();

        button2 = new JButton("通信開始");
        button2.setBounds(50, 175, 120, 50);
        box.add(button2);
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                th.start();
                button2.setEnabled(false);
            }
        });

        OptionPanel op = new OptionPanel(anime);
        op.setVisible(true);
        add(op, BorderLayout.EAST);
    }

    public void run(){
        try {
            socket = new Socket("localhost", 4444);
            System.out.println("接続しました" + socket.getRemoteSocketAddress());
            int i = 0;
            while(this.socket.isConnected()){
                try {
                    if(getSS().getNextSign()) {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        ModifierInfo modifierInfo = (ModifierInfo) in.readObject();
                        if (modifierInfo instanceof ClassPrepareEventInfo) {
                            ((ClassPrepareEventInfo) modifierInfo).printDump();
                            if(this.animeMode == 3) {
                                getSS().sentString(((ClassPrepareEventInfo) modifierInfo).dump());
                            }else {
                                getSS().sentObject(modifierInfo);
                            }
                            while(!getSS().getNextSign()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            out.println("next");
                        } else if(modifierInfo instanceof InterfacePrepareEventInfo){
                            out.println("next");
                        }else if (modifierInfo instanceof MethodEntryEventInfo) {
                            ((MethodEntryEventInfo) modifierInfo).printDump();
                            if(this.animeMode == 3) {
                                getSS().sentString(((MethodEntryEventInfo) modifierInfo).dump());
                            }else {
                                getSS().sentObject(modifierInfo);
                            }
                            /*if(i < 3){
                                out.println("next");
                                i++;
                            }*/
                            while(!getSS().getNextSign()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            out.println("next");
                        } else if (modifierInfo instanceof MethodExitEventInfo) {
                            ((MethodExitEventInfo) modifierInfo).printDump();
                            if(this.animeMode == 3) {
                                getSS().sentString(((MethodExitEventInfo) modifierInfo).dump());
                            }else {
                                getSS().sentObject(modifierInfo);
                            }
                            while(!getSS().getNextSign()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            out.println("next");
                        } else if (modifierInfo instanceof FinishInfo) {
                            System.out.println("finish");
                            getSS().closeBFile();
                            in.close();
                            socket.close();
                            break;
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e){
                System.out.println("切断されました　" + socket.getRemoteSocketAddress());
            }
        }
    }

    public static void loadLog(){
        anime.loadLogFile(Panel1.getSelected());
    }

    public javanimation getSS(){return this.anime;}

    public void setAnimeMode(int n){
        this.animeMode = n;
        if(animeMode == 1){
            button2.setVisible(false);
        }else if(animeMode == 2){
            button2.setVisible(true);
            anime.setMode(2);
        }else if(animeMode == 3){
            button2.setVisible(true);
            anime.setMode(3);
            anime.createBFile();
        }
    }

    public void pc(){
        sw.panelChange((JPanel) this, sw.panelNames[0]);
    }
}
