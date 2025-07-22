package todoapp.backend.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.model.ToDo;
import todoapp.backend.service.ToDoService;

@WebMvcTest
public class ToDoControllerTest {
    @MockitoBean
    private ToDoService toDoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private ToDo newToDoInput;

    @BeforeEach
    void init() {
        newToDoInput = new ToDo();
        newToDoInput.setName("Finish API testing");
        newToDoInput.setDueDate(null);
        newToDoInput.setPriority(Priority.HIGH);
    }

    @Test
    void addNewToDo() throws Exception {
        when(toDoService.addToDo(newToDoInput)).thenReturn(newToDoInput);

        this.mockMvc
                .perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newToDoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(newToDoInput.getName())))
                .andExpect(jsonPath("$.dueDate", is(newToDoInput.getDueDate())))
                .andExpect(jsonPath("$.priority", is(newToDoInput.getPriority())))
                .andExpect(jsonPath("$.isDone", is(Status.NOT_DONE)));
    }
}
