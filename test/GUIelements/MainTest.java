package GUIelements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag)
                    try {
//                        Thread.sleep(1000);
                        Main.main.finish();
                        flag = false;
                    } catch (Exception ex) { }
            }
        }).start();
        Main.main(new String[0]);
    }
}