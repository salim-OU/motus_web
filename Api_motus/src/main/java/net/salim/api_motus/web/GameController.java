package net.salim.api_motus.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.salim.api_motus.dto.ScoreDTO;  // Import de votre DTO existant
import net.salim.api_motus.model.Score;
import net.salim.api_motus.model.User;
import net.salim.api_motus.model.Word;
import net.salim.api_motus.repository.WordRepository;
import net.salim.api_motus.service.GameService;
import net.salim.api_motus.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private final WordRepository wordRepository;

    // ===== DTO pour Word (pour éviter les références circulaires) =====
    public static class WordDTO {
        private Long id;
        private String word;
        private Integer length;
        private String difficulty;

        // Constructeurs
        public WordDTO() {}

        public WordDTO(Word word) {
            this.id = word.getId();
            this.word = word.getWord();
            this.length = word.getLength();
            this.difficulty = word.getDifficulty();
        }

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getWord() { return word; }
        public void setWord(String word) { this.word = word; }

        public Integer getLength() { return length; }
        public void setLength(Integer length) { this.length = length; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    }

    // ===== ENDPOINTS CORRIGÉS =====

    // Obtenir un mot aléatoire pour jouer - CORRIGÉ
    @GetMapping("/word/random")
    public ResponseEntity<WordDTO> getRandomWord() {
        log.info("🎲 Récupération d'un mot aléatoire");

        return gameService.getRandomWord()
                .map(word -> {
                    WordDTO wordDTO = new WordDTO(word);
                    log.info("✅ Mot trouvé: {} (ID: {}, Difficulté: {})",
                            wordDTO.getWord(), wordDTO.getId(), wordDTO.getDifficulty());
                    return ResponseEntity.ok(wordDTO);
                })
                .orElseGet(() -> {
                    log.warn("❌ Aucun mot trouvé");
                    return ResponseEntity.notFound().build();
                });
    }

    // Obtenir des mots par longueur - CORRIGÉ
    @GetMapping("/words/length/{length}")
    public ResponseEntity<List<WordDTO>> getWordsByLength(@PathVariable Integer length) {
        log.info("🔍 Récupération des mots de longueur: {}", length);

        List<Word> words = gameService.getWordsByLength(length);
        List<WordDTO> wordDTOs = words.stream()
                .map(WordDTO::new)
                .collect(Collectors.toList());

        log.info("✅ {} mots trouvés de longueur {}", wordDTOs.size(), length);
        return ResponseEntity.ok(wordDTOs);
    }

    // Obtenir les scores d'un utilisateur - CORRIGÉ avec votre ScoreDTO
    @GetMapping("/scores/user/{userId}")
    public ResponseEntity<List<ScoreDTO>> getUserScores(@PathVariable Long userId) {
        log.info("📊 Récupération des scores pour l'utilisateur: {}", userId);

        try {
            List<Score> scores = gameService.getUserScores(userId);

            List<ScoreDTO> scoreDTOs = scores.stream()
                    .map(this::convertToScoreDTO)
                    .collect(Collectors.toList());

            log.info("✅ {} scores trouvés pour l'utilisateur {}", scoreDTOs.size(), userId);
            return ResponseEntity.ok(scoreDTOs);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des scores pour l'utilisateur {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtenir les scores d'un mot - CORRIGÉ avec votre ScoreDTO
    @GetMapping("/scores/word/{wordId}")
    public ResponseEntity<List<ScoreDTO>> getWordScores(@PathVariable Long wordId) {
        log.info("📊 Récupération des scores pour le mot: {}", wordId);

        try {
            List<Score> scores = gameService.getWordScores(wordId);

            List<ScoreDTO> scoreDTOs = scores.stream()
                    .map(this::convertToScoreDTO)
                    .collect(Collectors.toList());

            log.info("✅ {} scores trouvés pour le mot {}", scoreDTOs.size(), wordId);
            return ResponseEntity.ok(scoreDTOs);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des scores pour le mot {}: {}", wordId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Sauvegarder le score avec paramètres URL - CORRIGÉ
    @PostMapping("/score")
    public ResponseEntity<ScoreDTO> saveScore(@RequestParam Long userId,
                                              @RequestParam Long wordId,
                                              @RequestParam Integer score) {
        log.info("💾 Sauvegarde du score: userId={}, wordId={}, score={}", userId, wordId, score);

        try {
            // Récupérer l'utilisateur
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Récupérer le mot complet depuis la base de données
            Word word = wordRepository.findById(wordId)
                    .orElseThrow(() -> new RuntimeException("Word not found with id: " + wordId));

            Score savedScore = gameService.saveGameScore(user, word, score);
            ScoreDTO scoreDTO = convertToScoreDTO(savedScore);

            log.info("✅ Score sauvegardé avec l'ID: {}", savedScore.getId());
            return ResponseEntity.ok(scoreDTO);

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erreur interne: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Sauvegarder le score avec JSON Body - CORRIGÉ
    @PostMapping("/score/json")
    public ResponseEntity<ScoreDTO> saveScoreJson(@RequestBody ScoreRequest request) {
        log.info("💾 Sauvegarde du score (JSON): {}", request);

        try {
            User user = userService.getUserById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

            Word word = wordRepository.findById(request.getWordId())
                    .orElseThrow(() -> new RuntimeException("Word not found with id: " + request.getWordId()));

            Score savedScore = gameService.saveGameScore(user, word, request.getScore());
            ScoreDTO scoreDTO = convertToScoreDTO(savedScore);

            log.info("✅ Score sauvegardé avec l'ID: {}", savedScore.getId());
            return ResponseEntity.ok(scoreDTO);

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("❌ Erreur interne: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== MÉTHODES UTILITAIRES =====

    // Convertir Score entity vers votre ScoreDTO complet
    private ScoreDTO convertToScoreDTO(Score score) {
        // Conversion Date vers LocalDateTime si nécessaire
        LocalDateTime dateTime = null;
        if (score.getDateTime() != null) {
            // Conversion Date -> LocalDateTime
            dateTime = ((Date) score.getDateTime()).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return ScoreDTO.builder()
                .id(score.getId())
                .score(score.getScore())
                .dateTime(dateTime)

                // Informations utilisateur
                .username(score.getUser() != null ? score.getUser().getUsername() : null)
                .userId(score.getUser() != null ? score.getUser().getId() : null)

                // Informations du mot
                .wordId(score.getWord() != null ? score.getWord().getId() : null)
                .word(score.getWord() != null ? score.getWord().getWord() : null)
                .difficulty(score.getWord() != null ? score.getWord().getDifficulty() : null)
                .wordLength(score.getWord() != null ? score.getWord().getLength() : null)

                // Champs calculés
                .isWon(score.getScore() != null && score.getScore() > 0)
                .attempts(null) // À implémenter si vous trackez les tentatives

                .build();
    }

    // ===== CLASSES INTERNES =====

    // Classe pour les requêtes JSON - conservée telle quelle
    public static class ScoreRequest {
        private Long userId;
        private Long wordId;
        private Integer score;

        // Getters et Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getWordId() { return wordId; }
        public void setWordId(Long wordId) { this.wordId = wordId; }

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }

        @Override
        public String toString() {
            return String.format("ScoreRequest{userId=%d, wordId=%d, score=%d}", userId, wordId, score);
        }
    }
}