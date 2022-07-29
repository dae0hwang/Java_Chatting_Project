import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class RunnableServer implements Runnable {
    protected Socket sock;
    protected static ArrayList<Socket> clients = new ArrayList(5);
    //client number of sendmessage
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    public synchronized void remove(Socket socket) {
        for (Socket s : RunnableServer.clients) {
            if (socket == s) {
                RunnableServer.clients.remove(socket);
                break;
            }
        }
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
                dis.readFully(receiveBytes, 0, length);
                String receiveMessage = new String(receiveBytes);
                System.out.println("서버 받앗는지 확인: "+receiveMessage);

                //type =1111 -> name
                if (type == 1111) {
                    name = receiveMessage;
                }
                //type =2222-> send to other clients
                else if (type == 2222) {
                    //sendmessage num ++
                    sendNum.set(sendNum.get() + 1);
                    byte[] serverHeader = new byte[8];
                    int serverLength = receiveBytes.length;
                    int serverType = 3333;
                    Header.encodeHeader(serverLength, serverType);
                    serverHeader = Header.bytesHeader;
                    for (Socket s : clients) {
                        toClient = s.getOutputStream();
                        dos = new DataOutputStream(toClient);
                        dos.write(serverHeader, 0, 8);
                        dos.writeUTF(name);
                        dos.write(receiveBytes, 0, length);
                        dos.flush();
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(sock + ": 에러(" + ex + ")");
            System.out.println(name+"나갔음. ");

        } finally {
            try {
                if (sock != null) {
                    sock.close();
                    remove(sock);
                }
                //type =4444 -> socket.close
                System.out.println("4타입실행.");
                byte[] serverHeader = new byte[8];
                int serverLength = 0;
                int serverType = 4444;
                Header.encodeHeader(serverLength,serverType);
                serverHeader = Header.bytesHeader;
                for (Socket s : clients) {
                    toClient = s.getOutputStream();
                    dos = new DataOutputStream(toClient);
                    dos.write(serverHeader, 0, 8);
                    dos.writeUTF(name);
                    dos.writeInt(sendNum.get());
                    dos.flush();
                }
                fromClient = null;
                toClient = null;
            } catch (IOException ex) {
            }
        }
    }
}