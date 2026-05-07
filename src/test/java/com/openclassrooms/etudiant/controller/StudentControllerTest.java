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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

// Test d'intégration avec H2 en mémoire — pas besoin de Docker
// SpringBootTest charge le contexte Spring complet
// @AutoConfigureMockMvc configure MockMvc pour simuler les requêtes HTTP

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentControllerTest {

    // final = constante immuable accessible uniquement dans cette classe
    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "exemple@mail.com";
    private static final Long ID = 1L;

    // ici c'est faire en sorte de créer les objets directement
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    // cela sert à effectuer cette action après chaque test — supprimer les données ajoutées dans la BDD
    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
    }

    @Test
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
    public void createAlreadyExistStudent() throws Exception {
        // GIVEN : crée un étudiant avec les vraies valeurs en BDD
        // email est l'unique élément qui identifie un étudiant
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
    public void createStudentSuccessful() throws Exception {
        // GIVEN : prépare un DTO complet avec toutes les données requises
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
    public void getAllStudent() throws Exception {
        // GIVEN : pas besoin de données, on veut juste la liste

        // WHEN : simule une requête HTTP GET vers /api/students
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN : retourne 200 OK
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
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
    public void updateStudentById() throws Exception {
        // GIVEN : crée un étudiant en BDD et prépare le DTO de modification
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
// terminal : mvn test -Dtest=StudentControllerTest