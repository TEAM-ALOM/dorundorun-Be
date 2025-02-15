package com.alom.dorundorunbe.domain.ranking.repository;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRankingRepository extends JpaRepository<UserRanking, Long> {




    Optional<UserRanking> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)//벌크성 연산 em.clear 역할 수행
    @Query("DELETE FROM UserRanking ur WHERE ur.ranking.id = :rankingId")
    void deleteByRankingId(@Param("rankingId") Long rankingId);



    List<UserRanking> findByRankingId(Long rankingId);

    @EntityGraph(attributePaths = {"user"})
    List<UserRanking> findWithUserByRankingId(Long rankingId);
}
