import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class Groups {
    public Map<String, ArrayList<String>> groups;

    public Groups(Map<String, ArrayList<String>> groups){
        this.groups = groups;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toGroup(),
                "root","admin");
    }

    //加载所有群聊信息到内存里
    public void load() throws SQLException {
        ArrayList<String> group_list = all_groups();
        Server.groups.clear();

        for(String name_group : group_list){
            ArrayList<String> members = all_members(name_group);
            Server.groups.put(name_group, members);
        }
    }

    //该群聊的所有成员
    private ArrayList<String> all_members(String originName_group) throws SQLException{
        //添上通用前缀
        String name_group = "__g__"+originName_group;

        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from "+name_group;

        try (Connection c = getConnection();
             Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while(rs.next())
            {
                String name = rs.getString("name_member");
                list.add(name);
            }
        }

        return list;
    }

    private ArrayList<String> all_groups() throws SQLException{
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from groups_list";

        try(Connection c = getConnection();
            Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next())
            {
                String name = rs.getString("group_name");
                list.add(name);
            }
        }

        return list;
    }
}
