package rs.kunpero.fatpak.integration.dto;

import lombok.Data;

import java.util.List;

@Data
public class GiveTacoResponse {
    private String success;

    private Boolean ok;

    private List<String> error;
}
