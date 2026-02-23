import React, { createContext, useCallback, useContext, useState } from "react";

export const ROUTES = {
  HOME:       "home",
  CALCULATOR: "calculator",
  HISTORY:    "history",
} as const;

type RouterContextValue = {
  currentRoute: string;
  navigate: (route: string) => void;
};

const RouterContext = createContext<RouterContextValue | null>(null);

export function RouterProvider({ children }) {
  const [currentRoute, setCurrentRoute] = useState(ROUTES.HOME);

  const navigate = useCallback((route) => {
    setCurrentRoute(route);
  }, []);

  return (
    <RouterContext.Provider value={{ currentRoute, navigate }}>
      {children}
    </RouterContext.Provider>
  );
}

export function useRouter() {
  const ctx = useContext(RouterContext);
  if (!ctx) throw new Error("useRouter must be used within <RouterProvider>");
  return ctx;
}
