package todoapp.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.exception.ToDoNotFoundException;
import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;
import todoapp.backend.service.ToDoService;

@WebMvcTest(ToDoController.class)
public class ToDoControllerTest {
    @MockitoBean
    private ToDoService toDoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // sample To Dos data
    ToDo toDo1;
    ToDo toDo2;
    ToDo toDo3;
    List<ToDo> sampleToDos;

    @BeforeEach
    void init() {
        mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        toDo1 = new ToDo(1, "Test API", LocalDate.of(2025, 8, 4), Priority.HIGH);
        toDo2 = new ToDo(2, "Write API documentation", null, Priority.LOW);
        toDo3 = new ToDo(3, "Finish essay", LocalDate.of(2025, 7, 18), Priority.MEDIUM);
        toDo3.setIsDone(Status.DONE);
        sampleToDos = List.of(toDo1, toDo2, toDo3);
    }

    @Test
    @DisplayName("Test for GET('/todos') endpoint when not filtering nor sorting")
    void testGetAllToDos() throws Exception {
        // expected response
        Page<ToDo> expectedPage = new PageImpl<>(sampleToDos);

        when(toDoService.getAllToDos(any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get("/todos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // succesful response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // content length is equal to the expected
                .andExpect(jsonPath("$.content", hasSize(expectedPage.getContent().size())))
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    @DisplayName("Test for GET('/todos') endpoint when filtering by at least 1 criteria but not sorting")
    void testGetAllToDosByFilterCriteria() throws Exception {
        // expected response
        Page<ToDo> expectedPage = new PageImpl<>(List.of(toDo1, toDo2));

        when(toDoService.getByCriteria(any(FilterCriteria.class), any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get("/todos").param("name", "api")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // succesful response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // content length is equal to the expected
                .andExpect(jsonPath("$.content", hasSize(expectedPage.getContent().size())))
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    @DisplayName("Test for GET('/todos') endpoint when filtering by at least 1 criteria and sorting by at least 1 parameter")
    void testGetAllToDosWithFiltersAndSorting() throws Exception {
        // expected response
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "priority"));
        Page<ToDo> expectedPage = new PageImpl<>(List.of(toDo2, toDo1), pageReq, 2);

        when(toDoService.getByCriteria(any(FilterCriteria.class), any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get("/todos").param("name", "api").param("sortBy", "priority").param("order", "ASC")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // succesful response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // content length is equal to the expected
                .andExpect(jsonPath("$.content", hasSize(expectedPage.getContent().size())))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    @DisplayName("Test for GET('/todos') endpoint when sorting by at least 1 parameter but not filtering")
    void testGetAllToDosSorted() throws Exception {
        // expected response
        Pageable pageReq = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "priority"));
        Page<ToDo> expectedPage = new PageImpl<>(List.of(toDo2, toDo3, toDo1), pageReq, sampleToDos.size());

        when(toDoService.getAllToDos(any(Pageable.class))).thenReturn(expectedPage);

        mockMvc.perform(get("/todos").param("sortBy", "priority").param("order", "ASC")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // succesful response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // content length is equal to the expected
                .andExpect(jsonPath("$.content", hasSize(expectedPage.getContent().size())))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    @DisplayName("Test for POST('/todos') endpoint. Adding a new To Do.")
    void testAddNewToDo() throws Exception {
        ToDo newToDoInput = new ToDo();
        newToDoInput.setName("Finish API testing");
        newToDoInput.setDueDate(null);
        newToDoInput.setPriority(Priority.HIGH);
        // build POST request body
        String reqBody = mapper.writeValueAsString(newToDoInput);

        when(toDoService.addToDo(any(ToDo.class))).thenReturn(newToDoInput);

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(reqBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(newToDoInput.getName())))
                .andExpect(jsonPath("$.dueDate", is(newToDoInput.getDueDate())))
                .andExpect(jsonPath("$.priority", is(newToDoInput.getPriority().toString())))
                .andExpect(jsonPath("$.isDone", is(Status.NOT_DONE.toString())));
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}') endpoint when updating an existing To Do.")
    void testUpdateToDo() throws Exception {
        ToDo updatedData = toDo1;
        updatedData.setName("Updated Name");
        // build POST request body
        String reqBody = mapper.writeValueAsString(updatedData);

        when(toDoService.updateToDo(anyInt(), any(ToDo.class))).thenReturn(updatedData);

        mockMvc.perform(put("/todos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(reqBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedData.getId())))
                .andExpect(jsonPath("$.name", is(updatedData.getName())))
                .andExpect(jsonPath("$.dueDate", is(updatedData.getDueDate().toString())))
                .andExpect(jsonPath("$.priority", is(updatedData.getPriority().toString())))
                .andExpect(jsonPath("$.isDone", is(Status.NOT_DONE.toString())));
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}') endpoint when updating a To Do that does not exist.")
    void testUpdateNonExistingToDo() throws Exception {
        ToDo updatedData = toDo1;
        updatedData.setName("Updated Name");
        // build POST request body
        String reqBody = mapper.writeValueAsString(updatedData);

        when(toDoService.updateToDo(anyInt(), any(ToDo.class))).thenThrow(ToDoNotFoundException.class);

        mockMvc.perform(put("/todos/{id}", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(reqBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}/done') endpoint when marking an existing To Do as DONE.")
    void testMarkAsDone() throws Exception {
        // expected To Do data
        toDo2.setIsDone(Status.DONE);

        when(toDoService.updateToDoDoneStatus(2, Status.DONE)).thenReturn(toDo2);

        mockMvc.perform(put("/todos/{id}/done", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(toDo2.getId())))
                .andExpect(jsonPath("$.name", is(toDo2.getName())))
                .andExpect(jsonPath("$.dueDate", is(toDo2.getDueDate())))
                .andExpect(jsonPath("$.priority", is(toDo2.getPriority().toString())))
                .andExpect(jsonPath("$.isDone", is(Status.DONE.toString())));
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}/done') endpoint when marking a non existing To Do as DONE.")
    void testMarkNonExistingToDoAsDone() throws Exception {

        when(toDoService.updateToDoDoneStatus(10, Status.DONE)).thenThrow(ToDoNotFoundException.class);

        mockMvc.perform(put("/todos/{id}/done", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}/undone') endpoint when marking an existing To Do as NOT_DONE.")
    void testMarkAsNotDone() throws Exception {
        // expected To Do data
        toDo3.setIsDone(Status.NOT_DONE);

        when(toDoService.updateToDoDoneStatus(3, Status.NOT_DONE)).thenReturn(toDo3);

        mockMvc.perform(put("/todos/{id}/undone", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(toDo3.getId())))
                .andExpect(jsonPath("$.name", is(toDo3.getName())))
                .andExpect(jsonPath("$.dueDate", is(toDo3.getDueDate().toString())))
                .andExpect(jsonPath("$.priority", is(toDo3.getPriority().toString())))
                .andExpect(jsonPath("$.isDone", is(Status.NOT_DONE.toString())));
    }

    @Test
    @DisplayName("Test for PUT('/todos/{id}/undone') endpoint when marking a non existing To Do as NOT_DONE.")
    void testMarkNonExistingToDoAsNotDone() throws Exception {

        when(toDoService.updateToDoDoneStatus(10, Status.NOT_DONE)).thenThrow(ToDoNotFoundException.class);

        mockMvc.perform(put("/todos/{id}/undone", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test for DELETE('/todos/{id}') endpoint when deleting an existing To Do.")
    void testDeleteToDo() throws Exception {
        when(toDoService.deleteToDo(1)).thenReturn(true);

        mockMvc.perform(delete("/todos/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test for DELETE('/todos/{id}') endpoint when deleting an existing To Do.")
    void testDeleteNonExistingToDo() throws Exception {
        when(toDoService.deleteToDo(10)).thenReturn(false).thenThrow(ToDoNotFoundException.class);

        mockMvc.perform(delete("/todos/{id}", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }
}
