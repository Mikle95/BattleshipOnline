package GUIelements;

import battleShip.Ocean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class RestartController {
    public static RestartController restartController;

    Controller controller;
    int[] types = {1,2,3,4};
    int shipType = 1;
    TextField editX;
    TextField editY;
    boolean horizontal;
    EventHandler[] events = {null, null, null};
    List<List<Integer>> history = new ArrayList<>();

    EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()){
                case BACK_SPACE:
                    deleteLastShip();
                    break;
                case DIGIT1: shipType = 1; break;
                case DIGIT2: shipType = 2; break;
                case DIGIT3: shipType = 3; break;
                case DIGIT4: shipType = 4; break;
                case R: horizontal = !horizontal; break;
                case UP:
                case DOWN:
                case RIGHT:
                case LEFT: try{
                    if (!controller.restartMode) return;
                    TextField editX = (TextField) controller.root.lookup("#editX");
                    TextField editY = (TextField) controller.root.lookup("#editY");
                    int x = Integer.parseInt(editX.getText());
                    int y = Integer.parseInt(editY.getText());
                    refresh();
                    String style = "-fx-background-color: #FF0000; -fx-border-color: #000000";
                    if (tryPutShipOnTheField(y - 1, x - 1) && types[shipType - 1] > 0)
                        style = "-fx-background-color: #00FF00; -fx-border-color: #000000";
                    paintShip(y - 1, x - 1, style);
                    return;
                }catch (Exception ex) { break;}
                case ENTER:
                    TextField editX = (TextField) controller.root.lookup("#editX");
                    TextField editY = (TextField) controller.root.lookup("#editY");
                    try {
                        editX.setStyle("-fx-border-color: #008000");
                        editY.setStyle("-fx-border-color: #008000");
                        int x = Integer.parseInt(editX.getText());
                        int y = Integer.parseInt(editY.getText());
                        if (putShipOnTheField(y - 1, x - 1))
                            refresh();
                    }catch (Exception ex) {}
                break;
            }
            refresh();
        }
    };

    RestartController(Controller controller){
        restartController = this;
        this.controller = controller;
        editX = (TextField) controller.root.lookup("#editX");
        editY = (TextField) controller.root.lookup("#editY");
    }

    void RestartMode(){
        controller.restartMode = true;
        controller.ocean = new Ocean();
        controller.primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, keyPressed);
        Button undo = (Button)controller.root.lookup("#edit");
        undo.setText("Undo");
        events[0] = undo.getOnAction();
        undo.setOnAction(event -> {
            deleteLastShip();
            refresh();
        });


        Button restart = (Button)controller.root.lookup("#ready");
        events[1] = restart.getOnAction();
        restart.setOnAction(event -> endRestartMode());

        Button rndRestart = (Button) controller.root.lookup("#connect");
        events[2] = rndRestart.getOnAction();
        rndRestart.setText("Place ships randomly");
        rndRestart.setOnAction(event -> {
            controller.ocean = new Ocean();
            controller.ocean.placeAllShipsRandomly();
            types[0] = types[1] = types[2] = types[3] = 0;
            history.clear();
            refresh();
        });

        controller.logs.clear();
        controller.statistics.clear();
        editX.setEditable(false);
        editY.setEditable(false);

        for (int i = 0; i < 10; ++i)
            for (int j = 0; j < 10; ++j)
                setRestartAction(i, j);

        refresh();
    }

    private void setRestartAction(int x, int y){
        controller.Myfields.get(x).get(y).setOnAction(event -> {
            if(putShipOnTheField(x, y))
                refresh();
        });
        controller.Myfields.get(x).get(y).setOnMouseEntered(event -> {
            if(!controller.restartMode) return;

            refresh();
            String style = "-fx-background-color: #FF0000; -fx-border-color: #000000";
            if(tryPutShipOnTheField(x, y) && types[shipType - 1] > 0) style = "-fx-background-color: #00FF00; -fx-border-color: #000000";
            paintShip(x, y, style);
        });
    }

    void paintShip(int x, int y, String style){
        refresh();
        try {
            for (int i = 0; i < 5 - shipType; ++i)
                if (!horizontal)
                    controller.Myfields.get(x + i).get(y).setStyle(style);
                else
                    controller.Myfields.get(x).get(y + i).setStyle(style);
        }catch (Exception ex){}
    }

    boolean tryPutShipOnTheField(int x, int y){
        return controller.ocean.okToPlaceShip(x, y, horizontal, shipType - 1);
    }

    boolean putShipOnTheField(int x, int y){

        if(shipType < 1 || shipType > 4 || types[shipType - 1] == 0) return false;
        if(!controller.ocean.placeShip(x, y, horizontal, shipType - 1)) return false;
        --types[shipType - 1];
        var last = new ArrayList<Integer>();
        last.add(Integer.valueOf(x));
        last.add(Integer.valueOf(y));
        last.add(Integer.valueOf(shipType));
        history.add(last);
        return true;
    }

    void refresh(){
        for (int i = 0; i < 10; ++i)
            for (int j = 0; j < 10; ++j){
                if(controller.ocean.isOccupied(i,j))
                    controller.Myfields.get(i).get(j).setStyle("-fx-background-color: #00FF00; -fx-border-color: #000000");
                else
                    controller.Myfields.get(i).get(j).setStyle("-fx-background-color: #00FFFF; -fx-border-color: #000000");
            }
        controller.statistics.setText("Press 1 - 4 to change ship\n" +
                "Press r to switch horizontal/vertical\n" +
                "Backspace for undo operation\n" +
                "You can use arrows\n" +
                "1 - BattleShip(" + types[0] + ")\n" +
                "2 - Cruiser(" + types[1] + ")\n" +
                "3 - Destroyer(" + types[2] + ")\n" +
                "4 - Submarine(" + types[3] + ")");
    }

    boolean endRestartMode(){
        if(types[0] + types[1] + types[2] + types[3] > 0) {
            controller.logs.appendText("Not enough ships on the field!!!\n");
            return false;
        }
        Button undo = (Button)controller.root.lookup("#edit");
        undo.setOnAction(events[0]);
        undo.setText("EDIT MODE");
        Button ready = (Button)controller.root.lookup("#ready");
        ready.setOnAction(events[1]);
        Button rndRestart = (Button) controller.root.lookup("#connect");
        rndRestart.setText("CONNECT");
        rndRestart.setOnAction(events[2]);
        controller.primaryStage.removeEventFilter(KeyEvent.KEY_RELEASED, keyPressed);
        controller.GameMode();
        return true;
    }

    void deleteLastShip(){
        if(history.size() == 0) {
            types = new int[4];
            for (int i = 0; i < 4; ++i)
                types[i] = i + 1;
            controller.ocean = new Ocean();
            return;
        }
        var last = history.get(history.size() - 1);
        if(last.size() != 3) return;
        controller.ocean.deleteShip(last.get(0), last.get(1));
        ++types[last.get(2) - 1];
        history.remove(last);
    }
}
