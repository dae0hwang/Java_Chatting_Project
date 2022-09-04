import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

public class Client {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
        Socket sock = null;
        ClientService clientService = new ClientService();
        try {
            sock = new Socket("127.0.0.1", 5510);
            //receive message Thread start
            ServerHandler handler = new ServerHandler(sock);
            Thread receiveThread = new Thread(handler);
            receiveThread.start();
            System.out.print("Register your name: ");
            clientService.sendResisterName(sock);
            while (true) {
                InputStringAndType inputStringAndType = clientService.storeInputStringAndSetType();
                Type type = inputStringAndType.type;
                switch (type) {
                    case MESSAGETOSERVER :
                        clientService.sendStringMessage(sock, inputStringAndType);
                        break;
                    case IMAGETOSERVER:
                        clientService.sendImageMessage(sock, inputStringAndType);
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
}
