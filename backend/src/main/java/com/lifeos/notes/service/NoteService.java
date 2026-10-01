package com.lifeos.notes.service;

import com.lifeos.notes.dto.CreateNoteRequest;
import com.lifeos.notes.dto.NoteResponse;
import com.lifeos.notes.dto.UpdateNoteRequest;

import java.util.List;

public interface NoteService {

    List<NoteResponse> getNotes(Long userId, String search, String category);

    NoteResponse getNote(Long userId, Long id);

    NoteResponse createNote(Long userId, CreateNoteRequest request);

    NoteResponse updateNote(Long userId, Long id, UpdateNoteRequest request);

    void deleteNote(Long userId, Long id);
}
