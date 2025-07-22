package todoapp.backend.repository;

import java.util.List;

import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;

public interface ToDoRepository {

    /**
     * Retrieves all ToDos elements.
     *
     * @return a list of all ToDos
     */
    public List<ToDo> findAll();

    /**
     * Retrieves ToDos filtered by the following criteria: full or partial
     * name, priority, and/or done status.
     *
     * @param name       the full or partial name of the ToDo (null when
     *                   not filtering)
     * @param priority   the selected Priorities of the ToDo (null when not
     *                   filtering)
     * @param doneStatus the done status of the ToDo (null when not filtering)
     * @return a list of all ToDos that match the given criteria
     */
    public List<ToDo> findAllByCriteria(FilterCriteria filterCriteria);

    /**
     * Retrieves a ToDo given an ID.
     * 
     * @param id the ID of the ToDo to be found
     * @return the ToDo object that matches the given ID. If it's not found, return
     *         null
     */
    public ToDo findById(int id);

    /**
     * Adds a new ToDo.
     *
     * @param toDo the ToDo to add
     * @return the newly added ToDo
     */
    public ToDo add(ToDo toDo);

    /**
     * Updates an existing ToDo's name, due date(nullable), priority and/or done
     * status.
     * If done status is updated, ToDo's done date will also be modified.
     *
     * @param id   the ID of the ToDo to update
     * @param toDo the ToDo with updated properties
     */
    public ToDo update(int id, ToDo toDo);

    /**
     * Deletes the ToDo with the given ID. Returns True if an element was removed.
     * If it was not found, no changes to the storage are made and returns False.
     *
     * @param id the ID of the ToDo to delete
     * @return True if a ToDo was removed, otherwise returns False
     */
    public boolean delete(int id);
}
