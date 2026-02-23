import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "./context/RouterContext";
import { HistoryProvider } from "./context/HistoryContext";
import App from "./App";
import "./styles/global.css";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <RouterProvider>
      <HistoryProvider>
        <App />
      </HistoryProvider>
    </RouterProvider>
  </StrictMode>
);
