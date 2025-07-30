package net.salim.api_motus.repository;


import net.salim.api_motus.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByUserId(Long userId);
    List<Score> findByWordId(Long wordId);
    List<Score> findByUserIdOrderByScoreDesc(Long userId);
}