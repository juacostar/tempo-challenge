import { ROUTES, useRouter } from "./context/RouterContext";
import { Navbar } from "./components/layout/Navbar";
import Home       from "./pages/Home";
import Calculator from "./pages/Calculator";
import History    from "./pages/History";
import PaginatedHistory from "./pages/PaginatedHistory";

const PAGE_MAP = {
  [ROUTES.HOME]:       Home,
  [ROUTES.CALCULATOR]: Calculator,
  [ROUTES.HISTORY]:    History,
  [ROUTES.PAGINATED_HISTORY]: PaginatedHistory
};

export default function App() {
  const { currentRoute } = useRouter();
  const Page = PAGE_MAP[currentRoute] ?? Home;

  return (
    <>
      <Navbar />
      <Page />
    </>
  );
}
