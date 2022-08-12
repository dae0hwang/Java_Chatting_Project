import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class RunnableServer implements Runnable {
    protected Socket sock;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected static HashMap<Socket, Integer> clients = new HashMap<>();
    //client number of send message
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();

    //recieveMessage num++
    private synchronized void receiveNumPlus(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    @Override
    public void run() {
        sendNum.set(0);
        InputStream fromClient;
        OutputStream toClient;
        DataInputStream dis;
        DataOutputStream dos;
        String name = null;
        //implement header for encoding and decoding.
        Header makeHeader = new Header();

        try {
            System.out.println(sock + ": 연결됨");
            while (true) {
                fromClient = sock.getInputStream();
                dis = new DataInputStream(fromClient);

                //first input message header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                makeHeader.decodeHeader(header);
                int length = makeHeader.messageLength;
                int type = makeHeader.messageType;

                //second input message body
                byte[] receiveBytes;
                receiveBytes = new byte[length];
                dis.readFully(receiveBytes,0,length);
                Body inputBody = objectMapper.readValue(receiveBytes, Body.class);
                byte[] contentsBytes = inputBody.getBytes();
                System.out.println("server message received message header and body");

                //process input data
                //type == 1111 -> set name
                if (type == Type.RESISTERNAME.getValue()) {
                    name = new String(contentsBytes);
                }
                //type == 2222 -> send string to clients
                //type == 5555 -> send image to clients
                else {
                    //sendmessage num ++
                    sendNum.set(sendNum.get() + 1);

                    //implement sendJsonBytes
                    byte[] sendJsonBytes = implementBody(name, contentsBytes);

                    //implement serverHeader
                    byte[] serverHeader = implementServerHeader(type, sendJsonBytes);

                    // send message to clients.
                    Server.lock.lock();
                    try {
                        for (Socket s : clients.keySet()) {
                            if (s != sock) {
                                //each client receiveNum ++
                                receiveNumPlus(s);
                                toClient = s.getOutputStream();
                                dos = new DataOutputStream(toClient);
                                dos.write(serverHeader, 0, 8);
                                dos.write(sendJsonBytes,0,sendJsonBytes.length);
                                dos.flush();
                            }
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

                //when socket is closed. server send information of client to others.
                //implement close message body.
                byte[] sendJsonBytes = implementCloseBody(name, sendNum.get(), recieveNum);

                //implement close message header.
                byte[] serverHeader = implementCloseHeader(sendJsonBytes);

                // send close message to clients.
                try {
                    for (Socket s : clients.keySet()) {
                        receiveNumPlus(s);
                        toClient = s.getOutputStream();
                        dos = new DataOutputStream(toClient);
                        dos.write(serverHeader, 0, 8);
                        dos.write(sendJsonBytes, 0, sendJsonBytes.length);
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

    private static byte[] implementBody(String name, byte[] contentsBytes) throws JsonProcessingException {
        Body outputBody = new Body();
        outputBody.setName(name);
        outputBody.setBytes(contentsBytes);
        return objectMapper.writeValueAsBytes(outputBody);
    }

    private static byte[] implementServerHeader(int type, byte[] sendJsonBytes) {
        Header makeHeader = new Header();
        int serverType = 0;
        int serverLength = sendJsonBytes.length;
        if (type == Type.MESSAGETOSERVER.getValue()) {
            serverType = Type.MESSAGETOCLIENT.getValue();
        } else if (type == Type.IMAGETOSERVER.getValue()) {
            serverType = Type.IMAGETOCLIENT.getValue();
        }
        makeHeader.encodeHeader(serverLength, serverType);
        return makeHeader.bytesHeader;
    }

    private static byte[] implementCloseBody(String name, int sendNum, int recieveNum) throws JsonProcessingException {
        Body outputBody = new Body();
        outputBody.setName(name);
        outputBody.setSendNum(sendNum);
        outputBody.setRecieveNum(recieveNum);
        return objectMapper.writeValueAsBytes(outputBody);
    }

    private static byte[] implementCloseHeader( byte[] sendJsonBytes) {
        Header makeHeader = new Header();
        int serverLength = sendJsonBytes.length;
        int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
        makeHeader.encodeHeader(serverLength, serverType);
        return makeHeader.bytesHeader;
    }

}