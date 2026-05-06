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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "exemple@mail.com";
    private static final Long ID = 1L;


    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

    }

// Ici c'est un action à réaliser après chaque test pour supprimer les données ajoutées dans la BDD
    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
    }

    @Test
    public void createStudentWithoutRequiredData() throws Exception {
        // GIVEN : simule le fait d'ajouter des champs vide de l'étudiant. 
        StudentDTO studentDTO = new StudentDTO();

        // WHEN simule une requête http post pour publier
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //THEN renvoie une erreur
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createAlreadyExistStudent() throws Exception {
        // GIVEN créer un étudiant avec les vrais valeurs, et créé aussi un étudiant existant avec les mêmes valeurs (DTO)
        // email est l'unique élément qui identifie un étudiant. Il peut y avoir le même nom et prénom
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);

        // WHEN simule une requête http post pour publier la création de l'étudiant. 
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // THEN renvoie l'erreur. 
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createStudentSuccessful() throws Exception {
        // GIVEN - Aucune difficulté, il suffit de changer les register par student. 
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void getAllStudent() throws Exception {
        // GIVEN - Pas besoin de given ici, je veux juste la liste 


        // WHEN - ici changer le post par GET pour obtenir et mettre isOk. 
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getStudentById() throws Exception {
        // GIVEN - Reprendre les élements essentiels de Student comme dans le test Already
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);


        // WHEN - ici changer surtout le GET mais aussi URL avec le CHEMIN 
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateStudentById() throws Exception {
        // GIVEN - Reprendre les élements essentiels de Get Student
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName(FIRST_NAME);
        studentDTO.setLastName(LAST_NAME);
        studentDTO.setEmail(EMAIL);


        // WHEN - ici changer GET par PUT 
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + ID)
                        .content(objectMapper.writeValueAsString(studentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteStudentById() throws Exception {
        // GIVEN - Reprendre les élements essentiels de Student comme dans le test Already
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        studentService.create(student);


        // WHEN - ici changer surtout le GET mais aussi URL avec le CHEMIN 
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
// terminal : mvn test -Dtest=StudentControllerTest