import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "./context/RouterContext";
import { HistoryProvider } from "./context/HistoryContext";
import { PaginatedHistoryProvider } from "./context/PaginatedHistoryContext";
import App from "./App";
import "./styles/global.css";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <RouterProvider>
      <HistoryProvider>
        <PaginatedHistoryProvider>
        <App />
        </PaginatedHistoryProvider>
      </HistoryProvider>
    </RouterProvider>
  </StrictMode>
);
