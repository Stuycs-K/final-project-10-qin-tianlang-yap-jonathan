import java.nio.file.Files;
import java.nio.file.Paths;

public class Hexdump {
    public static void main(String[] args) throws Exception{
        byte[] arr = Files.readAllBytes(Paths.get(args[0]));
        
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(String.format("%02x ", b));
        }
        System.out.println(sb.toString());
    }
}