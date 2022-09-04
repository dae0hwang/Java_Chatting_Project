public class HeaderInformation {
    int messageBodyLength;
    Type messageBodyType;

    HeaderInformation(int length, Type type) {
        this.messageBodyLength = length;
        this.messageBodyType = type;
    }
}

