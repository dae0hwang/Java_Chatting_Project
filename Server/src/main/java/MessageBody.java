import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageBody {
    private String name;
    private int sendNum;
    private int recieveNum;
    private byte[] bytes;
}
