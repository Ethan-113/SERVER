import java.sql.*;

public class MessageStorage {
    public String name_self;

    public MessageStorage(String name_self){
        this.name_self = name_self;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toSignal(),
                "root","admin");
    }

    //将离线信息输入到数据库中，name_opposite为对方账号的name
    public void messageIN(String message, String name_opposite){
        String empty = "#";
        String message_now;
        String sql_messageNOW = "select * from " + name_opposite + " where name = '" + name_self + "'";

        try(Connection c =getConnection();
            Statement messageIN = c.createStatement();
            Statement messageNOW = c.createStatement())
        {
            ResultSet rs = messageNOW.executeQuery(sql_messageNOW);
            rs.next();
            message_now = rs.getString("message");
            if(!message_now.equals(empty))
                message = message_now + "\n" + message;

            String sql_messageIN = "update "+name_opposite+" set message = '"+message+"' where name = '"+name_self+"'";
            messageIN.execute(sql_messageIN);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
