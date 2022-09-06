import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {
    Socket socket;
    BufferedReader bufferedReader;
    ClientService clientService;
    DataOutputStream dataOutputStream;
    ObjectMapper objectMapper;

    @BeforeEach
    void init() throws IOException {
        socket = mock(Socket.class);
        bufferedReader = mock(BufferedReader.class);
        clientService = new ClientService(socket);
        dataOutputStream = mock(DataOutputStream.class);
        objectMapper = new ObjectMapper();
    }

    @Test
    void implementResisterNameJsonBytes() throws IOException {
        //given
        when(bufferedReader.readLine()).thenReturn("name");
        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
        resisterNameMessageBodyDto.setName("name");
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);

        //when
        byte[] expected = clientService.implementResisterNameJsonBytes(bufferedReader);

        //then
        assertArrayEquals(expected, sendJsonBytes);
    }

    @Test
    void implementResisterNameHeader() {
        //given
        byte[] sendJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] resultHeader = {0, 0, 0, 8, 0, 0, 4, 87};

        //when
        byte[] expectedBytes = clientService.implementResisterNameHeader(sendJsonBytes);

        //then
        assertArrayEquals(expectedBytes, resultHeader);
    }

    @Test
    void sendResisterName() throws IOException {
        //given
        byte[] sendJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] headerBytes = {0, 1, 2, 3, 4, 5, 6, 7};

        //when
        clientService.sendResisterName(dataOutputStream, headerBytes, sendJsonBytes);

        //then
        verify(dataOutputStream, times(1)).write(headerBytes, 0, 8);
        verify(dataOutputStream, times(1)).write(sendJsonBytes, 0, 10);
        verify(dataOutputStream, times(1)).flush();
    }

    @Test
    void storeInputStringAndSetType() throws IOException {
        //given
        when(bufferedReader.readLine()).thenReturn("image://C:\\Users\\geung\\Desktop\\sea.jpg\n")
            .thenReturn("string message");
        InputStringAndType result1 = new InputStringAndType
            ("image://C:\\Users\\geung\\Desktop\\sea.jpg\n", Type.IMAGETOSERVER);
        InputStringAndType result2 = new InputStringAndType
            ("string message", Type.MESSAGETOSERVER);

        //then1
        InputStringAndType expected1 = clientService.storeInputStringAndSetType(bufferedReader);

        //when1
        assertEquals(expected1, result1);

        //then2
        InputStringAndType expected2 = clientService.storeInputStringAndSetType(bufferedReader);

        //when2
        assertEquals(expected2, result2);

    }

    @Test
    void sendStringMessage() throws IOException {
        //given
        InputStringAndType inputStringAndType = new InputStringAndType("message", Type.MESSAGETOSERVER);
        byte[] inputStringtobytes = inputStringAndType.inputString.getBytes("UTF-8");
        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
        stringMessageBodyDto.setStringMessageBytes(inputStringtobytes);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = headerConverter.bytesHeader;

        //when
        clientService.sendStringMessage(dataOutputStream, inputStringAndType);

        //then
        verify(dataOutputStream, times(1)).write(header, 0, header.length);
        verify(dataOutputStream, times(1)).write(sendJsonBytes, 0, sendJsonBytes.length);
        verify(dataOutputStream, times(1)).flush();
    }

    @Disabled
    @Test
    void sendImageMessage() throws IOException {
        //given
        URL resource = getClass().getClassLoader().getResource("sea.jpg");
        String filePath = resource.getFile();

        InputStringAndType inputStringAndType
            = new InputStringAndType("image://" + filePath, Type.IMAGETOSERVER);
        BufferedImage image = ImageIO.read(getClass().getResourceAsStream("sea.jpg"));
        System.out.println(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageInByte = byteArrayOutputStream.toByteArray();

        ImageMessageBodyDto imageMessageBodyDto = new ImageMessageBodyDto();
        imageMessageBodyDto.setImageMessageBytes(imageInByte);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = headerConverter.bytesHeader;

        //when
        clientService.sendImageMessage(dataOutputStream, inputStringAndType);

        //then
        verify(dataOutputStream, times(1)).write(header, 0, header.length);
        verify(dataOutputStream, times(1)).write(sendJsonBytes, 0, sendJsonBytes.length);
        verify(dataOutputStream, times(1)).flush();

    }
}