import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckThread extends Thread{


    public CheckThread() {
    }

    public void run() {
        while (true) {
            Groups group = new Groups(Server.groups);

            try {
                group.load();

                History.Check(Server.groups);

                sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
