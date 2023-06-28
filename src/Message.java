public class Message {
    private String name_from;
    private String message;

    public Message(String name_from, String message){
        this.name_from = name_from;
        this.message = message;
    }

    public String toString(){
        return name_from + "#" + message;
    }
}
