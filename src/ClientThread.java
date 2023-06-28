import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class ClientThread extends Thread{
    public Socket socket;
    public String name;
    public Map<String, Socket> clients;
    public Map<String, ArrayList<String>> groups;

    public ClientThread(Map<String, Socket> clients, Map<String, ArrayList<String>> groups,
                        String name, Socket socket) throws SQLException, IOException {
        this.clients = clients;
        this.groups = groups;
        this.name = name;
        this.socket = socket;

        //登录时进行离线信息处理
        MessageOutput messageOutput = new MessageOutput(this.name);
        ArrayList messagesList = messageOutput.messagesOUT();
        if(!messagesList.isEmpty())
        {
            for(Object message : messagesList)
            {
                //传入离线时收到的信息
                new DataOutputStream(socket.getOutputStream()).writeUTF(message.toString());
            }
        }
    }

    public void run(){
        try{
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            while (true){
                String msg_from = null;

                //检测客户端是否关闭
                try {
                    msg_from = dis.readUTF();
                }catch (IOException e){
                    //关闭相关流
                    socket.close();
                    clients.remove(name);
                    dis.close();
                    is.close();
                    dos.close();
                    os.close();
                    System.out.println(name+"断开连接");
                    break;
                }

                //规定消息的格式是nameto#msg，#后是消息内容，前是目标的名字
                String[] parts = msg_from.split("#");

                //注意，这里一开始没有设计好所以逻辑有点复杂，会有多层的if存在
                //注意，谨记信息构成的格式
                //先检查是否是群聊消息
                if(groups.containsKey(parts[0])){
                    //为信息再加一层包装，说明是哪个群的消息
                    String msg_to = parts[0] + "#" + this.name + ":" + parts[1];

                    //规定五个连续的*是请求历史消息
                    if(parts[1].equals("*****")){
                        History.OUT(parts[0], this.socket);
                        break;
                    }

                    //写入历史消息记录
                    History.IN(parts[0], msg_to);

                    for(String name : groups.get(parts[0]))
                    {
                        //检查该成员是否在线，且不给自己发送
                        if(clients.containsKey(name) & !name.equals(this.name))
                        {
                            Socket socket0 = clients.get(name);
                            new DataOutputStream(socket0.getOutputStream()).writeUTF(msg_to);
                        }
                    }
                    continue;
                }

                //再检查是否是私聊，对方是否在线
                if(clients.containsKey(parts[0])){
                    //构造发出的消息内容是namefrom#msg，#后是消息内容，前是来源的名字
                    String msg_to = this.name + "#" + parts[1];

                    Socket socket = clients.get(parts[0]);
                    new DataOutputStream(socket.getOutputStream()).writeUTF(msg_to);
                }else {
                    //系统消息0是提醒对方下线
                    dos.writeUTF("系统消息#0");
                    MessageStorage messageStorage = new MessageStorage(this.name);
                    messageStorage.messageIN(parts[1], parts[0]);
                }
            }


        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
