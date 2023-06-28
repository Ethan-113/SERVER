import java.sql.*;
import java.util.ArrayList;

public class MessageOutput {
    public String name_self;

    public MessageOutput(String name_self){
        this.name_self = name_self;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toSignal(),
                "root","admin");
    }

    public ArrayList<Message> messagesOUT() throws SQLException{
        ArrayList<Message> messagesList = new ArrayList<>();
        String sql_message = "select * from " + name_self;
        //#代表空，没有收到该好友的离线信息
        String empty = "#";

        try (Connection c = getConnection();
             Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql_message);
            while (rs.next())
            {
                String name = rs.getString("name");
                String message = rs.getString("message");
                //不为空时
                if(!message.equals(empty))
                {
                    messagesList.add(new Message(name, message));
                    remake(name);
                }
            }
        }

        return messagesList;
    }

    //将目标好友的message栏位重新清空，变为‘#’
    public void remake(String name_opposite) throws SQLException{
        String sql_remake = "update "+ this.name_self +" set message = '#' where name = '"+name_opposite+"'";

        try (Connection c = getConnection();
             Statement s = c.createStatement())
        {
            s.execute(sql_remake);
        }
    }
}
