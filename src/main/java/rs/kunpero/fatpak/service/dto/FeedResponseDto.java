package rs.kunpero.fatpak.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FeedResponseDto {
    private boolean isSuccess;

    private String errorMessage;
}
