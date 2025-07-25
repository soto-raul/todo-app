package todoapp.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import todoapp.backend.enums.Status;
import todoapp.backend.exception.InvalidToDoPropertiesException;
import todoapp.backend.exception.ToDoNotFoundException;
import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;
import todoapp.backend.repository.ToDoInMemoRepository;
import todoapp.backend.util.Validators;

@Service
public class ToDoService {

    private final ToDoInMemoRepository toDoInMemoRepository;
    private int nextId;

    public ToDoService(ToDoInMemoRepository toDoInMemoRepository) {
        this.toDoInMemoRepository = toDoInMemoRepository;
        nextId = 1;
    }

    public Page<ToDo> getAllToDos(Pageable pageReq) {
        return getPageContent(toDoInMemoRepository.findAll(), pageReq);

    }

    public Page<ToDo> getByCriteria(FilterCriteria filterCriteria, Pageable pageReq) {
        return getPageContent(toDoInMemoRepository.findAllByCriteria(filterCriteria), pageReq);
    }

    public ToDo addToDo(ToDo toDo) {
        // Validate submitted new ToDo's properties
        if (Validators.validateToDo(toDo)) {
            // set up new ToDo's properties
            ToDo newToDo = new ToDo();
            newToDo.setId(nextId);
            newToDo.setName(toDo.getName());
            newToDo.setDueDate(toDo.getDueDate());
            newToDo.setPriority(toDo.getPriority());

            // increment nextId value
            nextId++;

            return toDoInMemoRepository.add(newToDo);
        } else {
            throw new InvalidToDoPropertiesException();
        }

    }

    public ToDo updateToDo(int id, ToDo updatedToDo) {
        ToDo existingToDo = toDoInMemoRepository.findById(id);

        // Throw exception if no ToDo was found
        if (existingToDo == null) {
            throw new ToDoNotFoundException("No ToDo matching ID '" + id + "' was found.");
        }

        // Validate updatedToDo data
        if (Validators.validateToDo(updatedToDo)) {
            existingToDo.setName(updatedToDo.getName());
            existingToDo.setDueDate(updatedToDo.getDueDate());
            existingToDo.setPriority(updatedToDo.getPriority());
            return toDoInMemoRepository.update(id, existingToDo);
        } else {
            throw new InvalidToDoPropertiesException();
        }

    }

    public ToDo updateToDoDoneStatus(int id, Status doneStatus) {
        ToDo existingToDo = toDoInMemoRepository.findById(id);

        // Throw exception if no ToDo was found
        if (existingToDo == null) {
            throw new ToDoNotFoundException("No ToDo matching ID '" + id + "' was found.");
        }

        // Otherwise, update done status
        existingToDo.setIsDone(doneStatus);

        return toDoInMemoRepository.update(id, existingToDo);
    }

    public boolean deleteToDo(int id) {
        boolean wasDeleted = toDoInMemoRepository.delete(id);
        if (!wasDeleted) {
            throw new ToDoNotFoundException("No ToDo matching ID '" + id + "' was found.");
        }
        return wasDeleted;
    }

    private Page<ToDo> getPageContent(List<ToDo> allToDos, Pageable pageReq) {
        // get start and end of our list slice
        int start = (int) pageReq.getOffset();
        int end = Math.min((start + pageReq.getPageSize()), allToDos.size());

        // get sublist
        List<ToDo> pageContent = allToDos.subList(start, end);
        return new PageImpl<>(pageContent, pageReq, allToDos.size());
    }
}
