package todoapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;
import todoapp.backend.repository.ToDoInMemoRepository;

@ExtendWith(MockitoExtension.class)
public class ToDoServiceTest {
    @Mock
    private ToDoInMemoRepository toDoRepository;

    @InjectMocks
    private ToDoService toDoService;

    private ToDo toDo1;
    private ToDo toDo2;
    private ToDo toDo3;

    @BeforeEach
    void init() {
        toDo1 = new ToDo(1, "Test API", LocalDateTime.of(2025, 8, 4, 12, 30), Priority.HIGH);
        toDo2 = new ToDo(2, "Write API documentation", null, Priority.LOW);
        toDo3 = new ToDo(3, "Finish essay", LocalDateTime.of(2025, 7, 18, 15, 00), Priority.MEDIUM);
        toDo3.setIsDone(Status.DONE);
    }

    @Test
    @DisplayName("Test for getAllToDos() method")
    void testGetAllToDos() {
        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        List<ToDo> allToDos = toDoService.getAllToDos();

        // Assertions
        assertNotNull(allToDos);
        assertTrue(allToDos.size() == 3);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by name.")
    void testGetByName() {
        FilterCriteria criteria = new FilterCriteria("API", null, null);
        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo1, toDo2));

        List<ToDo> toDosByName = toDoService.getByCriteria(criteria);

        // Assertions
        assertNotNull(toDosByName);
        assertTrue(toDosByName.size() == 2);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by priority.")
    void testGetByPriority() {
        Priority[] priorities = { Priority.HIGH, Priority.MEDIUM };
        FilterCriteria criteria = new FilterCriteria(null, priorities, null);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo1, toDo3));

        List<ToDo> toDosByPriority = toDoService.getByCriteria(criteria);

        // Assertions
        assertNotNull(toDosByPriority);
        assertTrue(toDosByPriority.size() == 2);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by status.")
    void testGetByStatus() {
        FilterCriteria criteria = new FilterCriteria(null, null, Status.DONE);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo3));

        List<ToDo> toDosByStatus = toDoService.getByCriteria(criteria);

        // Assertions
        assertNotNull(toDosByStatus);
        assertTrue(toDosByStatus.size() == 1);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering by all possible criteria.")
    void testGetByMultipleCriteria() {
        Priority[] priorities = { Priority.MEDIUM };
        FilterCriteria criteria = new FilterCriteria("Essay", priorities, Status.DONE);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo3));

        List<ToDo> toDosByMultiCriteria = toDoService.getByCriteria(criteria);

        // Assertions
        assertNotNull(toDosByMultiCriteria);
        assertTrue(toDosByMultiCriteria.size() == 1);
    }

    @Test
    @DisplayName("Test for addToDo() method")
    void testAddToDo() {
        when(toDoRepository.add(any(ToDo.class))).thenReturn(toDo1);

        ToDo addedToDo = toDoService.addToDo(toDo1);

        // Assertions
        assertNotNull(addedToDo);
        assertEquals(addedToDo.getId(), toDo1.getId());
    }

    @Test
    @DisplayName("Test for updateToDo() method")
    void testUpdateToDoData() {
        when(toDoRepository.update(eq(2), any(ToDo.class))).thenReturn(toDo2);

        toDo2.setName("Updated ToDo Name");
        toDo2.setDueDate(LocalDateTime.of(2025, 7, 23, 10, 00));
        toDo2.setPriority(Priority.HIGH);

        ToDo updatedToDo = toDoService.updateToDo(2, toDo2);

        // Assertions
        assertNotNull(updatedToDo);
        assertEquals(updatedToDo.getId(), toDo2.getId());
    }

    @Test
    @DisplayName("Test for updateToDo() method when trying to update a non existing To Do object")
    void testUpdateNonExistingToDo() {
        when(toDoRepository.update(eq(10), any(ToDo.class))).thenReturn(null);

        ToDo updatedToDo = toDoService.updateToDo(10, toDo2);

        // Assertions
        assertNull(updatedToDo);
    }

    @Test
    @DisplayName("Test for deleteToDo() method")
    void testDeleteExistingToDo() {
        when(toDoRepository.delete(1)).thenReturn(true);

        boolean wasDeleted = toDoService.deleteToDo(1);

        // Assertions
        assertTrue(wasDeleted);
    }

    @Test
    @DisplayName("Test for deleteToDo() method when trying to delete a non existent ")
    void testDeleteNonExistingToDo() {
        when(toDoRepository.delete(10)).thenReturn(false);

        boolean wasDeleted = toDoService.deleteToDo(10);

        // Assertions
        assertFalse(wasDeleted);
    }
}
