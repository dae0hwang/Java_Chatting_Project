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
            ServerHandler chandler = new ServerHandler(sock);
            Thread receiveThread = new Thread(chandler);
            receiveThread.start();

            //only first client send message type is 1111
            boolean first = true;
            int type;
            System.out.print("Register your name: ");

            while (true) {
                OutputStream toServer = sock.getOutputStream();
                DataOutputStream dos = new DataOutputStream(toServer);

                //String sendmessage or imagePath or name
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String sendMessage = br.readLine();
                byte[] outputBytes;
                byte[] header;

                if (first) {
                    type = Type.RESISTERNAME.getValue();
                } else if (sendMessage.length() >= 8 && sendMessage.substring(0,8).equals("image://")) {
                    type = Type.IMAGETOSERVER.getValue();
                }else {
                    type = Type.MESSAGETOSERVER.getValue();
                }

                //if Type == IMAGETOSERVER -> bytes = image file bytes
                if (type == Type.IMAGETOSERVER.getValue()) {
                    String path = sendMessage.substring(8, sendMessage.length());
                    File file = new File(path);
                    outputBytes = Files.readAllBytes(file.toPath());
                } else {
                    outputBytes = sendMessage.getBytes("UTF-8");
                }

                //implement message body
                Body body = new Body();
                body.setBytes(outputBytes);
                byte[] sendJsonBytes = objectMapper.writeValueAsBytes(body);

                //implenebt message header
                int length = sendJsonBytes.length;
                Header.encodeHeader(length, type);
                header = Header.bytesHeader;

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
}