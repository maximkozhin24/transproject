import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material';
import { SnackbarProvider } from 'notistack';
import Navbar from './components/Layout/Navbar';
import Dashboard from './pages/Dashboard';
import Clients from './pages/Clients';
import Cargos from './pages/Cargos';
import Orders from './pages/Orders';
import RoutesPage from './pages/Routes';  // Переименовали импорт
import Vehicles from './pages/Vehicles';
import { Container } from '@mui/material';

const theme = createTheme({
    palette: {
        primary: {
            main: '#1976d2',
        },
        secondary: {
            main: '#dc004e',
        },
    },
});

function App() {
    return (
        <ThemeProvider theme={theme}>
            <SnackbarProvider maxSnack={3}>
                <Router>
                    <Navbar />
                    <Container maxWidth="xl" sx={{ mt: 2, mb: 4 }}>
                        <Routes>
                            <Route path="/" element={<Dashboard />} />
                            <Route path="/clients" element={<Clients />} />
                            <Route path="/cargos" element={<Cargos />} />
                            <Route path="/orders" element={<Orders />} />
                            <Route path="/routes" element={<RoutesPage />} />  {/* Используем новое имя */}
                            <Route path="/vehicles" element={<Vehicles />} />
                        </Routes>
                    </Container>
                </Router>
            </SnackbarProvider>
        </ThemeProvider>
    );
}

export default App;