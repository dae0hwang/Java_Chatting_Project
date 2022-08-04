import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableServer implements Runnable {
    protected Socket sock;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected static HashMap<Socket, Integer> clients = new HashMap<>();
    //client number of sendmessage
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();

//    ReentrantLock lock = new ReentrantLock();

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    //recieveMessage num++
    private void recieveNumPlus(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }


   @Override
    public void run() {
        sendNum.set(0);
        InputStream fromClient ;
        OutputStream toClient;
        DataInputStream dis ;
        DataOutputStream dos;
        String name = null;

        try {
            System.out.println(sock + ": Connection");
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
                byte[] receiveBytes;
                receiveBytes = new byte[length];
                String inputJson = dis.readUTF();
                JsonNode jsonNode = objectMapper.readTree(inputJson);
                receiveBytes = jsonNode.get("bytes").binaryValue();
                String receiveMessage = new String(receiveBytes);
                System.out.println("from Client : "+receiveMessage);

                //type =1111 -> name
                if (type == Type.RESISTERNAME.getValue()) {
                    name = receiveMessage;
                }
                //type =2222-> send to other clients(type=3333)
                else if (type == Type.MESSAGETOSERVER.getValue()) {
                    //sendmessage num ++
                    sendNum.set(sendNum.get() + 1);
                    byte[] serverHeader;
                    int serverLength = receiveBytes.length;
                    int serverType = Type.MESSAGETOCLIENT.getValue();
                    Header.encodeHeader(serverLength, serverType);
                    serverHeader = Header.bytesHeader;
                    for (Socket s : clients.keySet()) {
                        //each client recieveNum ++
                        recieveNumPlus(s);
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
            System.out.println(sock + ": Errof(" + ex + ")");
            System.out.println(name+" is out ");

        } finally {
            try {
                int recieveNum = clients.get(sock);
                clients.remove(sock);

                //type =4444 -> socket.close
                byte[] serverHeader;
                int serverLength = 0;
                int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
                Header.encodeHeader(serverLength,serverType);
                serverHeader = Header.bytesHeader;
                for (Socket s : clients.keySet()) {
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
            } catch (IOException ex) {
            }
        }
    }
}