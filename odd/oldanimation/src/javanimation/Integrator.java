package javanimation;

/**
 * Created by shou on 2014/11/02.
 */

public class Integrator {
    final float DAMPING = 0.5f;
    final float ATTRACTION = 0.2f;

    float value;
    float vel;
    float accel;
    float force;
    float mass = 1;
    int update_flag = 1;

    float damping = DAMPING;
    float attraction = ATTRACTION;
    boolean targeting;
    float target;


    Integrator() { }


    Integrator(float value) {
        this.value = value;
    }


    Integrator(float value, float damping, float attraction) {
        this.value = value;
        this.damping = damping;
        this.attraction = attraction;
    }


    void set(float v) {
        value = v;
    }


    void update() {
        if(javanimation.play_flag==1) {
            if (update_flag == 1) {
                if (targeting) {
                    force += attraction * (target - value);
                }

                accel = force / mass;
                vel = (vel + accel) * damping;
                value += vel;

                force = 0;
                update_flag = 0;
            } else {
                update_flag = 1;
            }
        }
    }

    void cUpdate(){ // クラスネームフラグが切り替った時用のアップデート
        if (update_flag == 1) {
            if (targeting) {
                force += attraction * (target - value);
            }

            accel = force / mass;
            vel = (vel + accel) * damping;
            value += vel;

            force = 0;
            update_flag = 0;
        } else {
            update_flag = 1;
        }
    }

    void target(float t) {
        targeting = true;
        target = t;
    }


    void noTarget() {
        targeting = false;
    }

    float getValue(){
        return this.value;
    }
    void  setValue(float v) { this.value = v;}

    float getTarget(){
        return this.target;
    }
    public void setAttraction(float f){
        this.attraction = f;
    }
}
