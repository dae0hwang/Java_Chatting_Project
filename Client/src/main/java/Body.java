import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Body {
    private String name;
    private int sendNum;
    private int recieveNum;
    private byte[] bytes;
}
