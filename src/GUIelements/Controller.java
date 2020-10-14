package GUIelements;

import Network.Message;
import Network.OnlineController;
import battleShip.Ocean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {

    public static Controller controller;

    TextArea statistics;
    TextArea logs;
    public Ocean ocean;
    List<List<Button>> fields = new ArrayList<>();
    List<List<Button>> Myfields = new ArrayList<>();
    Stage primaryStage;
    Parent root;
    boolean restartMode = false;
    OnlineController netController;
    public boolean isReady = false;


    Controller(Stage primaryStage, Parent root, OnlineController netController){
        this.netController = netController;
        statistics = (TextArea) root.lookup("#statArea");
        logs = (TextArea) root.lookup("#logsArea");
        controller = this;
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        logs.setEditable(false);
        statistics.setEditable(false);

        //logs.setFocusTraversable(false);
        statistics.setFocusTraversable(false);
        this.primaryStage = primaryStage;
        this.root = root;
    }

    public Message MakeShotMy(int x, int y){
        Message msg = new Message(netController.name);
        msg.x = x;
        msg.y = y;
        msg.code = 1;
        if(ocean.isGameOver() || restartMode) return null;

        msg.message += netController.partnerName + " shootAt: " + (x + 1) + " " + (y + 1) + "\n";
        int[] sunk = ocean.shootAt(x , y);
        msg.miss = !ocean.isOccupied(x, y);
        if(ocean.isOccupied(x , y)) {
            if(sunk != null)
                for (int i = 0; i < sunk.length / 2; ++i) {
                    Myfields.get(sunk[i * 2]).get(sunk[i * 2 + 1]).setStyle("-fx-background-color: #8B0000; -fx-border-color: #000000");
                    msg.sunk.add(sunk[i * 2]);
                    msg.sunk.add(sunk[i * 2 + 1]);
                }
            else
                Myfields.get(x).get(y).setStyle("-fx-background-color: #FF4500; -fx-border-color: #000000");
        }
        else
            Myfields.get(x).get(y).setStyle("-fx-background-color: #00008B; -fx-border-color: #000000");

        msg.statistics += ocean.statistic();

        msg.message += ocean.logs + "\n";
        if(ocean.isGameOver()) {
            msg.message += netController.partnerName + " win!!!\n";
        }
        logs.appendText(msg.message);
        msg.isGameOver = ocean.isGameOver();
        netController.isMyStep = true;
        return msg;
    }

    public void MakeShot(int x, int y){
        if(ocean.isGameOver() || restartMode) return;

        var msg = netController.sendShot(x, y);
    }

    public void shotResult(Message msg){
        if(!msg.miss) {
            if(msg.sunk.size() > 0)
                for (int i = 0; i < msg.sunk.size() / 2; ++i)
                    fields.get(msg.sunk.get(i * 2)).get(msg.sunk.get(i * 2 + 1)).setStyle("-fx-background-color: #8B0000; -fx-border-color: #000000");
            else
                fields.get(msg.x).get(msg.y).setStyle("-fx-background-color: #FF4500; -fx-border-color: #000000");
        }
        else
            fields.get(msg.x).get(msg.y).setStyle("-fx-background-color: #00008B; -fx-border-color: #000000");

        statistics.setText(msg.statistics);
        logs.appendText(msg.message);
        netController.isMyStep = false;
    }

    public void restart(){
        RestartController restContr = new RestartController(this);
        restContr.RestartMode();
    }

    void GameMode(){
        restartMode = false;

        Button edit = (Button)root.lookup("#edit");
        edit.setVisible(true);
        edit.setOnAction(event -> restart());
        Button ready = (Button) root.lookup("#ready");
        ready.setVisible(true);
        ready.setOnAction(event -> {
            isReady = !isReady;
            ready.setStyle(isReady ? "-fx-background-color: #00FF00; -fx-border-color: #000000;" :
                    "-fx-background-color: #FF0000; -fx-border-color: #000000;");
        });

        logs.clear();
        statistics.clear();
        TextField editX = (TextField) root.lookup("#editX");
        TextField editY = (TextField) root.lookup("#editY");
        editX.setEditable(true);
        editY.setEditable(true);

        for (int i = 0; i < 10; ++i)
            for (int j = 0; j < 10; ++j)
                setGameAction(i, j);
    }

    private void setGameAction(int x, int y){
        fields.get(x).get(y).setStyle("-fx-background-color: #00FFFF; -fx-border-color: #000000;");
        if(!ocean.isOccupied(x,y))
            Myfields.get(x).get(y).setStyle("-fx-background-color: #00FFFF; -fx-border-color: #000000;");
        else
            Myfields.get(x).get(y).setStyle("-fx-background-color: #00FF00; -fx-border-color: #000000;");

        fields.get(x).get(y).setOnAction(new EventHandler<ActionEvent>() {
            boolean flag = true;
            @Override
            public void handle(ActionEvent event) {
                if(!netController.isMyStep) {
                    printLog("Not your step");
                    return;
                }

                if(!ocean.isGameOver() && flag) {
                    MakeShot(x, y);
                    flag = false;
                }
                else if(!ocean.isGameOver() && !flag)
                    logs.appendText("You've already fired here!\n");
            }
        });
    }

    public void printLog(String text){
        logs.appendText("\n" + text + "\n");
    }

    public void setInvisible(boolean flag){
        root.lookup("#edit").setVisible(flag);
        root.lookup("#ready").setVisible(flag);
    }

}
