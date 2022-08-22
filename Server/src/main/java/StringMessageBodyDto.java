import lombok.Data;

@Data
public class StringMessageBodyDto {
    String name;
    private byte[] stringMessageBytes;
}
