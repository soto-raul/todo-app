package todoapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.exception.ToDoNotFoundException;
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
        toDo1 = new ToDo(1, "Test API", LocalDate.of(2025, 8, 4), Priority.HIGH);
        toDo2 = new ToDo(2, "Write API documentation", null, Priority.LOW);
        toDo3 = new ToDo(3, "Finish essay", LocalDate.of(2025, 7, 18), Priority.MEDIUM);
        toDo3.setIsDone(Status.DONE);
    }

    @Test
    @DisplayName("Test for getAllToDos() method")
    void testGetAllToDos() {
        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        Pageable pageReq = PageRequest.of(0, 10);
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // Assertions
        assertNotNull(allToDos);
        assertTrue(allToDos.size() == 3);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by name.")
    void testGetByName() {
        FilterCriteria criteria = new FilterCriteria("API", null, null);
        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo1, toDo2));

        Pageable pageReq = PageRequest.of(0, 10);
        List<ToDo> toDosByName = toDoService.getByCriteria(criteria, pageReq).getContent();

        // Assertions
        assertNotNull(toDosByName);
        assertTrue(toDosByName.size() == 2);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by priority.")
    void testGetByPriority() {
        FilterCriteria criteria = new FilterCriteria(null, Priority.MEDIUM, null);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo3));

        Pageable pageReq = PageRequest.of(0, 10);
        List<ToDo> toDosByPriority = toDoService.getByCriteria(criteria, pageReq).getContent();

        // Assertions
        assertNotNull(toDosByPriority);
        assertTrue(toDosByPriority.size() == 1);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering only by status.")
    void testGetByStatus() {
        FilterCriteria criteria = new FilterCriteria(null, null, Status.DONE);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo3));

        Pageable pageReq = PageRequest.of(0, 10);
        List<ToDo> toDosByStatus = toDoService.getByCriteria(criteria, pageReq).getContent();

        // Assertions
        assertNotNull(toDosByStatus);
        assertTrue(toDosByStatus.size() == 1);
    }

    @Test
    @DisplayName("Test for getByCriteria() method when filtering by all possible criteria.")
    void testGetByMultipleCriteria() {
        FilterCriteria criteria = new FilterCriteria("Essay", Priority.MEDIUM, Status.DONE);

        when(toDoRepository.findAllByCriteria(criteria)).thenReturn(List.of(toDo3));

        Pageable pageReq = PageRequest.of(0, 10);
        List<ToDo> toDosByMultiCriteria = toDoService.getByCriteria(criteria, pageReq).getContent();

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
        when(toDoRepository.findById(anyInt())).thenReturn(toDo2);
        when(toDoRepository.update(anyInt(), any(ToDo.class))).thenReturn(toDo2);

        toDo2.setName("Updated ToDo Name");
        toDo2.setDueDate(LocalDate.of(2025, 7, 23));
        toDo2.setPriority(Priority.HIGH);

        ToDo updatedToDo = toDoService.updateToDo(2, toDo2);

        // Assertions
        assertNotNull(updatedToDo);
        assertEquals(updatedToDo.getId(), toDo2.getId());
    }

    @Test()
    @DisplayName("Test for updateToDo() method when trying to update a non existing To Do object")
    void testUpdateNonExistingToDo() throws ToDoNotFoundException {
        when(toDoRepository.findById(anyInt())).thenReturn(null).thenThrow(ToDoNotFoundException.class);

        assertThrows(ToDoNotFoundException.class, () -> toDoService.updateToDo(10, toDo2));
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
    void testDeleteNonExistingToDo() throws ToDoNotFoundException {
        when(toDoRepository.delete(10)).thenReturn(false);

        assertThrows(ToDoNotFoundException.class, () -> toDoService.deleteToDo(10));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority ASC")
    void testGetAllToDosSortedByPriorityAsc() {
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "priority"));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 3);
        assertEquals(allToDos, List.of(toDo2, toDo3, toDo1));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority DESC")
    void testGetAllToDosSortedByPriorityDesc() {
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "priority"));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 3);
        assertEquals(allToDos, List.of(toDo1, toDo3, toDo2));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by due date ASC")
    void testGetAllToDosSortedByDueDateAsc() {
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dueDate"));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 3);
        assertEquals(allToDos, List.of(toDo3, toDo1, toDo2));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by due date DESC")
    void testGetAllToDosSortedByDueDateDesc() {
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dueDate"));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 3);
        assertEquals(allToDos, List.of(toDo2, toDo1, toDo3));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority and due date, both ASC")
    void testGetAllToDosSortedByPriorityAndDueDateAsc() {
        ToDo extraToDo = new ToDo(4, "Extra sample To Do", null, Priority.MEDIUM);
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3, extraToDo);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // setup sort orders
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.ASC, "priority"));
        sortOrders.add(new Sort.Order(Sort.Direction.ASC, "dueDate"));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(sortOrders));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 4);
        assertEquals(allToDos, List.of(toDo2, toDo3, extraToDo, toDo1));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority and due date, both DESC")
    void testGetAllToDosSortedByPriorityAndDueDateDesc() {
        ToDo extraToDo = new ToDo(4, "Extra sample To Do", null, Priority.MEDIUM);
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3, extraToDo);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // setup sort orders
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.DESC, "priority"));
        sortOrders.add(new Sort.Order(Sort.Direction.DESC, "dueDate"));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(sortOrders));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 4);
        assertEquals(allToDos, List.of(toDo1, extraToDo, toDo3, toDo2));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority ASC and due date DESC")
    void testGetAllToDosSortedByPriorityAscAndDueDateDesc() {
        ToDo extraToDo = new ToDo(4, "Extra sample To Do", null, Priority.MEDIUM);
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3, extraToDo);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // setup sort orders
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.ASC, "priority"));
        sortOrders.add(new Sort.Order(Sort.Direction.DESC, "dueDate"));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(sortOrders));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 4);
        assertEquals(allToDos, List.of(toDo2, extraToDo, toDo3, toDo1));
    }

    @Test
    @DisplayName("Test for getAllToDos() when sorting by priority DESC and due date ASC")
    void testGetAllToDosSortedByPriorityDescAndDueDateAsc() {
        ToDo extraToDo = new ToDo(4, "Extra sample To Do", null, Priority.MEDIUM);
        List<ToDo> toDosReturned = List.of(toDo1, toDo2, toDo3, extraToDo);
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>(toDosReturned));

        // setup sort orders
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.DESC, "priority"));
        sortOrders.add(new Sort.Order(Sort.Direction.ASC, "dueDate"));

        // make request to get To Dos sorted
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(sortOrders));
        List<ToDo> allToDos = toDoService.getAllToDos(pageReq).getContent();

        // assertions
        assertNotNull(allToDos);
        assertEquals(allToDos.size(), 4);
        assertEquals(allToDos, List.of(toDo1, toDo3, extraToDo, toDo2));
    }

    @Test
    @DisplayName("Test for getMetrics() when no To Dos are marked as DONE")
    void testGetMetricsWhenNoToDosAreDone() {
        toDo3.setIsDone(Status.NOT_DONE); // mark the sample ToDo as NOT_DONE

        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        // call service method to get metrics
        Map<String, Double> metrics = toDoService.getMetrics();

        // assertions
        assertEquals(metrics.size(), 1); // metrics map only has 1 key
        assertTrue(metrics.containsKey("ALL")); // its only key is ALL
        assertEquals(metrics.get("ALL"), 0.0);
    }

    @Test
    @DisplayName("Test for getMetrics() when at least 1 To Do per priority is marked as DONE")
    void testGetMetricsWhenAtLeastOneToDoPerPriorityIsDone() {
        // mark each To Do per priority as DONE
        toDo1.setIsDone(Status.DONE);
        toDo2.setIsDone(Status.DONE);

        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        // call service method to get metrics
        Map<String, Double> metrics = toDoService.getMetrics();

        // assertions
        assertEquals(metrics.size(), 4); // metrics map only has 4 keys
        assertTrue(metrics.containsKey("ALL"));
        assertTrue(metrics.containsKey("HIGH"));
        assertTrue(metrics.containsKey("MEDIUM"));
        assertTrue(metrics.containsKey("LOW"));
    }

    @Test
    @DisplayName("Test for getMetrics() when only HIGH priority To Dos are marked as DONE")
    void testGetMetricsWhenOnlyHighPriorityAreDone() {
        // mark only HIGH priority To Do as DONE
        toDo1.setIsDone(Status.DONE);
        toDo3.setIsDone(Status.NOT_DONE);

        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        // call service method to get metrics
        Map<String, Double> metrics = toDoService.getMetrics();

        // assertions
        assertEquals(metrics.size(), 2); // metrics map only has 2 keys
        assertTrue(metrics.containsKey("ALL"));
        assertTrue(metrics.containsKey("HIGH"));
    }

    @Test
    @DisplayName("Test for getMetrics() when only MEDIUM priority To Dos are marked as DONE")
    void testGetMetricsWhenOnlyMediumPriorityAreDone() {
        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        // call service method to get metrics
        Map<String, Double> metrics = toDoService.getMetrics();

        // assertions
        assertEquals(metrics.size(), 2); // metrics map only has 2 keys
        assertTrue(metrics.containsKey("ALL"));
        assertTrue(metrics.containsKey("MEDIUM"));
    }

    @Test
    @DisplayName("Test for getMetrics() when only LOW priority To Dos are marked as DONE")
    void testGetMetricsWhenOnlyLowPriorityAreDone() {
        // mark only LOW priority To Dos as DONE
        toDo2.setIsDone(Status.DONE);
        toDo3.setIsDone(Status.NOT_DONE);

        when(toDoRepository.findAll()).thenReturn(List.of(toDo1, toDo2, toDo3));

        // call service method to get metrics
        Map<String, Double> metrics = toDoService.getMetrics();

        // assertions
        assertEquals(metrics.size(), 2); // metrics map only has 2 keys
        assertTrue(metrics.containsKey("ALL"));
        assertTrue(metrics.containsKey("LOW"));
    }
}
