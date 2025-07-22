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
    private Priority[] priorities;
    private Status doneStatus;

    // Default constructor
    public FilterCriteria() {

    }

    public FilterCriteria(String name, Priority[] priorities, Status doneStatus) {
        this.name = name;
        this.priorities = priorities;
        this.doneStatus = doneStatus;
    }
}
