package GUIelements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void makeShot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                try {
                    Thread.sleep(5000);
                    for (int i = 0; i < 10; ++i)
                        for (int j = 0; j < 10; ++j){
                            Controller.controller.fields.get(i).get(j).fire();
                            Thread.sleep(50);
                        }
                    Thread.sleep(2000);
                } catch (Exception ex) { }
                try{
                    while (flag) {
                        Main.main.finish();
                        flag = false;
                    }
                }catch (Exception ex){}
            }
        }).start();
        Main.main(new String[0]);
        try {
            Thread.sleep(1000);
        }catch (Exception ex){}
    }
}