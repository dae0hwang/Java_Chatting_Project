import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

class ServerHandler implements Runnable {
    static Socket sock;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    public void run() {
        InputStream fromServer = null;
        DataInputStream dataInputStream;
        ServerHandlerService serverHandlerService = new ServerHandlerService();
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dataInputStream = new DataInputStream(fromServer);

                byte[] inputMessageHeader = serverHandlerService.recieveMessageHeader(dataInputStream);
                HeaderInformation headerInformation
                    = serverHandlerService.implementHeaderInformation(inputMessageHeader);
                int messageBodyLength = headerInformation.messageBodyLength;
                Type messageBodyType = headerInformation.messageBodyType;
                byte[] receiveMessageBodyBytes
                    = serverHandlerService.recieveMessageBodyBytes(dataInputStream, messageBodyLength);
                switch (messageBodyType) {
                    case MESSAGETOCLIENT:
                        serverHandlerService.printNameAndMessage(receiveMessageBodyBytes);
                        break;
                    case CLIENTCLOSEMESSAGE:
                        serverHandlerService.printCloseMessage(receiveMessageBodyBytes);
                        break;
                    case IMAGETOCLIENT:
                        ImageBytesAndDirectory imageBytesAndDirectory
                            = serverHandlerService.saveImageInformation(sock, receiveMessageBodyBytes);
                        serverHandlerService.makeImageFile(imageBytesAndDirectory);
                        String fileName = serverHandlerService.returnFileName(imageBytesAndDirectory);
                        serverHandlerService.openImageFile(fileName);
                        break;
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