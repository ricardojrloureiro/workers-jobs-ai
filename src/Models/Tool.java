package Models;


import java.io.Serializable;

public class Tool  implements Serializable {

    public static int f1 = 1;
    public static int f2 = 2;
    public static int f3 = 3;
    public static int tool1 = 4;

    public int type;
    public Tool(int tool){
        this.type = tool;
    }

    public static int fromString(String s)
    {
        switch(s)
        {
            case "f1":
                return f1;
            case "f2":
                return f2;
            case "f3":
                return f3;
            case "tool1":
                return tool1;
            default:
                return -1;
        }

    }
}
