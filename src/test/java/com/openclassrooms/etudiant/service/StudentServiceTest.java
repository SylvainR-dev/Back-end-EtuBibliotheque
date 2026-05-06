package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// test unitaire ici, il y a le mock

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "exemple@mail.com";
    private static final Long ID = 1L;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentService studentService;

    @Test
    public void test_create_null_student_throws_IllegalArgumentException() {
        // GIVEN

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.create(null));
    }

    @Test
    public void test_create_already_exist_student_throws_IllegalArgumentException() {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(student));

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.create(student));
    }

    @Test
    public void test_create_student() {
        // GIVEN je créé un étudiant en dur et le mail est la clé. Raison pour laquelle il y a findByEmail
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());

        // WHEN ici à partir du StudentService je vais créer l'étudiant. 
        studentService.create(student);

        // THEN Ici on vérifie qu'il est bien enregistré avec save d'une part mais aussi avec getValue pour vériier que cela correspond avec étudiant. 
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(student);
    }

    @Test
    public void test_see_all_student() {
        // GIVEN ici finfAll pour voir tous
        when(studentRepository.findAll()).thenReturn(List.of());

        // WHEN l'action est de tous les prendre. 
        studentService.getAll();


        // THEN vérifie si il affiche tous les étudiants. 
        verify(studentRepository).findAll();
    }

    @Test
    public void test_see_student_by_id() {
        // GIVEN ici la clé est l'ID. 
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        // WHEN ici l'action est de prendre l'information à partir de l'id
        studentService.getById(ID);

        // THEN de v"rifier s'il affiche toutes les infos de l'id. 
        verify(studentRepository).findById(ID);
    }

    @Test
    public void test_see_student_null_throws_IllegalArgumentException() {
        // GIVEN

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.getById(null));
    }

    @Test
    public void test_update_student_by_id() {
        // GIVEN On prend les infos de création de l'étudiant. 
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);

        // WHEN : ici l'action avec update est de modifier les informations de l'étudiant. Pour ça qu'il y a 2 paramètres 
        studentService.update(ID, student);

        // THEN
        verify(studentRepository).save(student);
    }

    @Test
    public void test_delete_student_by_id() {
        // GIVEN


        // WHEN 
        studentService.delete(ID);

        // THEN
        verify(studentRepository).deleteById(ID);
    }
}
