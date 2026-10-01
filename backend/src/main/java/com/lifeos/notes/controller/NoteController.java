package com.lifeos.notes.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.notes.dto.CreateNoteRequest;
import com.lifeos.notes.dto.NoteResponse;
import com.lifeos.notes.dto.UpdateNoteRequest;
import com.lifeos.notes.service.NoteService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * List user notes with optional server-side search and category filtering:
     * GET /api/v1/notes
     * GET /api/v1/notes?search=java
     * GET /api/v1/notes?category=study
     * GET /api/v1/notes?search=java&category=study
     */
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<NoteResponse> notes = noteService.getNotes(principal.getId(), search, category);
        return ResponseEntity.ok(notes);
    }

    /**
     * Get single note by ID:
     * GET /api/v1/notes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NoteResponse response = noteService.getNote(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new note:
     * POST /api/v1/notes
     */
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestBody CreateNoteRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NoteResponse response = noteService.createNote(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing note:
     * PUT /api/v1/notes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NoteResponse response = noteService.updateNote(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a note:
     * DELETE /api/v1/notes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteNote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        noteService.deleteNote(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Note deleted successfully"));
    }
}
