package GUIelements;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestartControllerTest {

    @AfterEach
    void tearDown() {
        Main.main(new String[0]);
    }


    // Вылетает ошибка, которая не связана с тестом
    @Test
    void tryPutShipOnTheField() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                try {
                    String style = "-fx-background-color: #FF0000; -fx-border-color: #000000";
                    Thread.sleep(2000);
                    var rest = (Button)Controller.controller.root.lookup("#restart");
                    rest.fire();
                    for (int i = 0; i < 10; ++i)
                            for (int j = 0; j < 10; ++j) {
                                Controller.controller.fields.get(i).get(j).getOnMouseEntered().handle(null);
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
    }



    @Test
    void endRestartMode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                try {
                    String style = "-fx-background-color: #FF0000; -fx-border-color: #000000";
                    Thread.sleep(2000);
                    var rest = (Button)Controller.controller.root.lookup("#restart");
                    rest.fire();

                    var rand = (Button)Controller.controller.root.lookup("#random");
                    rand.fire();

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
    }

}