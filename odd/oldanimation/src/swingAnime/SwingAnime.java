package swingAnime;

/**
 * Created by shou on 2015/04/20.
 */

import javax.swing.*;

public class SwingAnime extends JFrame{
    public String[] panelNames = {"スタート","バッチ","ストリーム","アニメーション"};
    MainPanel mp = new MainPanel(this, panelNames[0]); //起動時
    Panel1 p1 = new Panel1(this, panelNames[1]); //バッチ式
    Panel2 p2 = new Panel2(this, panelNames[2]); //ストリーム式
    AnimePanel ap = new AnimePanel(this, panelNames[3]); //アニメーション

    public SwingAnime(){
        this.add(mp); mp.setVisible(true);
        this.add(p1); p1.setVisible(false);
        this.add(p2); p2.setVisible(false);
        this.add(ap); ap.setVisible(false);
        setBounds(100,100,1030,679);
    }

    public void panelChange(JPanel jp, String str){
        String name = jp.getName();
        if(name==panelNames[0])
            mp = (MainPanel)jp; mp.setVisible(false);
        if(name==panelNames[1]) {
            p1 = (Panel1) jp;
            p1.setVisible(false);
            ap.setAnimeMode(1);
            if(str==panelNames[3])
                ap.loadLog();
        }
        if(name==panelNames[2]) {
            p2 = (Panel2) jp;
            p2.setVisible(false);
            ap.setAnimeMode(2);
        }
        if(name==panelNames[3]) {
            ap = (AnimePanel) jp;
            ap.setVisible(false);
        }

        if(str==panelNames[0])
            mp.setVisible(true);
        if(str==panelNames[1])
            p1.setVisible(true);
        if(str==panelNames[2])
            p2.setVisible(true);
        if(str==panelNames[3])
            ap.setVisible(true);
    }

    public void setBCreateMode(){
        ap.setAnimeMode(3);
    }

    public static void main(String[] args){
        SwingAnime sw = new SwingAnime();
        sw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sw.setVisible(true);
    }
}
