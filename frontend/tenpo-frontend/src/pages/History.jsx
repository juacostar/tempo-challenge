import { Button } from "../components/ui/Button";
import { HistoryCallItem } from "../components/ui/HistoryCallItem";
import { useHistoryApi } from "../hooks/useHistoryApi";
import styles from "./History.module.css";

function EmptyState() {
  return (
    <div className={styles.empty} role="status">
      <span className={styles.emptyIcon}>◈</span>
      <p>Sin registros aún</p>
      <p className={styles.emptyHint}>Realiza un cálculo para ver el historial</p>
    </div>
  );
}

function LoadingState() {
  return (
    <div className={styles.empty} role="status" aria-live="polite">
      <span className={styles.emptyIcon} style={{ opacity: 1 }}>⟳</span>
      <p>Cargando historial…</p>
    </div>
  );
}

function ErrorState({ message }) {
  return (
    <div className={styles.errorState} role="alert">
      <p>⚠ {message}</p>
    </div>
  );
}

export default function History() {
  const { calls, loading, error, handleClearAll } = useHistoryApi();

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <h2 className={styles.title}>Historial de API</h2>
          <p className={styles.subtitle}>
            {loading ? "Sincronizando…" : `${calls.length} llamadas registradas`}
          </p>
        </div>
        {calls.length > 0 && !loading && (
          <Button variant="danger" onClick={handleClearAll}>
            Limpiar
          </Button>
        )}
      </header>

      {error && <ErrorState message={error} />}

      {loading && !error ? (
        <LoadingState />
      ) : !error && calls.length === 0 ? (
        <EmptyState />
      ) : (
        <ol className={styles.list} aria-label="Historial de llamadas">
          {calls.map((call) => (
            <HistoryCallItem key={call.id} call={call} />
          ))}
        </ol>
      )}
    </main>
  );
}
