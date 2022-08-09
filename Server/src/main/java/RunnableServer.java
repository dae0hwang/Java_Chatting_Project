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
        InputStream fromClient;
        OutputStream toClient;
        DataInputStream dis;
        DataOutputStream dos;
        String name = null;

        try {
            System.out.println(sock + ": 연결됨");
            while (true) {
                fromClient = sock.getInputStream();
                dis = new DataInputStream(fromClient);

                //first input message header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                Header.decodeHeader(header);
                int length = Header.messageLength;
                int type = Header.messageType;

                //second input message body
                byte[] receiveBytes;
                receiveBytes = new byte[length];
                dis.readFully(receiveBytes,0,length);
                Body inputBody = objectMapper.readValue(receiveBytes, Body.class);
                byte[] contentsBytes = inputBody.getBytes();
                System.out.println("server message received messgae header and body");

                //type =1111 -> name
                if (type == Type.RESISTERNAME.getValue()) {
                    name = new String(contentsBytes);
                }
                //type == 2222-> send string to clients
                //type == 5555 -> send image to clients
                else {
                    //sendmessage num ++
                    sendNum.set(sendNum.get() + 1);
                    Body outputBody = new Body();
                    outputBody.setName(name);
                    outputBody.setBytes(contentsBytes);
                    byte[] sendJsonBytes = objectMapper.writeValueAsBytes(outputBody);
                    byte[] serverHeader ;
                    int serverLength = sendJsonBytes.length;
                    int serverType = 0;
                    if (type == Type.MESSAGETOSERVER.getValue()) {
                        serverType = Type.MESSAGETOCLIENT.getValue();
                    } else if (type == Type.IMAGETOSERVER.getValue()) {
                        serverType = Type.IMAGETOCLIENT.getValue();
                    }
                    Header.encodeHeader(serverLength, serverType);
                    serverHeader = Header.bytesHeader;
                    Server.lock.lock();
                    try {
                        for (Socket s : clients.keySet()) {
                            //each client recieveNum ++
                            sendNumPlus(s);
                            toClient = s.getOutputStream();
                            dos = new DataOutputStream(toClient);
                            dos.write(serverHeader, 0, 8);
                            dos.write(sendJsonBytes,0,serverLength);
                            dos.flush();
                        }
                    }finally {
                        Server.lock.unlock();
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
                Body outputBody = new Body();
                outputBody.setName(name);
                outputBody.setSendNum(sendNum.get());
                outputBody.setRecieveNum(recieveNum);
                byte[] sendJsonBytes = objectMapper.writeValueAsBytes(outputBody);
                byte[] serverHeader;
                int serverLength = sendJsonBytes.length;
                int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
                Header.encodeHeader(serverLength,serverType);
                serverHeader = Header.bytesHeader;
                Server.lock.lock();
                try {
                    for (Socket s : clients.keySet()) {
                        sendNumPlus(s);
                        toClient = s.getOutputStream();
                        dos = new DataOutputStream(toClient);
                        dos.write(serverHeader, 0, 8);
                        dos.write(sendJsonBytes, 0, serverLength);
                        dos.flush();
                    }
                }finally {
                    Server.lock.unlock();
                }
                fromClient = null;
                toClient = null;
            } catch (IOException ex) {
            }
        }
    }
}