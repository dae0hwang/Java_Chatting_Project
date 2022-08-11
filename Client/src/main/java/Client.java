import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
        Socket sock = null;
        Header makeHeader = new Header();
        try {
            sock = new Socket("127.0.0.1", 5510);

            //receive message Thread start
            ServerHandler handler = new ServerHandler(sock);
            Thread receiveThread = new Thread(handler);
            receiveThread.start();

            //only first client send message type is 1111
            boolean first = true;
            int type;
            System.out.print("Register your name: ");

            while (true) {
                OutputStream toServer = sock.getOutputStream();
                DataOutputStream dos = new DataOutputStream(toServer);

                //input data for Name or StringMessage or ImageFile
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String sendMessage = br.readLine();
                byte[] outputBytes;
                byte[] header;
                byte[] sendJsonBytes;

                //set Type
                type = setType(first, sendMessage);

                //convert String to bytes
                outputBytes = convertToBytes(type, sendMessage);

                //implement message body
                sendJsonBytes = implementMessageBody(outputBytes);

                //implement message header
                int length = sendJsonBytes.length;
                makeHeader.encodeHeader(length, type);
                header = makeHeader.bytesHeader;

                //send message to server
                dos.write(header, 0, 8);
                dos.write(sendJsonBytes, 0, length);
                dos.flush();

                //second send messaage is first => false
                first = false;
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

    private static int setType(boolean first, String sendMessage) {
        if (first) {
            return Type.RESISTERNAME.getValue();
        } else if (sendMessage.length() >= 8 && sendMessage.substring(0,8).equals("image://")) {
            return Type.IMAGETOSERVER.getValue();
        }else {
            return Type.MESSAGETOSERVER.getValue();
        }
    }

    private static byte[] convertToBytes(int type, String sendMessage) throws IOException {
        if (type == Type.IMAGETOSERVER.getValue()) {
            String path = sendMessage.substring(8, sendMessage.length());
            File file = new File(path);
            return Files.readAllBytes(file.toPath());
        } else {
            return sendMessage.getBytes("UTF-8");
        }
    }

    private static byte[] implementMessageBody(byte[] outputBytes) throws JsonProcessingException {
        Body body = new Body();
        body.setBytes(outputBytes);
        return objectMapper.writeValueAsBytes(body);
    }
}
