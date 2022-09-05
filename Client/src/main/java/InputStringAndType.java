import lombok.Data;

@Data
public class InputStringAndType {
    String inputString;
    Type type;
    InputStringAndType(String inputString, Type type) {
        this.inputString = inputString;
        this.type = type;
    }
}