package net.salim.api_motus.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordDTO {
    private Long id;
    private Integer length;
    private String difficulty;
    private String word;
}