import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

public class practice {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String a = "name";
        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
        resisterNameMessageBodyDto.setName("name");
        byte[] sendJsonBytes = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);

        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.encodeHeader(sendJsonBytes.length, Type.RESISTERNAME.getValue());
        byte[] clientHeader = headerConverter.bytesHeader;

        System.out.println(Arrays.toString(sendJsonBytes));
        System.out.println(Arrays.toString(clientHeader));

    }
}
