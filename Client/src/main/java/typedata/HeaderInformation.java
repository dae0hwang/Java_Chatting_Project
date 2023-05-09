package typedata;

import lombok.Data;

@Data
public class HeaderInformation {
    public int messageBodyLength;
    public Type messageBodyType;

    public HeaderInformation(int length, Type type) {
        this.messageBodyLength = length;
        this.messageBodyType = type;
    }
}

