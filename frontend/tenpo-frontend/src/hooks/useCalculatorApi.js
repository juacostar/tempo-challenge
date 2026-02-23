import { useState, useCallback } from "react";
import { useHistory } from "../context/HistoryContext";
import { postCalculation } from "../services/calculatorService";


export function useCalculator() {
    const { addCall } = useHistory();
  
    const [a, setA]             = useState("");
    const [b, setB]             = useState("");
    const [result, setResult]   = useState(null);
    const [error, setError]     = useState("");
    const [loading, setLoading] = useState(false);
  
    const calculate = useCallback(async () => {
      // Basic client-side guard before hitting the network
      if (a === "" || b === "") {
        setError("Ingresa los dos operandos");
        return;
      }
  
      setError("");
      setResult(null);
      setLoading(true);
  
      try {
        // The API returns the full entry: { id, expression, result, timestamp }
        const entry = await postCalculation({ a, b });

        setResult(entry.result);
        addCall(entry);
      } catch (err) {
        setError(err.message ?? "Error al conectar con el servidor");
      } finally {
        setLoading(false);
      }
    }, [a, b, addCall]);
  
    const reset = useCallback(() => {
      setA("");
      setB("");
      setResult(null);
      setError("");
    }, []);
  
    return { a, setA, b, setB, result, error, loading, calculate, reset };
  }
  