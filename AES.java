import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AES {

    private static final int[][] s_box = new int[][]{
        {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
        {0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
        {0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
        {0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
        {0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
        {0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
        {0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
        {0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
        {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
        {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
        {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
        {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
        {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
        {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
        {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
        {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16},
    };

    public static void main(String[] args) throws IOException{
        testShiftRow();

        // https://github.com/francisrstokes/githublog/blob/main/2022/6/15/rolling-your-own-crypto-aes.md
        byte[] input_text = Files.readAllBytes(Paths.get(args[0]));
        byte[] input_key = Files.readAllBytes(Paths.get(args[1]));
        
        int[] text = new int[input_text.length];
        for (int i = 0; i < input_text.length; i++) {
            text[i] = input_text[i] & 0xFF;
        }

        // for (int i = 0; i < input_text.length; i++) {
        //     System.out.print(text[i] + " ");
        // }

        int[] key = new int[input_key.length];
        for (int i = 0; i < input_key.length; i++) {
            key[i] = input_key[i] & 0xFF;
        }

        // split original data to 16 byte sections, each 16 byte section will be stored in a 4x4 int[][], with each element storing one byte in hex. 
        for (int i = 0; i < 10; i++) {
            int[][] data = new int[4][4];
            subBytes(data);
            shiftRows(data);
            for (int j = 0; j < 4; j++) {
                int[] tempData = data[j];
                mixColumn(tempData);
            }
            addRoundKey(data , new int[1][1], i); // stub
        }

       
    }

    // 128 bits key
    // ECB operation mode:  encrypts each block on its own and displays the resultant encrypted blocks one after another.

    //subBytes
    public static void subBytes(int[][] data) {
       

        // for (int i = 0; i < s_box.length; i ++) {
        //     System.out.print(s_box[0][i] + " ");
        // }

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = s_box[data[i][j] / 16][ data[i][j] % 16];
            }
        }
    }

    //shiftRows
    public static int[][] shiftRows(int[][] data) {
        int[][] returnData = new int[4][4];
        // int i = 0;
        // int j = 0;
        // int[] tempnumbers = new int[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int offset = i;
                // tempnumbers[i + offset] = data[i][j];
                if (j - offset < 0) {
                    //out of bounds error
                    returnData[i][j - offset + 4] = data[i][j];
                }
                else {
                    returnData[i][j - offset] = data[i][j];
                }
            }
        }
        return returnData;
    }

    //mixColumn
    public static void mixColumn(int[] data) {
        
        int[] temp = {0, 0, 0, 0};
        temp[0] = gmul(0x02, data[0]) ^ gmul(0x03, data[1]) ^ data[2] ^ data[3];
        temp[1] = data[0] ^ gmul(0x02, data[1]) ^ gmul(0x03, data[2]) ^ data[3];
        temp[2] = data[0] ^ data[1] ^ gmul(0x02, data[2]) ^ gmul(0x03, data[3]);
        temp[3] = gmul(0x03, data[0]) ^ data[1] ^ data[2] ^ gmul(0x02, data[3]);

        for (int i = 0; i < 4; i++) {
            if (temp[i] > 256) {
                temp[i] -= 256;
            }
        }
        // for (int i = 0; i < 4; i++) {
        //     System.out.print(temp[i] + " ");
        //     System.err.println();
        // }
        // for (int i = 0; i < 4; i++) {
        //     for (int j = 0; j < 4; j++) {
        //         System.err.println(data[i][j]);
        //     }
        // }


        // for (int i = 0; i < 4; i++) {
        //     data[i] = temp[i];
        // }
    }

    private static int gmul(int a, int b) {
        int p = 0;
        int hiBitSet;
        for (int counter = 0; counter < 8; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            hiBitSet = (a & 0x80);
            a <<= 1;
            if (hiBitSet != 0) {
                a ^= 0x1b; // x^8 + x^4 + x^3 + x + 1
            }
            b >>= 1;
        }
        return p;
    }

    public static int[][] keyExpansion(int[] key) {
        int[][] expandedKey = new int[4][44];
        int[] temp = new int[4];
        int i = 0;

        while (i < 4) {
            expandedKey[0][i] = key[4 * i];
            expandedKey[1][i] = key[4 * i + 1];
            expandedKey[2][i] = key[4 * i + 2];
            expandedKey[3][i] = key[4 * i + 3];
            i++;
        }

        i = 4;
        while (i < 44) {
            for (int k = 0; k < 4; k++) {
                temp[k] = expandedKey[k][i - 1];
            }

            if (i % 4 == 0) {
                temp = keySchedule(temp, i / 4);
            }

            for (int k = 0; k < 4; k++) {
                expandedKey[k][i] = expandedKey[k][i - 4] ^ temp[k];
            }
            i++;
        }

        return expandedKey;
    }

    public static int[] keySchedule(int[] word, int n) {
        int[] temp = new int[4];

        temp[0] = word[1];
        temp[1] = word[2];
        temp[2] = word[3];
        temp[3] = word[0];

        for (int i = 0; i < 4; i++) {
            temp[i] = s_box[temp[i] / 16][temp[i] % 16];
        }
        int[] round_contants = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36};
        temp[0] ^= round_contants[n - 1];

        return temp;
    }

    //addRoundKey
    public static void addRoundKey(int[][] data, int[][] expandedKey, int round) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[i][j] ^= expandedKey[i][round * 4 + j];
            }
        }
    }

    //XOR
    /*
     * byte[] text = Files.readAllBytes(Paths.get(args[0]));
        byte[] temp = Files.readAllBytes(Paths.get(args[1]));
        byte[] key = new byte[text.length];
        int i = 0;
        while (i < text.length) {
            for (int j = 0; j < temp.length; j++) {
                if (i + j < text.length)
                    key[i + j] = temp[j];
            }
            i += temp.length;
        }
        byte[] result = new byte[text.length];
        for (int j = 0; j < text.length; j++) {
            result[j] = (byte) (text[j] ^ key[j]);
        }
        PrintWriter pw = new PrintWriter(args[2]);
        for (byte b : result) {
            pw.write(b);
        }
        pw.close();
     */

    //delete later 
    public static void testShiftRow() {
        int[][] idk = {
            {74, 55, 12, 32},
            {64, 23, 93, 62},
            {67, 87, 34, 12},
            {54, 67, 87, 90},
        };
        int[][] a = shiftRows(idk);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.err.println();
        }
    }

    public static void testMixColumn() {
        // int[][] idk = {
        //         { 0x87, 0xF2, 0x4D, 0x97 },
        //         { 0x6E, 0x4C, 0x90, 0xEC },
        //         { 0x46, 0xE7, 0x4A, 0xC3 },
        //         { 0xA6, 0x8C, 0xD8, 0x95 },
        // };
        int[] idk = {
        //  219, 19, 83, 69
         	// 242, 10, 34, 92 ,
         	// 1, 1, 1, 1 ,
        	// 198, 198, 198, 198	,
            45, 38, 49, 76
        };
        mixColumn(idk);
    }

    // public static int[][] test(int[][] data) {
    //     int[][] returnData = new int[4][4];
    //     // int i = 0;
    //     // int j = 0;
    //     // int[] tempnumbers = new int[4];
    //     for (int i = 0; i < 4; i++) {
    //         for (int j = 0; j < 4; j++) {
    //             int offset = i;
    //             // tempnumbers[i + offset] = data[i][j];
    //             if (j + offset >= 4) {
    //                 //out of bounds error
    //                 returnData[i][j + offset - 4] = data[i][j];
    //             }
    //             else {
    //                 returnData[i][j + offset] = data[i][j];
    //             }
    //         }
    //     }
    //     return returnData;
    // }
}

    // int[][] sbox_Inv = new int[][]{
    //     {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
    //     {0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
    //     {0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
    //     {0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
    //     {0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
    //     {0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
    //     {0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
    //     {0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
    //     {0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
    //     {0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
    //     {0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
    //     {0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
    //     {0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
    //     {0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
    //     {0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
    //     {0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
    // };