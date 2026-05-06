package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
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

// Ici c'est un test d'intégration, car cela concerne l'user (l'admin). On fait le lien avec user mais
// aussi la base de donnée. Je vois @Testcontainers qui fait le lien avec la BDD. 
// SpringBootTest sert à chercher l'environnement de test avec spring boot grâce aussi  l'ajout d'import en haut. 
// @AutoConfigureMockMvc configure spécifiquement MockMvc

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerTest {

// final = constante immuable accessible uniquement dans cette classe. Ne peut pas être modifiée
    private static final String URL = "/api/register";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";


// on initialise une nouvelle base de donnée pour ce test. Le latest et l'image récupéré. 
    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

// ici c'est faire en sorte de créer les objets directement
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

// c'est configurer la base de donnée en prenant en compte les élements précédents
    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

    }

// cela sert à effectuer cette action après chaque test. de supprimer toute information de l'user de la base de donnée.
    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void registerUserWithoutRequiredData() throws Exception {
        // GIVEN j'instancie un registerDTO vide, 
        RegisterDTO registerDTO = new RegisterDTO();

        // WHEN je simule une requête HTTP POST vers /api/register
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //THEN ici le but est que cela retour une erreur. 
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerAlreadyExistUser() throws Exception {
        // GIVEN on crée un utilisateur complet et on l'enregistre vraiment en base. 
        // Puis avec le dto on simule d'un utilisateur avec les mêmes données. 
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        userService.register(user);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        // WHEN simule une requête HTTP POST avec le DTO du doublon
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //THEN retourne une erreur car l'utilisateur existe déja. 
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void registerUserSuccessful() throws Exception {
        // GIVEN Je prépare un DTO complet avec toutes les données requises
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName(FIRST_NAME);
        registerDTO.setLastName(LAST_NAME);
        registerDTO.setLogin(LOGIN);
        registerDTO.setPassword(PASSWORD);

        // WHEN puis je simule une requête http avec post
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //THEN retourne positif
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
// Pour faire le test avec le terminal : mvn test -Dtest=UserControllerTest
// global : mvn test jacoco:report