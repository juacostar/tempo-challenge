import { useState, useEffect, useCallback } from "react";
import { usePaginatedHistory } from "../context/PaginatedHistoryContext";
import { getPaginatedHistory } from "../services/paginatedHistoryService";


export function usePaginatedHistoryApi() {
    
    const { 
        calls, 
        setCalls, 
        clearHistory,
        page,
        limit,
        setPage,
        setLimit
    } = usePaginatedHistory();

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        let cancelled = false;
        async function load() {
            setLoading(true);
            setError("");
            try {
                const data = await getPaginatedHistory(page, limit);
                setPage(page);
                setLimit(limit);
                if (!cancelled) setCalls(data);
            } catch (err) {
                if (!cancelled) setError(err.message ?? "Error al cargar el historial");
            } finally {
                if (!cancelled) setLoading(false);
            }
        }

        load();

        return () => { cancelled = true; };
    }, [page, limit]);

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
    
      return { 
        calls, 
        loading, 
        error, 
        setPage, 
        setLimit, 
        page, 
        limit 
    };
}