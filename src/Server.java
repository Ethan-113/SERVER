import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Server {
    public static Map<String, Socket> clients = new HashMap<>();
    public static Map<String, ArrayList<String>> groups = new HashMap<>();
    public static Map<String, ClientThread> clientPool = new HashMap<>();


    public static void main(String[] args) throws IOException, SQLException {
        //事先加载所有群
        CheckThread checkThread = new CheckThread();
        checkThread.start();

        ServerSocket server = new ServerSocket(1027);
        while (true){
            Socket socket = server.accept();
            String name = new DataInputStream(socket.getInputStream()).readUTF();

            //账户没有被顶登录的情况直接创建
            if(!clients.containsKey(name)) {
                //加入新用户
                clients.put(name, socket);
                //创建用户
                ClientThread clientThread = new ClientThread(clients, groups, name, socket);
                clientPool.put(name, clientThread);
                clientThread.start();

                System.out.println("用户连接" + name);
            }
            //账户被顶登录的情况要先关闭原先的所有，再连接新的账户
            else {
                Socket s = clients.get(name);
                //系统消息1是提醒顶号
                new DataOutputStream(s.getOutputStream()).writeUTF("系统消息#1");
                //关闭socket和线程，然后从线程池和socket池中除去这两项
                s.close();
                clients.remove(name);
                ClientThread old = clientPool.get(name);
                clientPool.remove(name);
                old.interrupt();

                //加入新用户
                clients.put(name, socket);
                //创建用户
                ClientThread clientThread = new ClientThread(clients, groups, name, socket);
                clientPool.put(name, clientThread);
                clientThread.start();

                System.out.println("用户连接" + name);
            }
        }
    }
}