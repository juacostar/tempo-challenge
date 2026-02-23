import { ROUTES, useRouter } from "../context/RouterContext";
import styles from "./Home.module.css";

const CARDS = [
  {
    route: ROUTES.CALCULATOR,
    icon: "⟨/⟩",
    title: "Calculadora",
    description: "Operadoras de dos números",
  },
  {
    route: ROUTES.HISTORY,
    icon: "◈",
    title: "Historial de API",
    description: "Revisa todas las llamadas de cálculo registradas en tiempo real.",
  },
];

export default function Home() {
  const { navigate } = useRouter();

  return (
    <main className={styles.page}>
      <div className={styles.grid} aria-hidden="true" />

      <div className={styles.content}>
        <h1 className={styles.title}>Tenpo</h1>
        <p className={styles.subtitle}>Ccomponente frontend para el backend de Tenpo</p>

        <div className={styles.cards}>
          {CARDS.map(({ route, icon, title, description }) => (
            <button
              key={route}
              className={styles.card}
              onClick={() => navigate(route)}
            >
              <span className={styles.cardIcon} aria-hidden="true">{icon}</span>
              <span className={styles.cardTitle}>{title}</span>
              <span className={styles.cardDesc}>{description}</span>
            </button>
          ))}
        </div>
      </div>
    </main>
  );
}
