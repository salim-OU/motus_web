package net.salim.api_motus.JwtUtil;


import net.salim.api_motus.model.Word;
import net.salim.api_motus.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class WordImporterApi implements CommandLineRunner {

    private final WordRepository wordRepository;

    // APIs françaises gratuites
    private static final String[] FRENCH_WORD_APIS = {
            "https://trouve-mot.fr/api/random/50",
            "https://www.dicolink.com/api/mots/aleatoires",
            "https://api-mots.herokuapp.com/mots/aleatoires"
    };

    // URL pour télécharger une liste de mots français (OpenData)
    private static final String FRENCH_WORDS_URL = "https://www.lexique.org/databases/lexique383/lexique383.tsv";

    // Mots de secours si toutes les APIs échouent
    private static final String[] FALLBACK_WORDS = {
            "CHAT", "PAIN", "MAIN", "LUNE", "ROSE", "BLEU", "VERT", "NOIR", "JOUR", "NUIT",
            "FLEUR", "CHIEN", "MAISON", "COEUR", "TEMPS", "MONDE", "AMOUR", "RIRE", "JOIE", "PAIX",
            "JARDIN", "SOLEIL", "NATURE", "VOYAGE", "MUSIQUE", "FAMILLE", "BONHEUR", "COURAGE",
            "VOITURE", "FENETRE", "CUISINE", "SOURIRE", "ENFANT", "PARENT", "COPAIN", "VACANCES",
            "ELEPHANT", "ORDINATEUR", "TELEPHONE", "MERVEILLEUX", "EXTRAORDINAIRE", "MAGNIFIQUE"
    };

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Démarrage de l'importation des mots français depuis les APIs...");

        // Vérifier si des mots existent déjà
        if (wordRepository.count() > 0) {
            log.info("✅ Des mots existent déjà dans la base de données ({} mots)", wordRepository.count());
            return;
        }

        List<String> importedWords = new ArrayList<>();

        // Méthode 1: API Lexique (Université de Savoie)
        try {
            log.info("📡 Tentative d'import depuis l'API Lexique...");
            importedWords.addAll(importFromLexiqueApi());
        } catch (Exception e) {
            log.warn("⚠️ Échec API Lexique: {}", e.getMessage());
        }

        // Méthode 2: APIs de mots français
        if (importedWords.size() < 50) {
            try {
                log.info("📡 Tentative d'import depuis les APIs de mots...");
                importedWords.addAll(importFromWordApis());
            } catch (Exception e) {
                log.warn("⚠️ Échec APIs de mots: {}", e.getMessage());
            }
        }

        // Méthode 3: Mots de secours
        if (importedWords.size() < 20) {
            log.info("📝 Utilisation des mots de secours...");
            importedWords.addAll(importFallbackWords());
        }

        // Sauvegarder les mots
        saveWordsToDatabase(importedWords);

        log.info("✅ Importation terminée! {} mots ajoutés à la base de données", wordRepository.count());
    }

    // Importer depuis l'API Lexique (base de données officielle française)
    private List<String> importFromLexiqueApi() {
        List<String> words = new ArrayList<>();

        try {
            log.info("🔍 Connexion à l'API Lexique...");

            URL url = new URL(FRENCH_WORDS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 secondes
            connection.setReadTimeout(30000); // 30 secondes

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                int count = 0;
                Random random = new Random();

                // Ignorer la première ligne (en-têtes)
                reader.readLine();

                while ((line = reader.readLine()) != null && count < 200) {
                    String[] columns = line.split("\\t");
                    if (columns.length > 0) {
                        String word = columns[0].toUpperCase().trim();

                        // Sélectionner aléatoirement 10% des mots
                        if (isValidFrenchWord(word) && random.nextDouble() < 0.1) {
                            words.add(word);
                            count++;
                        }
                    }
                }
                reader.close();

                log.info("📊 {} mots importés depuis l'API Lexique", words.size());
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'import depuis Lexique: {}", e.getMessage());
            throw new RuntimeException("Échec API Lexique", e);
        }

        return words;
    }

    // Importer depuis les APIs de mots français
    private List<String> importFromWordApis() {
        List<String> words = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String apiUrl : FRENCH_WORD_APIS) {
            try {
                log.info("🔍 Tentative API: {}", apiUrl);

                ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    // Parser la réponse JSON
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());

                    // Traitement selon la structure de l'API
                    if (jsonNode.isArray()) {
                        // Format: ["mot1", "mot2", ...]
                        jsonNode.forEach(wordNode -> {
                            String word = wordNode.asText().toUpperCase().trim();
                            if (isValidFrenchWord(word)) {
                                words.add(word);
                            }
                        });
                    } else if (jsonNode.has("mots")) {
                        // Format: {"mots": ["mot1", "mot2", ...]}
                        jsonNode.get("mots").forEach(wordNode -> {
                            String word = wordNode.asText().toUpperCase().trim();
                            if (isValidFrenchWord(word)) {
                                words.add(word);
                            }
                        });
                    } else if (jsonNode.has("data")) {
                        // Format: {"data": [{"mot": "word"}, ...]}
                        jsonNode.get("data").forEach(wordNode -> {
                            if (wordNode.has("mot")) {
                                String word = wordNode.get("mot").asText().toUpperCase().trim();
                                if (isValidFrenchWord(word)) {
                                    words.add(word);
                                }
                            }
                        });
                    }

                    log.info("✅ {} mots importés depuis {}", words.size(), apiUrl);
                    break; // Sortir si succès
                }

            } catch (HttpClientErrorException e) {
                log.warn("⚠️ Erreur HTTP pour {}: {}", apiUrl, e.getStatusCode());
            } catch (Exception e) {
                log.warn("⚠️ Erreur pour {}: {}", apiUrl, e.getMessage());
            }
        }

        return words;
    }

    // Importer les mots de secours
    private List<String> importFallbackWords() {
        List<String> words = new ArrayList<>();

        for (String word : FALLBACK_WORDS) {
            if (isValidFrenchWord(word)) {
                words.add(word.toUpperCase().trim());
            }
        }

        log.info("📝 {} mots de secours préparés", words.size());
        return words;
    }

    // Sauvegarder les mots dans la base de données
    private void saveWordsToDatabase(List<String> words) {
        int savedCount = 0;

        // Supprimer les doublons
        List<String> uniqueWords = words.stream()
                .distinct()
                .toList();

        for (String wordText : uniqueWords) {
            try {
                // Vérifier si le mot existe déjà
                if (wordRepository.findByWord(wordText).isEmpty()) {
                    Word word = new Word();
                    word.setWord(wordText);
                    word.setLength(wordText.length());
                    word.setDifficulty(determineDifficulty(wordText));

                    wordRepository.save(word);
                    savedCount++;
                }
            } catch (Exception e) {
                log.warn("⚠️ Erreur lors de la sauvegarde du mot '{}': {}", wordText, e.getMessage());
            }
        }

        log.info("💾 {} nouveaux mots uniques sauvegardés sur {} collectés", savedCount, uniqueWords.size());
    }

    // Déterminer la difficulté d'un mot
    private String determineDifficulty(String word) {
        int length = word.length();

        if (length <= 5) {
            return "EASY";
        } else if (length <= 8) {
            return "MEDIUM";
        } else {
            return "HARD";
        }
    }

    // Valider si un mot français est acceptable pour le jeu Motus
    private boolean isValidFrenchWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        word = word.trim().toUpperCase();

        // Critères de validation pour Motus
        return word.length() >= 4 &&  // Minimum 4 lettres pour Motus
                word.length() <= 12 && // Maximum 12 lettres
                word.matches("[A-ZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸŒ]+") && // Lettres françaises
                !containsInvalidPatterns(word) &&
                !isProperNoun(word); // Pas de noms propres
    }

    // Vérifier les motifs invalides
    private boolean containsInvalidPatterns(String word) {
        // Éviter les mots avec trop de lettres répétées consécutives
        if (word.matches(".*([A-Z])\\1{2,}.*")) {
            return true;
        }

        // Éviter les mots techniques ou abréviations
        String[] invalidPatterns = {"WWW", "HTTP", "HTML", "CSS", "API", "URL", "SMS", "GPS"};
        for (String pattern : invalidPatterns) {
            if (word.contains(pattern)) {
                return true;
            }
        }

        // Éviter les mots trop courts pour être intéressants
        if (word.length() < 4) {
            return true;
        }

        return false;
    }

    // Vérifier si c'est un nom propre (heuristique simple)
    private boolean isProperNoun(String word) {
        // Liste de préfixes/suffixes de noms propres français
        String[] properNounPrefixes = {"SAINT", "SAINTE", "MONT", "VILLE", "PARIS", "LYON", "JEAN", "MARIE"};

        for (String prefix : properNounPrefixes) {
            if (word.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}