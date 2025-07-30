package net.salim.api_motus.config;


import lombok.RequiredArgsConstructor;
import net.salim.api_motus.model.Word;
import net.salim.api_motus.repository.WordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final WordRepository wordRepository;

    @Override
    public void run(String... args) throws Exception {
        // Ajouter quelques mots par défaut si la base est vide
        if (wordRepository.count() == 0) {
            createDefaultWords();
        }
    }

    private void createDefaultWords() {
        // Mots faciles (4-5 lettres)
        createWord("CHAT", 4, "EASY");
        createWord("PAIN", 4, "EASY");
        createWord("FLEUR", 5, "EASY");
        createWord("MAISON", 6, "EASY");

        // Mots moyens (6-7 lettres)
        createWord("JARDIN", 6, "MEDIUM");
        createWord("VOITURE", 7, "MEDIUM");
        createWord("COURAGE", 7, "MEDIUM");

        // Mots difficiles (8+ lettres)
        createWord("ELEPHANT", 8, "HARD");
        createWord("MERVEILLEUX", 11, "HARD");
        createWord("EXTRAORDINAIRE", 13, "HARD");
    }

    private void createWord(String word, Integer length, String difficulty) {
        Word w = new Word();
        w.setWord(word);
        w.setLength(length);
        w.setDifficulty(difficulty);
        wordRepository.save(w);
    }
}