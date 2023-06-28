public class IPMysql {
    public String signal = "jdbc:mysql://10.28.227.44:3306/chat_users?characterEncoding=UTF-8";
    public String group = "jdbc:mysql://10.28.227.44:3306/chat_groups?characterEncoding=UTF-8";

    public String toSignal(){
        return signal;
    }

    public String toGroup(){return group;}
}
