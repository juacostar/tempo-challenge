import { createContext, useCallback, useContext, useReducer } from "react";

const initialState = {
  calls: [],
  page: 1,
  limit: 10,
  total: 0,
  totalPages: 0,
  hasNextPage: false,
  hasPreviousPage: false,
}

const SET_LOADING = "SET_LOADING";
const SET_ERROR = "SET_ERROR";
const SET_CALLS = "SET_CALLS";
const ADD_CALL = "ADD_CALL";
const CLEAR = "CLEAR";
const SET_PAGE = "SET_PAGE";
const SET_LIMIT = "SET_LIMIT";

function paginatedHistoryReducer(state, action) {
  switch (action.type) {
    case SET_LOADING:
      return { ...state, loading: action.payload };
    case SET_ERROR:
      return { ...state, error: action.payload };
    case SET_CALLS:
      return { ...state, calls: action.payload.content };
    case SET_PAGE:
      return { ...state, page: action.payload };
    case SET_LIMIT:
      return { ...state, limit: action.payload };
    case ADD_CALL:
      return { ...state, calls: [...state.calls, action.payload.content] };
    case CLEAR:
      return { ...state, calls: [] };
  }
}

const PaginatedHistoryContext = createContext(null);

export function PaginatedHistoryProvider({ children }) {
  const [state, dispatch] = useReducer(paginatedHistoryReducer, initialState);

  const setPage = useCallback((page) => {
    dispatch({ type: SET_PAGE, payload: page });
  }, []);
  
  const setLimit = useCallback((limit) => {
    dispatch({ type: SET_LIMIT, payload: limit });
  }, []);


  const setCalls = useCallback((entries) => {
    dispatch({ type: SET_CALLS, payload: entries });
  }, []);

  const clearHistory = useCallback(() => {
    dispatch({ type: CLEAR });
  }, []);

  return (
    <PaginatedHistoryContext.Provider value={{ ...state, setCalls, setPage, setLimit, clearHistory }}>
      {children}
    </PaginatedHistoryContext.Provider>
  );
}

export function usePaginatedHistory() {
  const ctx = useContext(PaginatedHistoryContext);
  if (!ctx) throw new Error("usePaginatedHistory must be used within <PaginatedHistoryProvider>");
  return ctx;
}