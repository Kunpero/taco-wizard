package rs.kunpero.fatpak.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedRequestDto {
    private final String fromUser;

    private final String toUser;

    private final int amount;

    private final String commentary;
}
