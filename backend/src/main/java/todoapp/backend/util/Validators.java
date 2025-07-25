package todoapp.backend.util;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.model.ToDo;

public final class Validators {

    /**
     * Validates that ToDo's structure is valid.
     * - name: Max. 120 charactes, not blank, not null
     * - isDone: Status.DONE or Status.NOT_DONE, not null
     * - priority: Priority.HIGH/MEDIUM/LOW, not null
     *
     * @param toDo
     * @return
     */
    public static boolean validateToDo(ToDo toDo) {
        boolean isValid = validateToDoName(toDo.getName()) && toDo.getIsDone() != null && toDo.getPriority() != null;
        return isValid;
    }

    /**
     * Validates that the ToDo's name has a maximum of 120 characters and is not
     * blank nor null.
     *
     * @param name the ToDo's name
     * @return True if the name meets the requirements, False if not
     */
    public static boolean validateToDoName(String name) {
        return name != null && !name.isBlank() && name.length() <= 120;
    }

    public static boolean validateAllCriteriaAreNull(String name, Priority priority, Status doneStatus) {
        return name == null && priority == null && doneStatus == null;
    }
}
