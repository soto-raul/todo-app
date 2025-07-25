import { HttpStatusCode } from "axios";
import { useEffect, useState } from "react";
import "./App.css";
import FilterControls from "./components/FilterControls/FilterControls";
import ToDoList from "./components/ToDoList/ToDoList";
import ToDoModalForm from "./components/ToDoModalForm/ToDoModalForm";
import {
  addToDo,
  deleteToDo,
  getAllToDos,
  markAsDone,
  markAsUndone,
  updateToDo,
} from "./services/ToDoService";
import type {
  FilterCriteria,
  Status,
  ToDo,
  ToDoCreationData,
} from "./shared/types";
import { isToDo } from "./shared/validators";

function App() {
  // ToDo tasks list
  const [toDoList, setToDoList] = useState<ToDo[]>([]);

  // FilterCriteria
  const [filters, setFilters] = useState<FilterCriteria>({
    name: null,
    priority: null,
    doneStatus: null,
  });

  // Modal Form
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [initialFormData, setInitialFormData] = useState<ToDo | null>(null);

  useEffect(() => {
    fetchToDos();
  }, [filters]); // Re-runs whenever filters change

  // Fetch all  To Dos
  const fetchToDos = () => {
    getAllToDos(filters)
      .then((response) => {
        setToDoList(response?.data);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  // handles form submission when adding a new To Do or editing an existing one
  const handleFormSubmission = (formData: ToDoCreationData | ToDo) => {
    // close form modal
    handleCloseFormModal();
    // If a ToDo obj is passed as formData, we are editing an exiting ToDo
    // Otherwise, we are trying to add a new ToDo
    const promise = isToDo(formData)
      ? updateToDo(formData.id, formData)
      : addToDo(formData);

    // handle request response
    promise.then((response) => {
      if (response?.status === HttpStatusCode.Ok) {
        fetchToDos();
      } else {
        console.error(response?.data);
      }
    });
  };

  // handles done status changes
  const handleDoneStatusChange = (id: number, newStatus: Status) => {
    const promise = newStatus === "DONE" ? markAsDone(id) : markAsUndone(id);

    promise
      .then((response) => {
        if (response?.status === HttpStatusCode.Ok) {
          fetchToDos();
        }
      })
      .catch((error) => {
        console.error(error);
      });
  };

  // handles click on edit button
  const handleEditToDoClick = (toDo: ToDo) => {
    // show modal form populated with To Do data
    handleOpenFormModal(toDo);
  };

  // Handles the deletion of a To Do given its ID
  const handleDeleteToDoClick = (id: number) => {
    deleteToDo(id).then((response) => {
      // if delete is successful, refetch To Dos
      if (response!!.status === HttpStatusCode.Ok) {
        fetchToDos();
      } else {
        console.log(response!!.data);
      }
    });
  };

  // handles opening form modal and populating it with data if necessary
  const handleOpenFormModal = (initData: ToDo | null) => {
    setInitialFormData(initData);
    setIsModalOpen(true);
  };

  // handle closing the form modal
  const handleCloseFormModal = () => {
    // close modal and reset initialFormData
    setIsModalOpen(false);
    setInitialFormData(null);
  };

  return (
    <>
      <main>
        <h1>ToDo App</h1>
        <FilterControls setFilters={setFilters} />
        <section>
          <button
            onClick={() => {
              handleOpenFormModal(null);
            }}
          >
            + New Task
          </button>
          <ToDoModalForm
            isOpen={isModalOpen}
            initialData={initialFormData}
            onCancel={handleCloseFormModal}
            onSave={handleFormSubmission}
          />
        </section>
        <ToDoList
          toDos={toDoList}
          onEditClick={handleEditToDoClick}
          onDeleteClick={handleDeleteToDoClick}
          onDoneStatusChange={handleDoneStatusChange}
        />
      </main>
    </>
  );
}

export default App;
