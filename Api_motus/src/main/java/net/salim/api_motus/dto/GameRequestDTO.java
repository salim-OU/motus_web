package net.salim.api_motus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Word ID is required")
    private Long wordId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be positive")
    private Integer score;

    private String guessedWord;
}