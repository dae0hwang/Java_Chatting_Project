package dto;

import lombok.Data;

@Data
public class ImageMessageBodyDto {
    String name;
    private byte[] imageMessageBytes;
}
