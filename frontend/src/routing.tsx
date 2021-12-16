import { BrowserRouter, Route, Routes } from "react-router-dom";
import { NavBar } from "./components/navbar";
import { Login } from "./pages/login";
import { Main } from "./pages/main";
import { Signup } from "./pages/signup";
import { Logout } from "./pages/Logout";
import { Trips } from "./pages/trips";
import { CreateTrip } from "./pages/createTrip";

export const Routing = () => {
  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="login" element={<Login />} />
        <Route path="trips" element={<Trips />} />
        <Route path="create" element={<CreateTrip />} />
        <Route path="signup" element={<Signup />} />
        <Route path="logout" element={<Logout />} />
      </Routes>
    </BrowserRouter>
  );
};
