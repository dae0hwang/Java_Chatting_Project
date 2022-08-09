import java.util.Arrays;

public class Header {
    static int messageLength;
    static int messageType;
    static byte[] bytesHeader = new byte[8];
    static byte[] lengthBytes = new byte[4];
    static byte[] typeBytes = new byte[4];

    //headerbyte -> int length and int type
    public static void decodeHeader(byte[] header) {
        byte[] lengthBytes = Arrays.copyOfRange(header, 0, 4);
        byte[] typeBytes = Arrays.copyOfRange(header, 4, 8);
        messageLength = byteArrayToInt(lengthBytes);
        messageType = byteArrayToInt(typeBytes);
    }

    //int -> byte[]
    public static void encodeHeader(int length, int type) {
        lengthBytes = intToByteArray(length);
        typeBytes = intToByteArray(type);
        bytesHeader[0] = lengthBytes[0];
        bytesHeader[1] = lengthBytes[1];
        bytesHeader[2] = lengthBytes[2];
        bytesHeader[3] = lengthBytes[3];
        bytesHeader[4] = typeBytes[0];
        bytesHeader[5] = typeBytes[1];
        bytesHeader[6] = typeBytes[2];
        bytesHeader[7] = typeBytes[3];
    }

    //int -> byte[4]
    public static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

    //byte[4] -> int
    public static int byteArrayToInt(byte bytes[]) {
        return ((((int)bytes[0] & 0xff) << 24) |
            (((int)bytes[1] & 0xff) << 16) |
            (((int)bytes[2] & 0xff) << 8) |
            (((int)bytes[3] & 0xff)));
    }
}