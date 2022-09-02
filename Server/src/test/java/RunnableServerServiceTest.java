import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RunnableServerServiceTest {

    ObjectMapper objectMapper;
    RunnableServerService serverService;
    //    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    Socket socket;
    HeaderConverter headerConverter;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        serverService = new RunnableServerService();
//        dataOutputStream = mock(DataOutputStream.class);
        dataInputStream = mock(DataInputStream.class);
        socket = mock(Socket.class);
        headerConverter = new HeaderConverter();
    }



    @Test
    void recieveMessageHeaderFromClient() throws IOException {
        //given
        byte[] header = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(header, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);

        //when
        byte[] expected = serverService.recieveMessageHeaderFromClient(dataInputStream);

        //then
        assertArrayEquals(expected, header);
    }

    @Test
    void receiveMessageBodyFromClient() throws IOException {

        byte[] header = {0, 0, 0, 8, 0, 0, 0, 0};
        byte[] receiveData = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(receiveData, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);
        //when
        byte[] recieveBytes = serverService.receiveMessageBodyFromClient(dataInputStream, header);

        //then
        assertArrayEquals(receiveData, receiveData);
    }

    @Test
    void readType() {
        //given
        byte[] header1 = {0, 0, 0, 0, 0, 0, 4, 87};
        Type result1 = Type.RESISTERNAME;

        byte[] header2 = {0, 0, 0, 0, 0, 0, 8, -82};
        Type result2 = Type.MESSAGETOSERVER;

        byte[] header3 = {0, 0, 0, 0, 0, 0, 21, -77};
        Type result3 = Type.IMAGETOSERVER;

        byte[] header4 = {0, 0, 0, 0, 0, 0, 26, 10};
        Type result4 = null;

        //when1
        Type expected1 = serverService.readType(header1);
        //then1
        assertEquals(expected1, result1);

        //when2
        Type expected2 = serverService.readType(header2);
        //then2
        assertEquals(expected2, result2);

        //when3
        Type expected3 = serverService.readType(header3);
        //then4
        assertEquals(expected3, result3);

        //when4
        Type expected4 = serverService.readType(header4);
        //then4
        assertEquals(expected4, result4);


    }

    @Test
    void resisterName() throws IOException {
        //given
        byte[] messageBody = {123, 34, 110, 97, 109, 101, 34, 58, 34, 110, 97, 109, 101, 34, 125};
        String reuslt = "name";

        //when
        String expectedName = serverService.resisterName(messageBody);

        //then
        assertEquals(expectedName, reuslt);
    }

//    @Test
//    void resisterName() throws IOException {
//        //given
//        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
//        resisterNameMessageBodyDto.setName("name");
//        byte[] messageBody = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);
//        String reuslt = "name";
//
//        //when
//        String expectedName = serverService.resisterName(messageBody);
//
//        //then
//        assertEquals(expectedName, reuslt);
//    }

    @Test
    void implementStringMessageJsonBytes() throws IOException {
        //given
        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
        String str = "hi";
        stringMessageBodyDto.setStringMessageBytes(str.getBytes());
        byte[] stringMessageBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
//        byte[] stringMessageBytes = {123, 34, 110, 97, 109, 101, 34, 58, 110, 117, 108, 108, 44, 34, 115, 116, 114,
//            105, 110, 103, 77, 101, 115, 115, 97, 103, 101, 66, 121, 116, 101, 115, 34, 58, 34, 97, 71, 107, 61, 34,
//            125};
        stringMessageBodyDto.setName("name");
        byte[] result = objectMapper.writeValueAsBytes(stringMessageBodyDto);

        //when
        byte[] expected = serverService.implementStringMessageJsonBytes(stringMessageBytes, "name");

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementStringMessageServerHeaderBytes() {
        //given
        byte[] stringMessageJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] reusultHeader = {0, 0, 0, 8, 0, 0, 13, 5};

        //when
        byte[] expectedHeader = serverService.implementStringMessageServerHeaderBytes(stringMessageJsonBytes);

        //then
        assertArrayEquals(expectedHeader, reusultHeader);
    }

    @Test
    void implementImageMessageJsonBytes() throws IOException {
        //given
        ImageMessageBodyDto imageMessageBodyDto = new ImageMessageBodyDto();
        byte[] imageBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        imageMessageBodyDto.setImageMessageBytes(imageBytes);
        byte[] imageMessageBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        imageMessageBodyDto.setName("name");
        byte[] result = objectMapper.writeValueAsBytes(imageMessageBodyDto);

        //when
        byte[] expected = serverService.implementImageMessageJsonBytes(imageMessageBytes, "name");

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementImageMessageServerHeaderBytes() {
        //given
        byte[] imageMessageJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] reusultHeader = {0, 0, 0, 8, 0, 0, 26, 10};

        //when
        byte[] expectedHeader = serverService.implementImageMessageServerHeaderBytes(imageMessageJsonBytes);

        //then
        assertArrayEquals(expectedHeader, reusultHeader);
    }

    @Test
    void implementCloseBody() throws JsonProcessingException {
        //given
        CloseMessageBodyDto closeMessageBodyDto = new CloseMessageBodyDto();
        closeMessageBodyDto.setName("name");
        closeMessageBodyDto.setSendNum(10);
        closeMessageBodyDto.setRecieveNum(10);
        byte[] result = objectMapper.writeValueAsBytes(closeMessageBodyDto);

        //when
        byte[] expected = serverService.implementCloseBody("name", 10, 10);

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementCloseHeader() {
        //given
        byte[] sendJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] result = {0, 0, 0, 8, 0, 0, 17, 92};

        //when
        byte[] expected = serverService.implementCloseHeader(sendJsonBytes);

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void broadcastAllUser() {

    }
}