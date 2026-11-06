import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

public class BattleJoker extends Application {

    public static Client client;

    public static void endProgram() {
        //Stop the other threads if it has
        client.disconnect();
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            GetNameDialog dialog = new GetNameDialog();
            GameWindow win = new GameWindow(primaryStage);

            //set the name
            String username = dialog.getPlayername();
            win.setName(username);

            client.send(GameEngine.NAME,username);
            Database.connect();


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() {
        try {
            Database.disconnect();
        } catch (SQLException ignored) {
        }
    }

    public static void main(String[] args) {
        System.setErr(new FilteredStream(System.err));      // All JavaFX'es version warnings will not be displayed

        client = new Client(System.getProperty("battlejoker.server.host", "127.0.0.1"),
                Integer.getInteger("battlejoker.server.port", 15556));

        try {
            client.connect();
            launch();
        } catch (IOException ex) {
            System.out.println("Unable to connect to the server. Try it later.");
        }

    }

    static class FilteredStream extends PrintStream {

        public FilteredStream(OutputStream out) {
            super(out);
        }

        @Override
        public void println(String x) {
            if (x != null && !x.contains("SLF4J: "))
                super.println(x);
        }

        @Override
        public void print(String x) {
            if (x != null && !x.contains("WARNING: Loading FXML document with JavaFX API of version 18"))
                super.print(x);
        }
    }
}
