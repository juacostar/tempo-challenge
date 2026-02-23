import styles from "./NumberInput.module.css";

/**
 * Controlled numeric input.
 * @param {string} label  - Accessible label (visually hidden)
 * @param {string} placeholder
 */
export function NumberInput({ label, value, onChange, placeholder, id }) {
  return (
    <div className={styles.wrapper}>
      <label htmlFor={id} className={styles.label}>{label}</label>
      <input
        id={id}
        className={styles.input}
        type="number"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        inputMode="decimal"
      />
    </div>
  );
}
