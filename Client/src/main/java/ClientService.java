import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientService {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Socket socket;
    protected DataOutputStream dataOutputStream;

    ClientService(Socket socket) throws IOException {
        this.socket = socket;
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

//    public void sendResisterName(DataOutputStream dataOutputStream) throws IOException {
////        OutputStream toServer = socket.getOutputStream();
////        DataOutputStream dos = new DataOutputStream(toServer);
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String name = br.readLine();
//        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
//        resisterNameMessageBodyDto.setName(name);
//        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);
//        int type = Type.RESISTERNAME.getValue();
//        HeaderConverter headerConverter = new HeaderConverter();
//        headerConverter.encodeHeader(sendJsonBytes.length,type);
//        byte[] clientHeader = headerConverter.bytesHeader;
//        dataOutputStream.write(clientHeader,0,clientHeader.length);
//        dataOutputStream.write(sendJsonBytes, 0, sendJsonBytes.length);
//        dataOutputStream.flush();
//    }

    public byte[] implementResisterNameJsonBytes() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String name = br.readLine();
        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
        resisterNameMessageBodyDto.setName(name);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);
        return sendJsonBytes;
    }

    public byte[] implementResisterNameHeader(byte[] sendJsonBytes) {
        int type = Type.RESISTERNAME.getValue();
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length,type);
        byte[] clientHeader = headerConverter.bytesHeader;
        return clientHeader;
    }

    public void sendResisterName
        (DataOutputStream dataOutputStream, byte[] resisterNameHeader, byte[] resisterNameJsonBytes)
        throws IOException {
        dataOutputStream.write(resisterNameHeader,0,resisterNameHeader.length);
        dataOutputStream.write(resisterNameJsonBytes, 0, resisterNameJsonBytes.length);
        dataOutputStream.flush();
    }

    public InputStringAndType storeInputStringAndSetType() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputString = br.readLine();
        Type type;
        if (inputString.length() >= 8 && inputString.substring(0, 8).equals("image://")) {
            type = Type.IMAGETOSERVER;
        } else {
            type = Type.MESSAGETOSERVER;
        }
        InputStringAndType inputStringAndType = new InputStringAndType(inputString, type);
        return inputStringAndType;
    }

    public void sendStringMessage(DataOutputStream dataOutputStream, InputStringAndType inputStringAndType)
        throws IOException {
        byte[] inputStringtobytes = inputStringAndType.inputString.getBytes("UTF-8");
        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
        stringMessageBodyDto.setStringMessageBytes(inputStringtobytes);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = headerConverter.bytesHeader;
//        OutputStream toServer = socket.getOutputStream();
//        DataOutputStream dos = new DataOutputStream(toServer);
        dataOutputStream.write(header, 0, header.length);
        dataOutputStream.write(sendJsonBytes, 0, sendJsonBytes.length);
        dataOutputStream.flush();
    }

    public void sendImageMessage(DataOutputStream dataOutputStream, InputStringAndType inputStringAndType)
        throws IOException {
        int filePathStartIdx = 8;
        String inputString = inputStringAndType.inputString;
        String filePath = inputString.substring(filePathStartIdx, inputString.length());
        File file = new File(filePath);
        byte[] imageFileBytes = Files.readAllBytes(file.toPath());
        ImageMessageBodyDto imageMessageBodyDto = new ImageMessageBodyDto();
        imageMessageBodyDto.setImageMessageBytes(imageFileBytes);
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length, inputStringAndType.type.getValue());
        byte[] header = headerConverter.bytesHeader;
//        OutputStream toServer = socket.getOutputStream();
//        DataOutputStream dos = new DataOutputStream(toServer);
        dataOutputStream.write(header, 0, header.length);
        dataOutputStream.write(sendJsonBytes, 0, sendJsonBytes.length);
        dataOutputStream.flush();
    }
}
