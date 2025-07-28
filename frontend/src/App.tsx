import { HttpStatusCode } from "axios";
import { useEffect, useState } from "react";
import "./App.css";
import FilterControls from "./components/FilterControls/FilterControls";
import Paginator from "./components/Paginator/Paginator";
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
  PaginationData,
  SortingOrder,
  Status,
  ToDo,
  ToDoCreationData,
} from "./shared/types";
import { anyFiltersApplied, isToDo } from "./shared/validators";

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

  // Pagination data
  const [paginationData, setPaginationData] = useState<PaginationData>({
    currentPage: 0,
    size: 10,
    totalPages: 1,
    first: true,
    last: false,
  });

  const [currPage, setCurrPage] = useState(0);

  // Sorting criteria
  const [sortingCriteria, setSortingCriteria] = useState<
    Map<string, SortingOrder>
  >(new Map<string, SortingOrder>());

  useEffect(() => {
    // if there are any filters applied and we're not in the first page of the list, first reset pagination
    if (anyFiltersApplied(filters) && currPage !== 0) {
      setCurrPage(0);
    } else {
      fetchToDos();
    }
  }, [filters, currPage]); // Re-runs whenever filters or current page change

  // Fetch all  To Dos
  const fetchToDos = () => {
    const sortBy = Array.from(sortingCriteria.keys()).join(",");
    const order = Array.from(sortingCriteria.values()).join(",");

    getAllToDos(filters, currPage, paginationData.size, sortBy, order)
      .then((response) => {
        setPaginationData({
          currentPage: response?.data.number,
          size: response?.data.size,
          totalPages: response?.data.totalPages,
          first: response?.data.first,
          last: response?.data.last,
        });

        setToDoList(response?.data.content);
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
      // if delete is successful, refetch ToDos considering pagination
      if (response!!.status === HttpStatusCode.Ok) {
        // if we deleted the last ToDo of the last page, go to previous page
        if (
          toDoList.length === 1 &&
          paginationData.last &&
          !paginationData.first
        ) {
          setCurrPage(currPage - 1);
        } else {
          fetchToDos(); // otherwise, fetch ToDos normally
        }
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

  // handles pagination
  const handlePagination = (targetPage: number) => {
    setCurrPage(targetPage);
  };

  // handle sorting
  const handleSortingChange = (
    newSortingCriteria: Map<string, SortingOrder>
  ) => {
    // update sorting criteria
    setSortingCriteria(newSortingCriteria);

    // refetch To Do list
    fetchToDos();
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
          currSorting={sortingCriteria}
          onEditClick={handleEditToDoClick}
          onDeleteClick={handleDeleteToDoClick}
          onDoneStatusChange={handleDoneStatusChange}
          onSortingChange={handleSortingChange}
        />
        <Paginator
          pagination={paginationData}
          onPageChange={handlePagination}
        />
      </main>
    </>
  );
}

export default App;
