package todoapp.backend.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;

@Entity
public class ToDo {

    // ToDo Properties
    @JsonProperty("id")
    @Setter
    @Getter
    private int id;

    @JsonProperty("name")
    @Getter
    @Setter
    private String name;

    @JsonProperty("dueDate")
    @Getter
    @Setter
    private LocalDate dueDate; // Optional field, nullable

    @JsonProperty("isDone")
    @Getter
    private Status isDone;

    @JsonProperty("doneDate")
    @Getter
    private LocalDateTime doneDate; // Nullable

    @JsonProperty("priority")
    @Getter
    @Setter
    private Priority priority;

    @JsonProperty("creationDate")
    @Getter
    private LocalDateTime creationDate;

    // Default constructor
    public ToDo() {
        this.isDone = Status.NOT_DONE; // "Not Done " by default
        this.doneDate = null; // No done date initially
        this.creationDate = LocalDateTime.now();
    }

    public ToDo(int id, String name, LocalDate dueDate, Priority priority) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.isDone = Status.NOT_DONE; // "Not Done " by default
        this.doneDate = null; // No done date initially
        this.priority = priority;
        this.creationDate = LocalDateTime.now();
    }

    // Setter for isDone flag. Works for both marking as done and not done.
    public void setIsDone(Status doneStatus) {
        // If isDone status is already the one to which we are trying to
        // update to, nothing changes. No error is thrown.
        if (this.isDone == doneStatus) {
            return;
        }

        // Otherwisem, update isDone flag and doneDate
        this.isDone = doneStatus;
        if (doneStatus == Status.DONE) {
            // Set done date to today when marked as done
            this.doneDate = LocalDateTime.now();
        } else {
            // Reset done date when marked as not done
            this.doneDate = null;
        }
    }

    // Computed property completionTime
    public Duration getCompletionTime() {
        return isDone == Status.DONE ? Duration.between(creationDate, doneDate)
                : Duration.between(creationDate, LocalDateTime.now());
    }
}
