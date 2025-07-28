import React, { useEffect, useState } from "react";
import type { Priority, ToDo, ToDoCreationData } from "../../shared/types";
import { isNameValid } from "../../shared/validators";
import "./ToDoModalForm.css";

const ToDoForm = ({
  isOpen,
  initialData,
  onCancel,
  onSave,
}: {
  isOpen: boolean;
  initialData: ToDo | null;
  onCancel: () => void;
  onSave: (formData: ToDoCreationData | ToDo) => void;
}) => {
  // default inputs values
  const defaultInputs = {
    name: "",
    dueDate: "",
    priority: "HIGH",
  };

  // min dueDate value = today
  const today = new Date().toISOString().substring(0, 10);

  // validity of name input
  const [nameIsValid, setNameIsValid] = useState(false);

  // form input data state
  const [formInputs, setFormInputs] = useState(defaultInputs);

  useEffect(() => {
    setFormInputs(
      initialData === null
        ? defaultInputs
        : {
            name: initialData.name,
            dueDate:
              initialData.dueDate === null
                ? ""
                : initialData.dueDate.toString(),
            priority: initialData.priority as string,
          }
    );
    setNameIsValid(isNameValid(formInputs.name)); // check name validity
  }, [initialData]);

  // handle form submission
  const handleSubmit = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    // build form data to be submitted accordingly
    const submittedData = {
      name: formInputs.name,
      dueDate: formInputs.dueDate === "" ? null : new Date(formInputs.dueDate),
      priority: formInputs.priority as Priority,
    };

    // reset inputs
    setFormInputs(defaultInputs);

    // When initialData is null -> Add a new To Do -> ToDoCreationData
    if (initialData === null) {
      onSave(submittedData);
    } else {
      // Otherwise, we are updating an existing To Do -> ToDo
      const updatedToDo: ToDo = {
        ...initialData,
        ...submittedData,
      };
      onSave(updatedToDo);
    }
  };

  if (!isOpen) {
    return null; // do not render if it should not be visible
  }

  return (
    <div className="modal-overlay">
      <div
        className="modal-container"
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        <h2>To Do Task Data</h2>
        <form noValidate>
          <div className="input-field">
            <label htmlFor="name">Name</label>
            <input
              type="text"
              name="name"
              id="name"
              value={formInputs.name}
              placeholder="Enter To Do task name..."
              maxLength={120}
              onChange={(e) => {
                setFormInputs({
                  ...formInputs,
                  [e.target.name]: e.target.value,
                });
                setNameIsValid(isNameValid(e.target.value));
              }}
            />
            {!nameIsValid && (
              <p className="error-msg">
                Invalid name input. Name cannot be empty and must contain a
                maximum of 120 characters.
              </p>
            )}
          </div>
          <div className="input-field">
            <label htmlFor="priority">Priority</label>
            <select
              name="priority"
              id="priority"
              value={formInputs.priority}
              onChange={(e) => {
                setFormInputs({
                  ...formInputs,
                  [e.target.name]: e.target.value,
                });
              }}
            >
              <option value="HIGH">High</option>
              <option value="MEDIUM">Medium</option>
              <option value="LOW">Low</option>
            </select>
          </div>
          <div className="input-field">
            <label htmlFor="dueDate">Due Date</label>
            <input
              type="date"
              name="dueDate"
              id="dueDate"
              value={formInputs.dueDate}
              min={today}
              onChange={(e) => {
                setFormInputs({
                  ...formInputs,
                  [e.target.name]: e.target.value,
                });
              }}
            />
          </div>
          <div className="form-actions">
            <button type="button" onClick={onCancel}>
              Cancel
            </button>
            <button
              type="submit"
              onClick={handleSubmit}
              disabled={!nameIsValid}
            >
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ToDoForm;
