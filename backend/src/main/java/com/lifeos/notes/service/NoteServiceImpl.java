package com.lifeos.notes.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.notes.dto.CreateNoteRequest;
import com.lifeos.notes.dto.NoteResponse;
import com.lifeos.notes.dto.UpdateNoteRequest;
import com.lifeos.notes.entity.Note;
import com.lifeos.notes.repository.NoteRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponse> getNotes(Long userId, String search, String category) {
        String trimmedSearch = StringUtils.hasText(search) ? search.trim() : null;
        String trimmedCategory = StringUtils.hasText(category) ? category.trim() : null;

        List<Note> notes;

        if (trimmedSearch != null && trimmedCategory != null) {
            notes = noteRepository.searchNotesByUserIdAndCategory(userId, trimmedSearch, trimmedCategory);
        } else if (trimmedSearch != null) {
            notes = noteRepository.searchNotesByUserId(userId, trimmedSearch);
        } else if (trimmedCategory != null) {
            notes = noteRepository.findByUserIdAndCategoryOrderByUpdatedAtDesc(userId, trimmedCategory);
        } else {
            notes = noteRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        }

        return notes.stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponse getNote(Long userId, Long id) {
        Note note = noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        return NoteResponse.fromEntity(note);
    }

    @Override
    public NoteResponse createNote(Long userId, CreateNoteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String trimmedTitle = request.getTitle() != null ? request.getTitle().trim() : "";
        String trimmedContent = request.getContent() != null ? request.getContent().trim() : null;
        String trimmedCategory = StringUtils.hasText(request.getCategory()) ? request.getCategory().trim() : null;

        Note note = new Note(user, trimmedTitle, trimmedContent, trimmedCategory);
        Note saved = noteRepository.save(note);
        return NoteResponse.fromEntity(saved);
    }

    @Override
    public NoteResponse updateNote(Long userId, Long id, UpdateNoteRequest request) {
        Note note = noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent() != null ? request.getContent().trim() : null);
        note.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory().trim() : null);

        Note saved = noteRepository.save(note);
        return NoteResponse.fromEntity(saved);
    }

    @Override
    public void deleteNote(Long userId, Long id) {
        Note note = noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        noteRepository.delete(note);
    }
}
