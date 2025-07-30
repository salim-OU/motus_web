package net.salim.api_motus.service;


import lombok.RequiredArgsConstructor;
import net.salim.api_motus.model.Score;
import net.salim.api_motus.model.User;
import net.salim.api_motus.model.Word;
import net.salim.api_motus.repository.ScoreRepository;
import net.salim.api_motus.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final ScoreRepository scoreRepository;
    private final WordRepository wordRepository;

    // Obtenir un mot aléatoire pour jouer
    public Optional<Word> getRandomWord() {
        return wordRepository.findRandomWord();
    }

    // Obtenir un mot par longueur
    public List<Word> getWordsByLength(Integer length) {
        return wordRepository.findByLength(length);
    }

    // Sauvegarder le score d'une partie
    public Score saveGameScore(User user, Word word, Integer scoreValue) {
        Score score = new Score();
        score.setUser(user);
        score.setWord(word);
        score.setScore(scoreValue);
        score.setDateTime(new Date());
        return scoreRepository.save(score);
    }

    // Obtenir les scores d'un utilisateur
    public List<Score> getUserScores(Long userId) {
        return scoreRepository.findByUserIdOrderByScoreDesc(userId);
    }

    // Obtenir les scores pour un mot
    public List<Score> getWordScores(Long wordId) {
        return scoreRepository.findByWordId(wordId);
    }
}
