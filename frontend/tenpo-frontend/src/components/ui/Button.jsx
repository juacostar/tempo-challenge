import styles from "./Button.module.css";

/**
 * @param {'primary' | 'ghost' | 'danger'} [variant='primary']
 */
export function Button({ children, variant = "primary", className = "", ...props }) {
  return (
    <button
      className={`${styles.btn} ${styles[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
