package javanimation;

import processing.core.*;

/**
 * Created by shou on 2014/12/07.
 */
public class Token {
    javanimation papplet;
    private long instansId;
    private long threadId;
    private int tokenId = 0;
    static int countToken = 0;
    private float[] point = {0,0};
    private Integrator[] integrator;
    private boolean clickFlag = false; // 選択されるとtrueに

    public Token(MEE mee, javanimation p){
        this.instansId = mee.getId();
        this.threadId = mee.getThreadId();
        this.papplet = p;
        this.tokenId = countToken;
        countToken++;

        integrator = new Integrator[2];
        integrator[0] = new Integrator(this.point[0]);
        integrator[1] = new Integrator(this.point[1]);
    }
    public long drawToken(float[] p){
        integrator[0].target(p[0]);
        integrator[1].target(p[1]);
        integrator[0].cUpdate();
        integrator[1].cUpdate();

        if(papplet.getTokenFlag()) {
            papplet.noStroke();
            tokenColor();
            papplet.ellipse(integrator[0].getValue(), integrator[1].getValue(), (float) 2.5 * papplet.getMagnification() / 10, (float) 2.5 * papplet.getMagnification() / 10);
            papplet.fill(0);
            papplet.text((int)this.threadId,integrator[0].getValue(),integrator[1].getValue());
        }
        return instansId;
    }
    public long getInstansId() { return this.instansId;}
    public void setInstansId(long id) { this.instansId = id;}
    public long getThreadId(){ return this.threadId;}
    public float[] getPoint() {
        this.point[0] = integrator[0].getValue();
        this.point[1] = integrator[1].getValue();

        return this.point;
    }
    public void setPoint(float[] p){
        integrator[0].target(p[0]);
        integrator[1].target(p[1]);
    }
    public void tokenColor() {
        switch(this.tokenId%9) {
            case 0: papplet.fill(0x77E60012); break;
            case 1: papplet.fill(0x771D2088); break;
            case 2: papplet.fill(0x77009944); break;
            case 3: papplet.fill(0x77F39800); break;
            case 4: papplet.fill(0x77009E96); break;
            case 5: papplet.fill(0x77920783); break;
            case 6: papplet.fill(0x77E5004F); break;
            case 7: papplet.fill(0x770068B7); break;
            case 8: papplet.fill(0x778FC31F); break;
        }
    }

    public int getTokenColor(){
        return this.tokenId%9;
    }

    public boolean checkTokenClick(float mouseX, float mouseY){
        float r = (float) 2.5 * papplet.getMagnification() / 20;
        float d = papplet.dist(integrator[0].getValue(), integrator[1].getValue(), mouseX, mouseY);
        if(d < r){
            System.out.println("トークン：" + this.tokenId);
            this.clickFlag = !this.clickFlag;
            return true;
        }
        return false;
    }
}
