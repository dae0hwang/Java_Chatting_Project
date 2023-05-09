package dto;

import lombok.Data;

@Data
public class CloseMessageBodyDto {
    private String name;
    private int sendNum;
    private int recieveNum;
}
