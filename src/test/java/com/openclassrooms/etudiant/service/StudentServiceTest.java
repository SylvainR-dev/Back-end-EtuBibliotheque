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
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());

        // WHEN
        studentService.create(student);

        // THEN
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isEqualTo(student);
    }

    @Test
    public void test_see_all_student() {
        // GIVEN
        when(studentRepository.findAll()).thenReturn(List.of());

        // WHEN
        studentService.getAll();


        // THEN
        verify(studentRepository).findAll();
    }

    @Test
    public void test_see_student_by_id() {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        // WHEN
        studentService.getById(ID);

        // THEN
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
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);

        // WHEN
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
