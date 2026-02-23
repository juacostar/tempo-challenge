import { Button } from "../components/ui/Button";
import { NumberInput } from "../components/ui/NumberInput";
import { ResultDisplay } from "../components/ui/ResultDisplay";
import { useCalculator } from "../hooks/useCalculatorApi";
import styles from "./Calculator.module.css";

export default function Calculator() {
  const { a, setA, b, setB, result, error, loading, calculate, reset } =
    useCalculator();

  function handleKeyDown(e) {
    // Avoid triggering while a request is already in flight
    if (e.key === "Enter" && !loading) calculate();
  }

  return (
    <main className={styles.page}>
      <div className={styles.card}>
        <header className={styles.header}>
          <h2 className={styles.title}>Calculadora</h2>
          <p className={styles.subtitle}>Solo dos operandos</p>
        </header>

        <div className={styles.inputs} onKeyDown={handleKeyDown}>
          <NumberInput
            id="operand-a"
            label="Operando A"
            value={a}
            onChange={setA}
            placeholder="A"
            disabled={loading}
          />
          <NumberInput
            id="operand-b"
            label="Operando B"
            value={b}
            onChange={setB}
            placeholder="B"
            disabled={loading}
          />
        </div>

        <div className={styles.actions}>
          <Button onClick={calculate} disabled={loading} style={{ flex: 1 }}>
            {loading ? "Calculando…" : "Calcular"}
          </Button>
          <Button variant="ghost" onClick={reset} disabled={loading} aria-label="Resetear">
            ↺
          </Button>
        </div>

        <ResultDisplay a={a} b={b} result={result} error={error} />
      </div>
    </main>
  );
}
