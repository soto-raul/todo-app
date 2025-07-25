import type { Status, ToDo } from "../../shared/types";
import "./toDoList.css";

const ToDoList = ({
  toDos,
  onEditClick,
  onDeleteClick,
  onDoneStatusChange,
}: {
  toDos: ToDo[];
  onEditClick: (toDo: ToDo) => void;
  onDeleteClick: (id: number) => void;
  onDoneStatusChange: (id: number, newStatus: Status) => void;
}) => {
  if (toDos.length === 0) {
    return (
      <section>
        <div>
          <table>
            <thead>
              <tr>
                <th className="done-col"></th>
                <th className="name-col">Name</th>
                <th className="priority-col">Priority</th>
                <th className="due-date-col">Due Date</th>
                <th className="actions-col"></th>
              </tr>
            </thead>
          </table>
          <div className="no-todos-msg">No To Do tasks were found</div>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div>
        <table>
          <thead>
            <tr>
              <th className="done-col"></th>
              <th className="name-col">Name</th>
              <th className="priority-col">Priority</th>
              <th className="due-date-col">Due Date</th>
              <th className="actions-col"></th>
            </tr>
          </thead>
          <tbody>
            {toDos.map((toDo: ToDo, index: number) => (
              <tr key={index}>
                <td className="done-checkbox">
                  <input
                    type="checkbox"
                    name="isDone"
                    id="isDone"
                    checked={toDo.isDone === "DONE"}
                    onChange={() =>
                      onDoneStatusChange(
                        toDo.id,
                        toDo.isDone === "DONE" ? "NOT_DONE" : "DONE"
                      )
                    }
                  />
                </td>
                <td
                  className={
                    toDo.isDone === "DONE" ? "task-done" : "task-not-done"
                  }
                >
                  {toDo.name}
                </td>
                <td>{toDo.priority}</td>
                <td>
                  {toDo.dueDate !== null ? toDo.dueDate.toLocaleString() : "-"}
                </td>
                <td className="actions">
                  <button
                    className="edit-btn"
                    onClick={() => {
                      onEditClick(toDo);
                    }}
                  >
                    Edit
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => onDeleteClick(toDo.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default ToDoList;
