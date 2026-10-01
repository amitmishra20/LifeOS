package com.lifeos.notes.repository;

import com.lifeos.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    List<Note> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Note> findByUserIdAndCategoryOrderByUpdatedAtDesc(Long userId, String category);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(COALESCE(n.content, '')) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.updatedAt DESC")
    List<Note> searchNotesByUserId(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.category = :category AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(COALESCE(n.content, '')) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.updatedAt DESC")
    List<Note> searchNotesByUserIdAndCategory(@Param("userId") Long userId, @Param("query") String query, @Param("category") String category);
}
