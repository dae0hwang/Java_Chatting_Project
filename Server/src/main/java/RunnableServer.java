import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableServer implements Runnable {
    protected Socket sock;
    protected static HashMap<Socket, Integer> clients = new HashMap<>();
    //client number of sendmessage
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();

    protected ObjectMapper objectMapper = new ObjectMapper();

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
                byte[] _receiveBytes;
                _receiveBytes = new byte[length];
                String json = dis.readUTF();
                JsonNode jsonNode = objectMapper.readTree(json);
                _receiveBytes = jsonNode.get("bytes").binaryValue();

//                String receiveMessage = jsonNode.get()
//                dis.readFully(receiveBytes, 0, length);
                String receiveMessage = new String(_receiveBytes);
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
                    int serverLength = _receiveBytes.length;
                    int serverType = Type.MESSAGETOCLIENT.getValue();
                    Header.encodeHeader(serverLength, serverType);
                    serverHeader = Header.bytesHeader;
                    for (Socket s : clients.keySet()) {
                        //each client recieveNum ++
                        recieveNumPlus(s);
                        toClient = s.getOutputStream();
                        dos = new DataOutputStream(toClient);
                        Body body = new Body();
                        body.setName(name);
                        body.setBytes(_receiveBytes);
                        dos.write(serverHeader, 0, 8);
                        String json2 = objectMapper.writeValueAsString(body);
                        dos.writeUTF(json2);
//                        dos.writeUTF(name);
//                        dos.write(receiveBytes, 0, length);
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
                    String json = objectMapper.writeValueAsString(body);
                    dos.writeUTF(json);
//                    dos.writeUTF(name);
//                    dos.writeInt(sendNum.get());
//                    dos.writeInt(recieveNum);
                    dos.flush();
                }
            } catch (IOException ex) {
            }
        }
    }
}