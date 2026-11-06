import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class JokerServer {
    public static final int NAME = 0;
    public static final int COMMAND = 1;
    public static final int MESSAGE = 2;
    public static final int SETTING = 3;
    public static final int PUZZLE = 4;
    public static final int GAMEOVER = 5;

    public static Object lockObject= new Object();


//    HashMap<Socket, DataOutputStream> clients = new HashMap<>();
    HashMap<Socket, Player> clients = new HashMap<>();
    ServerEngine engine = new ServerEngine();

    public JokerServer() throws IOException {
        ServerSocket srvSocket = new ServerSocket(Integer.getInteger("battlejoker.server.port", 15556));


        while (true) {
            Socket socket = srvSocket.accept();

            Thread t = new Thread(() -> {
                Player player = new Player();
                try {
                    synchronized (lockObject){

                        player.out = new DataOutputStream(socket.getOutputStream());
//                        clients.put(socket, new DataOutputStream(socket.getOutputStream()));
                        clients.put(socket, player);

                    }
                    serve(player,socket);

                } catch (IOException ex) {
                    System.out.println("Disconnected..." + socket.getInetAddress());
                    synchronized (lockObject) {
                        clients.remove(socket);
                    }
                }
            });
            t.start();


        }
    }

    public void serve(Player player,Socket socket) throws IOException {
        DataInputStream in= new DataInputStream(socket.getInputStream());
        byte[] buffer = new byte[1024];

        DataOutputStream out= new DataOutputStream(socket.getOutputStream());
        sendPuzzleData(out);

        while (true) {

            int dataType =in.readInt();
            int len= in.readInt();
            in.read(buffer, 0, len);

            switch (dataType){
                case NAME:
                    handleName(new String(buffer, 0, len));
                    break;
                case COMMAND:
                    handleCommand(player,new String(buffer, 0, len));
                    break;
                case MESSAGE:
                    handleMessage(new String(buffer, 0, len));
                    break;
                case SETTING:
                    handleSetting(new String(buffer, 0, len));
                    break;
                default:
                    System.out.println("I get an unknown data type");
            }



            System.out.println("I get a data type "+dataType);
            System.out.println(new String(buffer, 0, len));

            }
        }

        public void handleName(String name){
            System.out.println("The player wants to set their name to "+name);
        }

    public void handleCommand(Player player,String command) throws IOException {
        System.out.println("The player wants to move the puzzle "+command);

       synchronized (lockObject){
        engine.movePuzzle(player,command);

        DataOutputStream out=null;
        for (Socket socket : clients.keySet()) { //send the puzzle to all clients
//            out=clients.get(socket);
            Player otherPlayer=clients.get(socket);
            out=otherPlayer.out;

            try{
                sendPuzzleData(out);
                /// send the current player information to other players

        }catch (IOException ex){
              clients.remove(socket); //remove the client if it is disconnected
            }
        }
        if (engine.getGameStatus()==true){
            sendGameOverSignal();
        }

       }
    }

    public void handleMessage(String message){
        System.out.println("The server wants to send a message: "+message);
    }

    public void handleSetting(String setting) {
        System.out.println("The server wants to change a setting: " + setting);
    }

    public void sendPuzzleData(DataOutputStream out) throws IOException {
        synchronized (lockObject) {
            out.writeInt(PUZZLE);
            out.writeInt(engine.SIZE);//row
            out.writeInt(engine.SIZE);
            System.out.printf(" Send puzzle with size %d x %d\n", engine.SIZE, engine.SIZE);

            for (int i = 0; i < engine.SIZE * engine.SIZE; i++) { //start sending the data
                out.writeInt(engine.board[i]);
            }
            System.out.println(Arrays.toString(engine.board));
            out.flush();
        }

    }
    public void sendGameOverSignal() throws IOException {
        synchronized (lockObject) {
            Iterator<Socket> iterator = clients.keySet().iterator();
            while (iterator.hasNext()) {
                Socket socket = iterator.next();
//                DataOutputStream out = clients.get(socket);
                Player player = clients.get(socket);
                DataOutputStream out = player.out;

                try {
                    out.writeInt(GAMEOVER);
                    out.flush();
                } catch (IOException ex) {
                    clients.remove(socket); // Remove the client if it is disconnected
                }
            }
        }
    }
        public static void main(String[] args) throws IOException {
            new JokerServer();

    }
}