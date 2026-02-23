import { createContext, useCallback, useContext, useReducer } from "react";

const initialState = {
  calls: [],
};

const SET_LOADING = "SET_LOADING";
const SET_ERROR = "SET_ERROR";
const SET_CALLS = "SET_CALLS";
const ADD_CALL = "ADD_CALL";
const CLEAR = "CLEAR";

function historyReducer(state, action) {
  switch (action.type) {
    case SET_LOADING:
      return { ...state, loading: action.payload };
    case SET_ERROR:
      return { ...state, error: action.payload };
    case SET_CALLS:
      return { ...state, calls: action.payload };
    case ADD_CALL:
      return { ...state, calls: [...state.calls, action.payload] };
    case CLEAR:
      return { ...state, calls: [] };
    default:
      return state;
  }
}

const HistoryContext = createContext(null);

export function HistoryProvider({ children }) {
  const [state, dispatch] = useReducer(historyReducer, initialState);

  const addCall = useCallback((entry) => {
    dispatch({ type: ADD_CALL, payload: entry });
  }, []);

  const setCalls = useCallback((entries) => {
    dispatch({ type: SET_CALLS, payload: entries });
  }, []);

  const clearHistory = useCallback(() => {
    dispatch({ type: CLEAR });
  }, []);

  return (
    <HistoryContext.Provider value={{ calls: state.calls, addCall, setCalls, clearHistory }}>
      {children}
    </HistoryContext.Provider>
  );
}

export function useHistory() {
  const ctx = useContext(HistoryContext);
  if (!ctx) throw new Error("useHistory must be used within <HistoryProvider>");
  return ctx;
}
