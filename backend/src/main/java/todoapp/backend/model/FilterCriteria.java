package todoapp.backend.model;

import lombok.Getter;
import lombok.Setter;
import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;

@Getter
@Setter
public class FilterCriteria {
    // All can be null, which means there is no filtering by that criteria
    private String name;
    private Priority priority;
    private Status doneStatus;

    // Default constructor
    public FilterCriteria() {

    }

    public FilterCriteria(String name, Priority priority, Status doneStatus) {
        this.name = name;
        this.priority = priority;
        this.doneStatus = doneStatus;
    }
}
