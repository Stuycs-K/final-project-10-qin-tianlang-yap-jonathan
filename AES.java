import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AES {
    public static void main(String[] args) throws IOException{
        byte[] text = Files.readAllBytes(Paths.get(args[0]));
        byte[] key = Files.readAllBytes(Paths.get(args[1]));
    }

    // 128 bits key
    // ECB operation mode:  encrypts each block on its own and displays the resultant encrypted blocks one after another.

    //subBytes

    //shiftRows

    //mixColumn

    //addRoundKey


}