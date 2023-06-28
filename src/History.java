//群消息历史记录漫游
//此处用的是相对路径

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class History {
    //写入历史消息
    static void IN(String name, String msg){
        LocalDate today = LocalDate.now();
        //记录在当天的txt中
        String filePath = "JCHAT_history\\"+name+"\\"+today+".txt";

        try {
            File file = new File(filePath);

            if(!file.exists()){
                file.createNewFile();
            }

            //写入
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(msg);
            writer.newLine();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //读取并发送历史消息
    static void OUT(String name, Socket socket) throws IOException {
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        for (int i = 7; i>=0; i--){
            LocalDate day = LocalDate.now().minusDays(i);
            String filePath = "JCHAT_history\\"+name+"\\"+day+".txt";
            File file = new File(filePath);

            if(!file.exists()){
                continue;
            }

            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String msg;
            msg = name + "#" + day + "------消息如下------";
            dos.writeUTF(msg);
            while ((msg = reader.readLine()) != null){
                dos.writeUTF(msg);
            }
        }

        //每次请求就对记录进行一此清理
        String directoryPath = "JCHAT_history\\"+name;
        LocalDate today = LocalDate.now();
        // 一周前的日期
        LocalDate oneWeekAgo = today.minusWeeks(1);

        // 文件名日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    //截取名称前十位解析为日期
                    LocalDate fileDate = LocalDate.parse(fileName.substring(0, 10), formatter);

                    //如果该文件是一周前的，则删除
                    if (fileDate.isBefore(oneWeekAgo)) {
                        file.delete();
                    }
                }
            }
        }

        dos.close();
        os.close();
    }

    //在服务端创建群消息记录文件夹
    static void Check(Map<String, ArrayList<String>> map){
        Set<String> keySet = map.keySet();

        for(String name : keySet){
            String directoryPath = "JCHAT_history\\" + name;
            File directory = new File(directoryPath);
            if(!directory.exists()){
                directory.mkdir();
            }
        }
    }
}
