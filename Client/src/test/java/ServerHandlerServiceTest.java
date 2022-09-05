import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ServerHandlerServiceTest {
    DataInputStream dataInputStream;
    Socket socket;
    ServerHandlerService serverHandlerService;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        dataInputStream = mock(DataInputStream.class);
        socket = mock(Socket.class);
        serverHandlerService = new ServerHandlerService();

        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void recieveMessageHeader() throws IOException {
        //given
        byte[] header = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(header, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);

        //when
        byte[] expected = serverHandlerService.recieveMessageHeader(dataInputStream);

        //then
        assertArrayEquals(expected, header);
    }

    @Test
    void implementHeaderInformation() {
        //given
        byte[] headerBytes1 = {0, 0, 0, 8, 0, 0, 13, 5};
        HeaderInformation result1 = new HeaderInformation(8, Type.MESSAGETOCLIENT);
        byte[] headerBytes2 = {0, 0, 0, 8, 0, 0, 26, 10};
        HeaderInformation result2 = new HeaderInformation(8, Type.IMAGETOCLIENT);
        byte[] headerBytes3 = {0, 0, 0, 8, 0, 0, 17, 92};
        HeaderInformation result3 = new HeaderInformation(8, Type.CLIENTCLOSEMESSAGE);

        //when1
        HeaderInformation expected1 = serverHandlerService.implementHeaderInformation(headerBytes1);
        //then1
        assertEquals(expected1, result1);

        //when2
        HeaderInformation expected2 = serverHandlerService.implementHeaderInformation(headerBytes2);
        //then2
        assertEquals(expected2, result2);

        //when3
        HeaderInformation expected3 = serverHandlerService.implementHeaderInformation(headerBytes3);
        //then3
        assertEquals(expected3, result3);
    }

    @Test
    void recieveMessageBodyBytes() throws IOException {
        //given
        byte[] messageBodyBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(messageBodyBytes, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);

        //when
        byte[] expected = serverHandlerService.recieveMessageBodyBytes(dataInputStream, 8);

        //then
        assertArrayEquals(expected, messageBodyBytes);
    }

    @Disabled
    @Test
    void printNameAndMessage() throws IOException {
        //given
        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
        stringMessageBodyDto.setName("name");
        String message = "message";
        stringMessageBodyDto.setStringMessageBytes(message.getBytes("UTF-8"));
        byte[] messageBodyBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        String printResult = "name: message\n";

        //when
        serverHandlerService.printNameAndMessage(messageBodyBytes);

        //then
        assertEquals(outputStreamCaptor.toString(), printResult);

    }

    @Test
    void printCloseMessage() throws IOException {
        //given
        CloseMessageBodyDto closeMessageBodyDto = new CloseMessageBodyDto();
        closeMessageBodyDto.setName("name");
        closeMessageBodyDto.setSendNum(5);
        closeMessageBodyDto.setRecieveNum(10);
        byte[] closeMessageBytes = objectMapper.writeValueAsBytes(closeMessageBodyDto);
        String printResult = "name is out || Number of sendMessageNum: 5, Number of recieveMessageNum : 10\n";

        //when
        serverHandlerService.printCloseMessage(closeMessageBytes);

        //then
        assertEquals(outputStreamCaptor.toString(), printResult);
    }

    @Test
    void saveAndOpenImageFile() {

    }
}