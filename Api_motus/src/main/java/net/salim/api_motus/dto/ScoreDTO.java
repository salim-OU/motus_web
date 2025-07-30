package net.salim.api_motus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDTO {
    private Long id;
    private Integer score;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    private String username;
    private Long userId;
    private Long wordId;
    private String word;
    private String difficulty;
    private Integer wordLength;
    private Boolean isWon;
    private Integer attempts;
}
