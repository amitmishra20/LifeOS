package com.lifeos.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.notes.dto.CreateNoteRequest;
import com.lifeos.notes.dto.UpdateNoteRequest;
import com.lifeos.notes.entity.Note;
import com.lifeos.notes.repository.NoteRepository;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ObjectMapper objectMapper;

    private User primaryUser;
    private User secondaryUser;
    private Cookie authCookie;
    private Cookie otherAuthCookie;

    @BeforeEach
    void setUp() {
        cleanup();

        primaryUser = userRepository.save(new User("Primary User", "notes_user1@example.com", "passwordHash"));
        secondaryUser = userRepository.save(new User("Secondary User", "notes_user2@example.com", "passwordHash"));

        String token = tokenProvider.generateToken(primaryUser.getId(), primaryUser.getEmail());
        authCookie = new Cookie(cookieService.getCookieName(), token);

        String otherToken = tokenProvider.generateToken(secondaryUser.getId(), secondaryUser.getEmail());
        otherAuthCookie = new Cookie(cookieService.getCookieName(), otherToken);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/notes without auth returns 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/notes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/notes creates new note successfully")
    void createNote_Success() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest(
                "Distributed Systems Architecture",
                "Key takeaways: CAP theorem, Paxos consensus, Raft protocol, and Eventual Consistency models.",
                "Engineering"
        );

        mockMvc.perform(post("/api/v1/notes")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Distributed Systems Architecture"))
                .andExpect(jsonPath("$.content").value("Key takeaways: CAP theorem, Paxos consensus, Raft protocol, and Eventual Consistency models."))
                .andExpect(jsonPath("$.category").value("Engineering"))
                .andExpect(jsonPath("$.userId").value(primaryUser.getId()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        List<Note> notes = noteRepository.findByUserIdOrderByUpdatedAtDesc(primaryUser.getId());
        assertThat(notes).hasSize(1);
        assertThat(notes.get(0).getTitle()).isEqualTo("Distributed Systems Architecture");
    }

    @Test
    @DisplayName("POST /api/v1/notes fails with 400 when title is blank")
    void createNote_BlankTitle_Returns400() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest(
                "   ",
                "Some body content",
                "General"
        );

        mockMvc.perform(post("/api/v1/notes")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/v1/notes fails with 400 when category exceeds 50 chars")
    void createNote_CategoryTooLong_Returns400() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest(
                "Title",
                "Content",
                "A".repeat(51)
        );

        mockMvc.perform(post("/api/v1/notes")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET /api/v1/notes returns only current user's notes")
    void getNotes_UserIsolation() throws Exception {
        noteRepository.save(new Note(primaryUser, "User 1 Note", "Content 1", "Work"));
        noteRepository.save(new Note(secondaryUser, "User 2 Secret Note", "Secret content", "Private"));

        mockMvc.perform(get("/api/v1/notes")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("User 1 Note"));
    }

    @Test
    @DisplayName("GET /api/v1/notes/{id} returns note for owner")
    void getNote_Success() throws Exception {
        Note note = noteRepository.save(new Note(primaryUser, "Architecture Notes", "Clean Architecture details", "Tech"));

        mockMvc.perform(get("/api/v1/notes/" + note.getId())
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(note.getId()))
                .andExpect(jsonPath("$.title").value("Architecture Notes"))
                .andExpect(jsonPath("$.content").value("Clean Architecture details"))
                .andExpect(jsonPath("$.category").value("Tech"));
    }

    @Test
    @DisplayName("GET /api/v1/notes/{id} returns 404 for cross-user note")
    void getNote_CrossUser_Returns404() throws Exception {
        Note otherNote = noteRepository.save(new Note(secondaryUser, "Secret Doc", "Confidential", "Confidential"));

        mockMvc.perform(get("/api/v1/notes/" + otherNote.getId())
                        .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/v1/notes/{id} updates note successfully")
    void updateNote_Success() throws Exception {
        Note note = noteRepository.save(new Note(primaryUser, "Draft Title", "Draft content", "Draft"));

        UpdateNoteRequest request = new UpdateNoteRequest(
                "Final Title",
                "Refined knowledge content",
                "Knowledge"
        );

        mockMvc.perform(put("/api/v1/notes/" + note.getId())
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Final Title"))
                .andExpect(jsonPath("$.content").value("Refined knowledge content"))
                .andExpect(jsonPath("$.category").value("Knowledge"));

        Note updated = noteRepository.findById(note.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Final Title");
        assertThat(updated.getContent()).isEqualTo("Refined knowledge content");
    }

    @Test
    @DisplayName("PUT /api/v1/notes/{id} returns 404 for cross-user note")
    void updateNote_CrossUser_Returns404() throws Exception {
        Note otherNote = noteRepository.save(new Note(secondaryUser, "Other Title", "Other content", "Personal"));

        UpdateNoteRequest request = new UpdateNoteRequest(
                "Hijacked Title",
                "Hijacked content",
                "Hacked"
        );

        mockMvc.perform(put("/api/v1/notes/" + otherNote.getId())
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/notes/{id} deletes note for owner")
    void deleteNote_Success() throws Exception {
        Note note = noteRepository.save(new Note(primaryUser, "Temporary Note", "To be deleted", "Temp"));

        mockMvc.perform(delete("/api/v1/notes/" + note.getId())
                        .with(csrf())
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Note deleted successfully"));

        assertThat(noteRepository.findById(note.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/notes/{id} returns 404 for cross-user note")
    void deleteNote_CrossUser_Returns404() throws Exception {
        Note otherNote = noteRepository.save(new Note(secondaryUser, "Secondary Note", "Will not be deleted", "Protected"));

        mockMvc.perform(delete("/api/v1/notes/" + otherNote.getId())
                        .with(csrf())
                        .cookie(authCookie))
                .andExpect(status().isNotFound());

        assertThat(noteRepository.findById(otherNote.getId())).isPresent();
    }

    @Test
    @DisplayName("GET /api/v1/notes?search=... searches title and content case-insensitively")
    void searchNotes_MatchesTitleAndContent() throws Exception {
        noteRepository.save(new Note(primaryUser, "React State Management", "Using Redux and Context API", "Frontend"));
        noteRepository.save(new Note(primaryUser, "Spring Boot Microservices", "Building distributed REST APIs with Java", "Backend"));
        noteRepository.save(new Note(primaryUser, "Database Indexing", "B-Tree vs Hash indexing tradeoffs in MySQL", "Database"));
        noteRepository.save(new Note(secondaryUser, "Spring Security Secrets", "Java security internals", "Backend"));

        // 1. Search by title keyword (case-insensitive)
        mockMvc.perform(get("/api/v1/notes")
                        .param("search", "react")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("React State Management"));

        // 2. Search by content keyword (case-insensitive)
        mockMvc.perform(get("/api/v1/notes")
                        .param("search", "tradeoffs")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Database Indexing"));

        // 3. Search that matches secondary user's content must NOT leak
        mockMvc.perform(get("/api/v1/notes")
                        .param("search", "secrets")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/notes?category=... filters notes by category")
    void getNotes_CategoryFilter() throws Exception {
        noteRepository.save(new Note(primaryUser, "Note A", "Content A", "Books"));
        noteRepository.save(new Note(primaryUser, "Note B", "Content B", "Tech"));
        noteRepository.save(new Note(primaryUser, "Note C", "Content C", "Books"));

        mockMvc.perform(get("/api/v1/notes")
                        .param("category", "Books")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("Books"))
                .andExpect(jsonPath("$[1].category").value("Books"));
    }

    @Test
    @DisplayName("GET /api/v1/notes?search=...&category=... combined filtering")
    void getNotes_CombinedSearchAndCategory() throws Exception {
        noteRepository.save(new Note(primaryUser, "Clean Architecture Book", "Robert C Martin insights", "Books"));
        noteRepository.save(new Note(primaryUser, "Pragmatic Programmer Book", "Career advice for developers", "Books"));
        noteRepository.save(new Note(primaryUser, "Architecture Design Doc", "Internal system design", "Tech"));

        mockMvc.perform(get("/api/v1/notes")
                        .param("search", "architecture")
                        .param("category", "Books")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Clean Architecture Book"));
    }

    @Test
    @DisplayName("GET /api/v1/notes?search=nonexistent returns empty list")
    void searchNotes_NoMatches_ReturnsEmptyList() throws Exception {
        noteRepository.save(new Note(primaryUser, "Note 1", "Content 1", "Work"));

        mockMvc.perform(get("/api/v1/notes")
                        .param("search", "nonexistenttermxyz")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PUT /api/v1/notes/{id} fails with 400 when title is blank")
    void updateNote_BlankTitle_Returns400() throws Exception {
        Note note = noteRepository.save(new Note(primaryUser, "Original Title", "Content", "Work"));

        UpdateNoteRequest request = new UpdateNoteRequest(
                "   ",
                "Updated content",
                "Work"
        );

        mockMvc.perform(put("/api/v1/notes/" + note.getId())
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
