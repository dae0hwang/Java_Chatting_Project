package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataOutputStreamFactory {

    public DataOutputStream createDataOutputStream(Socket socket) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        return dataOutputStream;
    }
}
