import typedata.InputStringAndType;
import typedata.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 5510);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            ServerHandler handler = new ServerHandler(socket);
            Thread receiveThread = new Thread(handler);
            receiveThread.start();
            ClientService clientService = new ClientService(socket);
            System.out.print("Register your name: ");
            byte[] resisterNameJsonBytes = clientService.implementResisterNameJsonBytes(bufferedReader);
            byte[] resisterNameHeader = clientService.implementResisterNameHeader(resisterNameJsonBytes);
            clientService.sendResisterName(clientService.dataOutputStream, resisterNameHeader, resisterNameJsonBytes);
            while (true) {
                InputStringAndType inputStringAndType = clientService.storeInputStringAndSetType(bufferedReader);
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
