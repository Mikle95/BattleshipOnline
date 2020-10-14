package Network;

import GUIelements.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Online {
    public Client(String adress, int port, OnlineController controller) throws IOException {
        super(controller);
        clientSocket = new Socket(adress, port);
//        input = new ObjectInputStream(clientSocket.getInputStream());
//        output = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void interrupt() {
        try{
            clientSocket.close();
        } catch (Exception ex){}
        super.interrupt();
    }

    @Override
    public void run() {
        meeting();
    }

    @Override
    public void meeting() {
        System.out.println(this.getClass() + " " + "METHOD meeting");
        Message msg = new Message(controller.name);
        if(Controller.controller.isReady)
            Controller.controller.setInvisible(false);
        msg.ready = Controller.controller.isReady;
        try {
            sendMessage(msg);
            msg = recieveMessage();
            partnerName = msg.name;
            controller.partnerName = partnerName;
            msg.name = controller.name;
            msg.ready = Controller.controller.isReady;
            sendMessage(msg);
            if(msg.code < 0) return;
        }catch (Exception ex){return;}
        placement(false);
    }

    @Override
    public void placement(boolean flag) {
        System.out.println(this.getClass() + " " + "METHOD placement");
        while(!Controller.controller.isReady || !flag){
            try {
                Thread.sleep(50);
                var msg = recieveMessage();
                flag = msg.ready;
                msg = new Message(controller.name);
                msg.ready = Controller.controller.isReady;
                sendMessage(msg);
            }catch (Exception ex){
                ex.printStackTrace();
                interrupt();
            }
        }
        Controller.controller.setInvisible(false);
        game();
    }

    @Override
    public void game() {
        printLog("Your step");
        controller.isMyStep = true;
        super.game();
    }
}
