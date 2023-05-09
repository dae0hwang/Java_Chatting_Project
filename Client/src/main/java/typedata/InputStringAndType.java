package typedata;

import lombok.Data;

@Data
public class InputStringAndType {
    public String inputString;
    public Type type;
    public InputStringAndType(String inputString, Type type) {
        this.inputString = inputString;
        this.type = type;
    }
}