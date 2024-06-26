import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class AES {

    private static final int[][] s_box = new int[][] {
            { 0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76 },
            { 0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0 },
            { 0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15 },
            { 0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75 },
            { 0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84 },
            { 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf },
            { 0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8 },
            { 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2 },
            { 0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73 },
            { 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb },
            { 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79 },
            { 0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08 },
            { 0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a },
            { 0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e },
            { 0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf },
            { 0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 },
    };

    public static void main(String[] args) throws IOException {
        testShiftRow();

        // https://github.com/francisrstokes/githublog/blob/main/2022/6/15/rolling-your-own-crypto-aes.md
        byte[] input_text = Files.readAllBytes(Paths.get(args[0]));
        byte[] input_key = Files.readAllBytes(Paths.get(args[1]));

        int[] key = new int[input_key.length];
        for (int i = 0; i < input_key.length; i++) {
            key[i] = input_key[i] & 0xFF;
        }

        int[][] roundKeys = keyExpansion(key);

        byte[] padded_text = pad(input_text);
        byte[] cipher = new byte[padded_text.length];
        for (int k = 0; k < padded_text.length / 16; k++) {
            int[][] data = new int[4][4];
            for (int i = 0; i < 16; i++) {
                data[i / 4][i % 4] = padded_text[k * 16 + i] & 0xFF;
            }

            addRoundKey(data, roundKeys, 0);

            for (int round = 1; round < 10; round++) {
                // System.out.println(data.length + " " + data[0].length);
                subBytes(data);
                data = shiftRows(data);
                mixColumn(data);
                addRoundKey(data, roundKeys, round);
            }

            subBytes(data);
            data = shiftRows(data);
            addRoundKey(data, roundKeys, 10);

            for (int i = 0; i < 16; i++) {
                cipher[k * 16 + i] = (byte) data[i / 4][i % 4];
            }
        }

        Files.write(Paths.get("cipher_text.txt"), cipher);
    }

    // 128 bits key
    // ECB operation mode: encrypts each block on its own and displays the resultant
    // encrypted blocks one after another.

    public static byte[] pad(byte[] input) {
        int padding_length = 16 - (input.length % 16);
        byte[] padded_input = Arrays.copyOf(input, input.length + padding_length);
        for (int i = input.length; i < padded_input.length; i++) {
            padded_input[i] = (byte) padding_length;
        }
        return padded_input;
    }

    // subBytes
    public static void subBytes(int[][] data) {
        // for (int i = 0; i < s_box.length; i ++) {
        // System.out.print(s_box[0][i] + " ");
        // }

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                // System.out.println(data[i][j] / 16);
                data[i][j] = s_box[data[i][j] / 16][data[i][j] % 16];
            }
        }
    }

    // shiftRows
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
                    // out of bounds error
                    returnData[i][j - offset + 4] = data[i][j];
                } else {
                    returnData[i][j - offset] = data[i][j];
                }
            }
        }
        return returnData;
    }

    // mixColumn
    public static void mixColumn(int[][] data) {
        for (int i = 0; i < 4; i++) {
            int[] column = new int[4];
            for (int j = 0; j < 4; j++) {
                column[j] = data[j][i];
            }
            data[0][i] = gmul(0x02, column[0]) ^ gmul(0x03, column[1]) ^ column[2] ^ column[3];
            data[1][i] = column[0] ^ gmul(0x02, column[1]) ^ gmul(0x03, column[2]) ^ column[3];
            data[2][i] = column[0] ^ column[1] ^ gmul(0x02, column[2]) ^ gmul(0x03, column[3]);
            data[3][i] = gmul(0x03, column[0]) ^ column[1] ^ column[2] ^ gmul(0x02, column[3]);
        }
    }

    private static int gmul(int a, int b) {
        int result = 0;
        while (a != 0) {
            if ((a & 1) != 0) {
                result ^= b;
            }
            b = ((b << 1) & 0xFF) ^ (((b >>> 7) & 1) * 0x1B);
            a >>>= 1;
        }
        return result;
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
        int[] round_contants = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36 };
        temp[0] ^= round_contants[n - 1];

        return temp;
    }

    // addRoundKey
    public static void addRoundKey(int[][] data, int[][] expandedKey, int round) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[i][j] ^= expandedKey[i][round * 4 + j];
            }
        }
    }

    // delete later
    public static void testShiftRow() {
        int[][] idk = {
                { 74, 55, 12, 32 },
                { 64, 23, 93, 62 },
                { 67, 87, 34, 12 },
                { 54, 67, 87, 90 },
        };
        int[][] a = shiftRows(idk);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // System.out.print(a[i][j] + " ");
            }
            // System.err.println();
        }
    }

}