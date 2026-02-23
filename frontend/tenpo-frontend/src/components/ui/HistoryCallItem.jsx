import styles from "./HistoryCallItem.module.css";

export function HistoryCallItem({ call }) {
  const { id, expression, result, timestamp } = call;

  return (
    <li className={styles.item}>
      <span className={styles.dot} aria-hidden="true" />
      <span className={styles.badge}>#{id}</span>
      <div className={styles.info}>
        <span className={styles.expr}>{expression}</span>
        <span className={styles.time}>{timestamp}</span>
      </div>
      <span className={styles.result}>{result}</span>
    </li>
  );
}
