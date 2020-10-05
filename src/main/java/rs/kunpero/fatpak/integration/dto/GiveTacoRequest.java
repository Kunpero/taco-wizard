package rs.kunpero.fatpak.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiveTacoRequest {
    private String token;

    private String uid;

    private int amount;

    private String message;
}
