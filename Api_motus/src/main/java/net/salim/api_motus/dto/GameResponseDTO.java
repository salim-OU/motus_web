package net.salim.api_motus.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponseDTO {
    private boolean success;
    private String message;
    private Integer score;
    private WordDTO word;
    private String result; // "WIN", "LOSE", "CONTINUE"
}
