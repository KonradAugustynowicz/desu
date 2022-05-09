import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final Scanner sc = new Scanner(System.in);


    public static void main(String[] args) throws IOException {
        System.out.println("1 - Szyfrowanie ");
        System.out.println("2 - Deszyfrowanie ");
        int menuChoice = Integer.parseInt(sc.nextLine());

        if (menuChoice == 1) {
            processFileEncoding();
        }else {
            processFileDecoding();
        }
    }

    private static void processFileEncoding() throws IOException {
        System.out.println("Podaj 64-bitowy klucz w hex: ");
        String klucz = sc.nextLine();

        System.out.println("Podaj nazwe pliku z wiadomoscia:");
        String fileInName = sc.nextLine();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileInName));
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream("encoded.bin"));

        byte[] buffer = new byte[8];
        while (inputStream.read(buffer) != -1) {
            String hexInput = bytesToHex(buffer);
            String encrypted = processEncryption(hexInput, klucz);

            byte[] encryptedBytes = decodeHexString(encrypted);
            outStream.write(encryptedBytes);
        }
        inputStream.close();
        outStream.flush();
        outStream.close();
    }

    private static void processFileDecoding() throws IOException {
        System.out.println("Podaj 64-bitowy klucz w hex: ");
        String klucz = sc.nextLine();

        System.out.println("Podaj nazwe pliku z zakodowana wiadomoscia:");
        String fileInName = sc.nextLine();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileInName));
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream("decoded.bin"));

        byte[] buffer = new byte[8];
        try {

            while (inputStream.read(buffer) != -1) {
                String hexInput = bytesToHex(buffer);
                String decrypted = processDecrypt(hexInput, klucz);
                byte[] decryptedBytes = decodeHexString(decrypted);
                outStream.write(decryptedBytes);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            inputStream.close();
            outStream.flush();
            outStream.close();
        }
    }

    public static String AsciiToBinaryString(String asciiString) {

        byte[] bytes = asciiString.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    public static boolean[] binaryStringToBooleanArray(String booleanString) {
        StringBuilder sb = new StringBuilder(booleanString);
        while (sb.length() < 64) {
            sb.insert(0, "0");
        }
        boolean[] result = new boolean[64];
        for (int i = 0; i < 64; i++) {
            result[i] = sb.charAt(i) == '1';
        }
        return result;
    }

    public static String processEncryption(String plaintext, String key) {
        boolean[] input = hexToBooleanArray(plaintext);
        boolean[] k = hexToBooleanArray(key);

        DESKeys DESKeys = new DESKeys(k);                                                               //

        boolean[] output = DES.encrypt(input, DESKeys.getKeys());
        return booleanArrayToHexString(output);
    }

    public static String processDecrypt(String encryptedText, String key) {
        boolean[] input = hexToBooleanArray(encryptedText);
        boolean[] k = hexToBooleanArray(key);
                                                                                                        //
        DESKeys DESKeys = new DESKeys(k);

        boolean[] output = DES.decrypt(input, DESKeys.getKeys());
        return booleanArrayToHexString(output);
    }

    private static boolean[] hexToBooleanArray(String hexString) {
        BigInteger numberValue = new BigInteger(hexString, 16);
        String binaryString = numberValue.toString(2);
        return binaryStringToBooleanArray(binaryString);
    }

    private static String booleanArrayToHexString(boolean[] array) {
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < 64; i++)
            binaryString.append(array[i] ? '1' : '0');

        BigInteger decimal = new BigInteger(binaryString.toString(), 2);
        StringBuilder hexString = new StringBuilder(decimal.toString(16).toUpperCase());

        while (hexString.length() < 16) {
            hexString.insert(0, "0");
        }
        return hexString.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }
}
