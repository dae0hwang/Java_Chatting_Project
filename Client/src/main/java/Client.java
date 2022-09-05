import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

public class Client {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
        Socket socket = null;
//        ClientService clientService = new ClientService();
        try {
            socket = new Socket("127.0.0.1", 5510);
            //receive message Thread start
            ServerHandler handler = new ServerHandler(socket);
            Thread receiveThread = new Thread(handler);
            receiveThread.start();
            ClientService clientService = new ClientService(socket);
            System.out.print("Register your name: ");
            byte[] resisterNameJsonBytes = clientService.implementResisterNameJsonBytes();
            byte[] resisterNameHeader = clientService.implementResisterNameHeader(resisterNameJsonBytes);
            clientService.sendResisterName(clientService.dataOutputStream, resisterNameHeader, resisterNameJsonBytes);
            while (true) {
                InputStringAndType inputStringAndType = clientService.storeInputStringAndSetType();
                Type type = inputStringAndType.type;
                switch (type) {
                    case MESSAGETOSERVER :
                        clientService.sendStringMessage(clientService.dataOutputStream, inputStringAndType);
                        break;
                    case IMAGETOSERVER:
                        clientService.sendImageMessage(clientService.dataOutputStream, inputStringAndType);
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Connection termination (" + ex + ")");
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
            }
        }
    }
}
