import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private String ipAddress;
    private int tcpPort;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    public Client(String ipAddress, int tcpPort) {
        this.ipAddress = ipAddress;
        this.tcpPort = tcpPort;
    }

    public void connect() throws IOException {
        socket = new Socket(ipAddress, tcpPort);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    receive();
                }
            } catch (IOException ex) {
                System.out.println("Disconnected. Stop the receiver thread.");
            }
        });
        receiveThread.start();
    }

    public void disconnect() {
        try {
            socket.close();
            socket=null;
            in=null;
            out=null;

        } catch (IOException ex) {
            System.out.println("Disconnect!");
        }
    }


    public void send (int dataType, String data) throws IOException {
        out.writeInt(dataType);
        out.writeInt(data.length());
        out.write(data.getBytes(), 0, data.length());
        out.flush();

    }

    public void receive () throws IOException {
        int dataType =in.readInt();

        System.out.println("Receive data type: " + dataType);
        switch (dataType){
            case GameEngine.PUZZLE:
                handleReceivePuzzle();
                break;
                case GameEngine.GAMEOVER:
                    GameEngine.getInstance().gameOver=true;
                    break;
        }
    }

    public void handleReceivePuzzle() throws IOException {
      int rows= in.readInt();
      int cols= in.readInt();
      int[]puzzle= new int[rows*cols];

      for(int i=0;i<rows*cols;i++){
          puzzle[i]=in.readInt();
      }
        GameEngine engine= GameEngine.getInstance();
        engine.setBoard(puzzle);

        System.out.println(Arrays.toString(puzzle));


    }

    public static void main(String[] args) throws IOException {

        Client client = new Client("127.0.0.1", 15556);
        client.connect();
        client.send(0,"Hello!");
    }





}
