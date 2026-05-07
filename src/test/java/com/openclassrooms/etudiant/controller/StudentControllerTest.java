package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.StudentDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

// Test d'intégration avec MySQL via profil CI sur GitHub Actions
// @WithMockUser simule un utilisateur authentifié pour passer Spring Security

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentControllerTest {

    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "exemple@mail.com";
    private static final Long ID = 1L;

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void createStudentWithoutRequiredData() throws Exception {
        // GIVEN : instancie un StudentDTO vide
        StudentDTO studentDTO = new StudentDTO();

        // WHEN : simule une requête HTTP POST vers /api/students
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : renvoie une erreur
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void createAlreadyExistStudent() throws Exception {
        // GIVEN : crée un étudiant en BDD puis simule un doublon avec DTO
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);

        // WHEN : simule une requête HTTP POST avec le DTO du doublon
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : renvoie une erreur car l'étudiant existe déjà
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void createStudentSuccessful() throws Exception {
        // GIVEN : prépare un DTO complet
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);

        // WHEN : simule une requête HTTP POST
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne positif
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser
    public void getAllStudent() throws Exception {
        // GIVEN : pas besoin de données

        // WHEN : simule une requête HTTP GET vers /api/students
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne 200 OK
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void getStudentById() throws Exception {
        // GIVEN : crée un étudiant en BDD
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        // WHEN : simule une requête HTTP GET vers /api/students/1
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne 200 OK
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void updateStudentById() throws Exception {
        // GIVEN : crée un étudiant en BDD et prépare le DTO
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);

        // WHEN : simule une requête HTTP PUT vers /api/students/1
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + ID)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne 200 OK
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void deleteStudentById() throws Exception {
        // GIVEN : crée un étudiant en BDD
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        // WHEN : simule une requête HTTP DELETE vers /api/students/1
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne 204 No Content
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}