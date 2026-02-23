import { ROUTES, useRouter } from "../../context/RouterContext";
import styles from "./Navbar.module.css";

const NAV_ITEMS = [
  { route: ROUTES.HOME,       label: "Inicio" },
  { route: ROUTES.CALCULATOR, label: "Calculadora" },
  { route: ROUTES.HISTORY,    label: "Historial" },
];

export function Navbar() {
  const { currentRoute, navigate } = useRouter();

  return (
    <nav className={styles.nav}>
      <span className={styles.logo}>CALCLAB</span>
      <ul className={styles.links} role="list">
        {NAV_ITEMS.map(({ route, label }) => (
          <li key={route}>
            <button
              className={`${styles.link} ${currentRoute === route ? styles.active : ""}`}
              onClick={() => navigate(route)}
              aria-current={currentRoute === route ? "page" : undefined}
            >
              {label}
            </button>
          </li>
        ))}
      </ul>
    </nav>
  );
}
