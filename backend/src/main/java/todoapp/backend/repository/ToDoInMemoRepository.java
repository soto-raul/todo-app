package todoapp.backend.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;

@Repository
public class ToDoInMemoRepository implements ToDoRepository {
    // In-memory storage using a List
    private List<ToDo> toDoList = new ArrayList<>();

    @Override
    public List<ToDo> findAll() {
        return toDoList;
    }

    @Override
    public List<ToDo> findAllByCriteria(FilterCriteria filterCriteria) {
        List<ToDo> filteredList = toDoList.stream()
                .filter(toDo -> matchesCriteria(toDo, filterCriteria))
                .collect(Collectors.toList());
        return filteredList;
    }

    @Override
    public ToDo findById(int id) {
        return toDoList.stream()
                .filter(toDo -> toDo.getId() == id)
                .findFirst().orElse(null);
    }

    @Override
    public ToDo add(ToDo toDo) {
        toDoList.add(toDo);
        return toDo;
    }

    @Override
    public ToDo update(int id, ToDo updatedToDo) {
        List<ToDo> updatedList = toDoList.stream().map(toDo -> toDo.getId() == updatedToDo.getId() ? updatedToDo : toDo)
                .collect(Collectors.toList());

        toDoList = updatedList;

        return findById(id);
    }

    @Override
    public boolean delete(int id) {
        return toDoList.removeIf(toDo -> toDo.getId() == id);
    }

    private boolean matchesCriteria(ToDo toDo, FilterCriteria criteria) {
        // Check if To Do matches criteria one by one. Whenever a criteria is
        // null, the filter is not applied, so we return the match as true.
        boolean matchesName = criteria.getName() == null
                || toDo.getName().toLowerCase().contains(criteria.getName().toLowerCase());

        boolean matchesPriority = criteria.getPriority() == null
                || criteria.getPriority() == toDo.getPriority();

        boolean matchesDoneStatus = criteria.getDoneStatus() == null || criteria.getDoneStatus() == toDo.getIsDone();

        return matchesName && matchesPriority && matchesDoneStatus;
    }
}
