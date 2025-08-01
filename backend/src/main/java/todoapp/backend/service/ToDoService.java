package todoapp.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // Comparators map
    Map<String, Comparator<ToDo>> comparators;

    public ToDoService(ToDoInMemoRepository toDoInMemoRepository) {
        this.toDoInMemoRepository = toDoInMemoRepository;
        nextId = 1;
        comparators = new HashMap<>();
        comparators.put("dueDate",
                Comparator.comparing(ToDo::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        comparators.put("priority",
                Comparator.comparing(ToDo::getPriority, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public Page<ToDo> getAllToDos(Pageable pageReq) {
        List<ToDo> allToDos = new ArrayList<>(toDoInMemoRepository.findAll());

        // sort if necessary
        if (pageReq.getSort() != null) {
            Collections.sort(allToDos, getComparator(pageReq.getSort()));
        }

        return getPageContent(allToDos, pageReq);

    }

    public Page<ToDo> getByCriteria(FilterCriteria filterCriteria, Pageable pageReq) {
        List<ToDo> allToDos = new ArrayList<>(toDoInMemoRepository.findAllByCriteria(filterCriteria));

        // sort if necessary
        if (pageReq.getSort() != null) {
            Collections.sort(allToDos, getComparator(pageReq.getSort()));
        }

        return getPageContent(allToDos, pageReq);
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

    public Map<String, Double> getMetrics() {
        // get all completed To Dos
        List<ToDo> allCompleted = toDoInMemoRepository.findAll().stream()
                .filter(toDo -> (toDo.getIsDone() == Status.DONE))
                .collect(Collectors.toList());

        Double avgAll = allCompleted.stream()
                .collect(Collectors.averagingDouble(toDo -> toDo.getCompletionTime().toSeconds()));

        // average completion time grouped by priority
        Map<String, Double> avgGrouped = allCompleted.stream()
                .collect(Collectors.groupingBy(toDo -> toDo.getPriority().toString(),
                        Collectors.averagingDouble(toDo -> toDo.getCompletionTime().toSeconds())));

        // merge all results into 1 map
        Map<String, Double> metrics = new HashMap<String, Double>();
        metrics.put("ALL", avgAll);
        metrics.putAll(avgGrouped);

        return metrics;
    }

    private Page<ToDo> getPageContent(List<ToDo> allToDos, Pageable pageReq) {
        // get start and end of our list slice
        int start = (int) pageReq.getOffset();
        int end = Math.min((start + pageReq.getPageSize()), allToDos.size());

        // get sublist
        List<ToDo> pageContent = allToDos.subList(start, end);
        return new PageImpl<>(pageContent, pageReq, allToDos.size());
    }

    private Comparator<ToDo> getComparator(Sort sortOrders) {
        Comparator<ToDo> fullComparator = sortOrders.stream()
                .map(order -> (order.getDirection() == Sort.Direction.ASC) ? comparators.get(order.getProperty())
                        : comparators.get(order.getProperty()).reversed())
                .reduce(Comparator::thenComparing).orElse((toDo1, toDo2) -> 0);

        return Comparator.nullsLast(fullComparator);
    }
}
