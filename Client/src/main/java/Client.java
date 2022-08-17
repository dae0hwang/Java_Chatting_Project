import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
        Socket sock = null;
        try {
            sock = new Socket("127.0.0.1", 5510);
            //receive message Thread start
            ServerHandler handler = new ServerHandler(sock);
            Thread receiveThread = new Thread(handler);
            receiveThread.start();
            System.out.print("Register your name: ");
            sendResisterName(sock);
            while (true) {
                InputStringAndType inputStringAndType = storeInputStringAndSetType();
                //타입에따라 보내기.
                Type type = inputStringAndType.type;
                switch (type) {
                    case MESSAGETOSERVER : sendStringMessage(sock, inputStringAndType);
                        break;
                    case IMAGETOSERVER: sendImageMessage(sock, inputStringAndType);
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Connection termination (" + ex + ")");
        } finally {
            try {
                if (sock != null) {
                    sock.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private static void sendResisterName(Socket socket) throws IOException {
        OutputStream toServer = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(toServer);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        byte[] resisterName = br.readLine().getBytes();
        MessageBody messageBody = new MessageBody();
        messageBody.setBytes(resisterName);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(messageBody);
        int type = Type.RESISTERNAME.getValue();
        Header makeHeader = new Header();
        makeHeader.encodeHeader(sendJsonBytes.length,type);
        byte[] clientHeader = makeHeader.bytesHeader;
        dos.write(clientHeader,0,clientHeader.length);
        dos.write(sendJsonBytes, 0, sendJsonBytes.length);
        dos.flush();
    }

    private static InputStringAndType storeInputStringAndSetType() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputString = br.readLine();
        Type type;
        if (inputString.length() >= 8 && inputString.substring(0, 8).equals("image://")) {
            type = Type.IMAGETOSERVER;
        } else {
            type = Type.MESSAGETOSERVER;
        }
        InputStringAndType inputStringAndType = new InputStringAndType(inputString, type);
        return inputStringAndType;
    }

    static class InputStringAndType {
        String inputString;
        Type type;
        InputStringAndType(String inputString, Type type) {
            this.inputString = inputString;
            this.type = type;
        }
    }

    private static void sendStringMessage(Socket socket, InputStringAndType inputStringAndType)
        throws IOException {
        byte[] inputStringtobytes = inputStringAndType.inputString.getBytes("UTF-8");
        MessageBody messageBody = new MessageBody();
        messageBody.setBytes(inputStringtobytes);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(messageBody);
        Header makeHeader = new Header();
        makeHeader.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = makeHeader.bytesHeader;
        OutputStream toServer = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(toServer);
        dos.write(header, 0, header.length);
        dos.write(sendJsonBytes, 0, sendJsonBytes.length);
        dos.flush();
    }

    private static void sendImageMessage(Socket socket, InputStringAndType inputStringAndType)
        throws IOException {
        int filePathStartIdx = 8;
        String inputString = inputStringAndType.inputString;
        String filePath = inputString.substring(filePathStartIdx, inputString.length());
        File file = new File(filePath);
        byte[] imageFileBytes = Files.readAllBytes(file.toPath());
        MessageBody messageBody = new MessageBody();
        messageBody.setBytes(imageFileBytes);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(messageBody);
        Header makeHeader = new Header();
        makeHeader.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = makeHeader.bytesHeader;
        OutputStream toServer = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(toServer);
        dos.write(header, 0, header.length);
        dos.write(sendJsonBytes, 0, sendJsonBytes.length);
        dos.flush();
    }
}
