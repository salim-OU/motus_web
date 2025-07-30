package net.salim.api_motus.repository;


import net.salim.api_motus.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByLength(Integer length);
    List<Word> findByDifficulty(String difficulty);
    Optional<Word> findByWord(String word);

    @Query(value = "SELECT * FROM words ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Word> findRandomWord();
}