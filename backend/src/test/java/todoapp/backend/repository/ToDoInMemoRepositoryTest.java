package todoapp.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;

class ToDoInMemoRepositoryTest {

    private ToDoInMemoRepository toDoRepository;

    // Sample ToDo used on initial setup for each test.
    private ToDo toDo1;
    private ToDo toDo2;
    private ToDo toDo3;

    @BeforeEach
    void init() {
        toDoRepository = new ToDoInMemoRepository();
        toDo1 = new ToDo(1, "Test API", LocalDateTime.of(2025, 8, 4, 12, 30), Priority.HIGH);
        toDo2 = new ToDo(2, "Write API documentation", null, Priority.LOW);
        toDo3 = new ToDo(3, "Finish essay", LocalDateTime.of(2025, 7, 18, 15, 00), Priority.MEDIUM);
        toDoRepository.add(toDo1);
        toDoRepository.add(toDo2);
        toDoRepository.add(toDo3);
    }

    @Test
    void testFindAllToDos() {
        // Check that all initial ToDos are present
        List<ToDo> all = toDoRepository.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testFindById() {
        ToDo toDo = toDoRepository.findById(1);

        // Assertions
        assertNotNull(toDo);
        assertEquals(toDo, toDo1);
    }

    @Test
    void testFindByName() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setName("API");

        List<ToDo> filteredToDos = toDoRepository.findAllByCriteria(criteria);

        // Assertions
        assertEquals(2, filteredToDos.size());
        assertTrue(filteredToDos.contains(toDo1));
        assertTrue(filteredToDos.contains(toDo2));
    }

    @Test
    void testFindByPriority() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setPriorities(new Priority[] { Priority.HIGH, Priority.MEDIUM });

        List<ToDo> filteredToDos = toDoRepository.findAllByCriteria(criteria);

        // Assertions
        assertEquals(2, filteredToDos.size());
        assertTrue(filteredToDos.contains(toDo1));
        assertTrue(filteredToDos.contains(toDo3));
    }

    @Test
    void testFindByDoneStatus() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setDoneStatus(Status.DONE);

        toDo1.setIsDone(Status.DONE);
        toDoRepository.update(1, toDo1);

        List<ToDo> filteredToDos = toDoRepository.findAllByCriteria(criteria);

        // Assertions
        assertEquals(1, filteredToDos.size());
        assertTrue(filteredToDos.contains(toDo1));
    }

    @Test
    void testFindByMultipleFilters() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setName("essay");
        criteria.setPriorities(new Priority[] { Priority.MEDIUM });
        criteria.setDoneStatus(Status.NOT_DONE);

        List<ToDo> filteredToDos = toDoRepository.findAllByCriteria(criteria);

        // Assertions
        assertEquals(1, filteredToDos.size());
        assertTrue(filteredToDos.contains(toDo3));
    }

    @Test
    void testAddToDo() {
        ToDo newToDo = new ToDo(5, "New Task", null, Priority.HIGH);
        ToDo addedToDo = toDoRepository.add(newToDo);

        List<ToDo> all = toDoRepository.findAll();

        // Assertion
        assertEquals(4, all.size());
        assertTrue(all.contains(newToDo));
        assertTrue(addedToDo == newToDo);
    }

    @Test
    void testUpdateToDo() {
        ToDo existingToDo = toDo3;

        existingToDo.setName("Updated ToDo Name");
        existingToDo.setDueDate(LocalDateTime.of(2025, 7, 20, 10, 00));
        existingToDo.setPriority(Priority.LOW);

        ToDo updatedToDo = toDoRepository.update(3, existingToDo);

        // Assertions
        assertNotNull(updatedToDo);
        assertTrue(toDoRepository.findAll().contains(updatedToDo));
        // the returned ToDo (updatedToDo) equals the original after changes (existing)
        assertEquals(updatedToDo, existingToDo);
    }

    @Test
    void testUpdateDoneStatus() {
        ToDo existingToDo = toDo1;
        existingToDo.setIsDone(Status.DONE);

        ToDo updatedToDo = toDoRepository.update(1, existingToDo);

        // Assertions
        assertNotNull(updatedToDo);
        assertEquals(updatedToDo.getIsDone(), Status.DONE);
        // the returned ToDo (updatedToDo) equals the original after changes (existing)
        assertEquals(existingToDo, updatedToDo);
    }

    @Test
    void testUpdateNonExistingToDo() {
        ToDo updatedToDo = toDoRepository.update(10, toDo1);

        // Assertions
        assertNull(updatedToDo);
        assertFalse(toDoRepository.findAll().contains(updatedToDo));
    }

    @Test
    void testDeleteExistingToDo() {
        boolean wasDeleted = toDoRepository.delete(1);

        // Get all
        List<ToDo> all = toDoRepository.findAll();

        // Assertions
        assertTrue(wasDeleted);
        assertEquals(2, all.size());
    }

    @Test
    void testDeleteNonExistingToDo() {
        boolean wasDeleted = toDoRepository.delete(10);

        // Get all
        List<ToDo> all = toDoRepository.findAll();

        // Assertions
        assertFalse(wasDeleted);
        assertEquals(3, all.size());
    }
}
