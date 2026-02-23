import styles from "./ResultDisplay.module.css";

export function ResultDisplay({ a, b, op, result, error }) {
  return (
    <div className={styles.wrapper}>
      {error ? (
        <p className={styles.error} role="alert">⚠ {error}</p>
      ) : (
        <div className={styles.box}>
          <div>
            <span className={styles.label}>Resultado</span>
            <span className={styles.expr}>{a || "A"} {op} {b || "B"}</span>
          </div>
          <span className={styles.value}>{result !== null ? result : "—"}</span>
        </div>
      )}
    </div>
  );
}
