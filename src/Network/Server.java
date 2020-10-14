package Network;

import GUIelements.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Online {
    ServerSocket serverSocket;

    public Server(int port, OnlineController controller) throws IOException {
        super(controller);
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void interrupt() {
        try {
            serverSocket.close();
            clientSocket.close();
        }catch (Exception ex){}
        occupedAnswer.interrupt();
        super.interrupt();
    }

    @Override
    public void run() {
        meeting();
    }

    @Override
    public void meeting() {
        System.out.println(this.getClass() + " " + "METHOD meeting");
        Message msg;
        try{
            while (true) {
                clientSocket = serverSocket.accept();
                try {
//                    input = new ObjectInputStream(clientSocket.getInputStream());
//                    output = new ObjectOutputStream(clientSocket.getOutputStream());
                    msg = recieveMessage();
                    partnerName = msg.name;
                    if(partnerName.equals("")){
                        msg.name = controller.name;
                        msg.code = -1;
                        sendMessage(msg);
                        continue;
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    return;
                }
                boolean flag = msg.ready;
                occupedAnswer.start();
                placement(false);
                break;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }
    }

    @Override
    public void placement(boolean flag) {
        System.out.println(this.getClass() + " " + "METHOD placement");
        while(!Controller.controller.isReady || !flag){
            try {
                Thread.sleep(50);
                var msg = new Message(controller.name);
                msg.ready = Controller.controller.isReady;
                sendMessage(msg);
                msg = recieveMessage();
                flag = msg.ready;
            }catch (Exception ex){}
        }
        try {
            var msg = new Message(controller.name);
            msg.ready = true;
            sendMessage(msg);
        }catch (Exception ex){return;}
        Controller.controller.setInvisible(false);
        game();
    }

//    @Override
//    public void game(){
//        var msg = new Message(controller.name);
//        msg.code = 1;
//        msg.message = "start";
//
//        try {
//            output.writeObject(msg);
//        }catch (Exception ex){return;}
//
//        while (!msg.isGameOver){
//            try {
//                msg = (Message) input.readObject();
//            }catch (Exception ex){return;}
//        }
//        try {
//            clientSocket.close();
//        }catch (Exception ex){}
//    }


    Thread occupedAnswer = new Thread(){
        @Override
        public void run() {
            while (true){
                try(Socket soc = serverSocket.accept()) {
                    Message msg = new Message(controller.name);
                    msg.message = "occuped";
                    msg.code = -1;
                    new ObjectOutputStream(soc.getOutputStream()).writeObject(msg);
                }catch (Exception ex){}
            }
        }
    };
}
