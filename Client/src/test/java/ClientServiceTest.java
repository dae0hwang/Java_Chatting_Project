import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {
    Socket socket;
    BufferedReader bufferedReader;
    ClientService clientService;
    DataOutputStream dataOutputStream;

    @BeforeEach
    void init() throws IOException {
        socket = mock(Socket.class);
        bufferedReader = mock(BufferedReader.class);
        clientService = new ClientService(socket);
        dataOutputStream = mock(DataOutputStream.class);
    }

//    @Test
//    void sendResisterName() throws IOException {
//        //given
//        clientService.sendResisterName(dataOutputStream);
//
//        when(bufferedReader.readLine()).thenReturn("name");
//        byte[] sendJsonBytes = {123, 34, 110, 97, 109, 101, 34, 58, 34, 110, 97, 109, 101, 34, 125};
//        byte[] clientHeader = {0, 0, 0, 15, 0, 0, 4, 87};
//
//        //when
//
//        //then
//        verify(dataOutputStream, times(1)).write(clientHeader, 0, clientHeader.length);
//        verify(dataOutputStream, times(1)).write(sendJsonBytes, 0, sendJsonBytes.length);
//        verify(dataOutputStream, times(1)).flush();
//    }

    @Test
    void storeInputStringAndSetType() {
    }

    @Test
    void sendStringMessage() {
    }

    @Test
    void sendImageMessage() {
    }
}