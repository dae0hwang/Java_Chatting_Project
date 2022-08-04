import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

// Thread for receive message from Server
class ServerHandler implements Runnable {
    private static ObjectMapper objectMapper = new ObjectMapper();
    Socket sock;

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    public void run() {
        InputStream fromServer = null;
        DataInputStream dis;
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dis = new DataInputStream(fromServer);

                //First input Header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                Header.decodeHeader(header);
                int length = Header.messageLength;
                int type = Header.messageType;
                byte[] receiveBytes = new byte[length];

                ///type = 3333이면 -> name and Message
                if (type == Type.MESSAGETOCLIENT.getValue()) {
                    String json = dis.readUTF();
                    JsonNode jsonNode = objectMapper.readTree(json);
                    String _name = jsonNode.get("name").asText();
                    byte[] _bytes = jsonNode.get("bytes").binaryValue();
                    String _receiveMessage = new String(_bytes);

//                    String name = dis.readUTF();
//                    dis.readFully(receiveBytes, 0, length);
//                    String receiveMessage = new String(receiveBytes);
                    System.out.println(_name + ": " + _receiveMessage);
                }
                //type = 4444 -> close Message
                else if (type == Type.CLIENTCLOSEMESSAGE.getValue()) {
                    String json = dis.readUTF();
                    JsonNode jsonNode = objectMapper.readTree(json);
                    String _name = jsonNode.get("name").asText();
                    int _sendNum = jsonNode.get("sendNum").asInt();
                    int _receiveNum = jsonNode.get("recieveNum").asInt();
//                    String name = dis.readUTF();
//                    int sendNum = dis.readInt();
//                    int receiveNum = dis.readInt();
                    System.out.println(_name + " is out || Number of sendMessageNum: " + _sendNum + ", Number of recieveMessageNum :"+ _receiveNum);
                }
            }
        } catch (IOException ex) {
            System.out.println("Connection termination (" + ex + ")");
        } finally {
            try {
                if (fromServer != null)
                    fromServer.close();
                if (sock != null)
                    sock.close();
            } catch (IOException ex) {
            }
        }
    }
}