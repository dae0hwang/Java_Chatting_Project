import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class RunnableServer implements Runnable {
    protected Socket sock;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected static HashMap<Socket, Integer> clients = new HashMap<>();
    //client number of sendmessage
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    //recieveMessage num++
    private synchronized void sendNumPlus(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }

    @Override
    public void run() {
        sendNum.set(0);
        InputStream fromClient = null;
        OutputStream toClient = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        String name = null;

        try {
            System.out.println(sock + ": 연결됨");
            while (true) {
                fromClient = sock.getInputStream();
                dis = new DataInputStream(fromClient);

                //first input header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                Header.decodeHeader(header);
                int length = Header.messageLength;
                int type = Header.messageType;

                //second input body message
                byte[] receiveBytes = null;
                receiveBytes = new byte[length];
                String inputJson = dis.readUTF();
                JsonNode jsonNode = objectMapper.readTree(inputJson);
                receiveBytes = jsonNode.get("bytes").binaryValue();
                String receiveMessage = new String(receiveBytes);
                System.out.println("서버 받앗는지 확인: "+receiveMessage);

                //type =1111 -> name
                if (type == Type.RESISTERNAME.getValue()) {
                    name = receiveMessage;
                }
                //type == 2222-> send to other clients
                //type == 5555 -> send image to clients
                else {
                    //sendmessage num ++
                    sendNum.set(sendNum.get() + 1);
                    byte[] serverHeader = new byte[8];
                    int serverLength = receiveBytes.length;
                    int serverType = 0;
                    if (type == Type.MESSAGETOSERVER.getValue()) {
                        serverType = Type.MESSAGETOCLIENT.getValue();
                    } else if (type == Type.IMAGETOSERVER.getValue()) {
                        serverType = Type.IMAGETOCLIENT.getValue();
                    }
                    Header.encodeHeader(serverLength, serverType);
                    serverHeader = Header.bytesHeader;
                    for (Socket s : clients.keySet()) {
                        //each client recieveNum ++
                        sendNumPlus(s);
                        toClient = s.getOutputStream();
                        dos = new DataOutputStream(toClient);
                        dos.write(serverHeader, 0, 8);
                        Body body = new Body();
                        body.setName(name);
                        body.setBytes(receiveBytes);
                        String toJson = objectMapper.writeValueAsString(body);
                        dos.writeUTF(toJson);
                        dos.flush();
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(sock + ": 에러(" + ex + ")");
            System.out.println(name+"나갔음. ");

        } finally {
            try {
                //remove lock
                int recieveNum = clients.get(sock);
                Server.lock.lock();
                try {
                    clients.remove(sock);
                }finally {
                    Server.lock.unlock();
                }
                //type =4444 -> socket.close
                byte[] serverHeader = new byte[8];
                int serverLength = 0;
                int serverType = 4444;
                Header.encodeHeader(serverLength,serverType);
                serverHeader = Header.bytesHeader;
                for (Socket s : clients.keySet()) {
                    sendNumPlus(s);
                    toClient = s.getOutputStream();
                    dos = new DataOutputStream(toClient);
                    dos.write(serverHeader, 0, 8);
                    Body body = new Body();
                    body.setName(name);
                    body.setSendNum(sendNum.get());
                    body.setRecieveNum(recieveNum);
                    String toJson2 = objectMapper.writeValueAsString(body);
                    dos.writeUTF(toJson2);
                    dos.flush();
                }
                fromClient = null;
                toClient = null;
            } catch (IOException ex) {
            }
        }
    }
}