import { useState, useEffect, useCallback } from "react";
import { useHistory } from "../context/HistoryContext";
import { getHistory } from "../services/historyService";

export function useHistoryApi() {
    const { calls, setCalls, clearHistory } = useHistory();
  
    const [loading, setLoading] = useState(false);
    const [error, setError]     = useState("");
  
    // Fetch on mount — hydrate the context with server data
    useEffect(() => {
      let cancelled = false; // cleanup flag to avoid state updates on unmounted component
  
      async function load() {
        setLoading(true);
        setError("");
  
        try {
          const data = await getHistory();
          if (!cancelled) setCalls(data);
        } catch (err) {
          if (!cancelled) setError(err.message ?? "Error al cargar el historial");
        } finally {
          if (!cancelled) setLoading(false);
        }
      }
  
      load();
  
      return () => { cancelled = true; };
    }, []); // eslint-disable-line react-hooks/exhaustive-deps
  
    const handleClearAll = useCallback(async () => {
      setError("");
      setLoading(true);
  
      try {
        await clearAllHistory();
        clearHistory(); // wipe context after server confirms
      } catch (err) {
        setError(err.message ?? "Error al limpiar el historial");
      } finally {
        setLoading(false);
      }
    }, [clearHistory]);
  
    return { calls, loading, error, handleClearAll };
  }