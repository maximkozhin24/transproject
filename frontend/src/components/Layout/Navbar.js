import React from 'react';
import { AppBar, Toolbar, Typography, Button, Container, Box } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import PeopleIcon from '@mui/icons-material/People';
import InventoryIcon from '@mui/icons-material/Inventory';
import MapIcon from '@mui/icons-material/Map';
import RouteIcon from '@mui/icons-material/Route';

const Navbar = () => {
    const location = useLocation();

    const menuItems = [
        { path: '/', label: 'Dashboard', icon: <LocalShippingIcon /> },
        { path: '/clients', label: 'Clients', icon: <PeopleIcon /> },
        { path: '/cargos', label: 'Cargos', icon: <InventoryIcon /> },
        { path: '/orders', label: 'Orders', icon: <LocalShippingIcon /> },
        { path: '/routes', label: 'Routes', icon: <RouteIcon /> },
        { path: '/vehicles', label: 'Vehicles', icon: <MapIcon /> },
    ];

    return (
        <AppBar position="static" sx={{ mb: 3 }}>
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                    Management System
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    {menuItems.map((item) => (
                        <Button
                            key={item.path}
                            component={Link}
                            to={item.path}
                            color="inherit"
                            startIcon={item.icon}
                            sx={{
                                backgroundColor: location.pathname === item.path ? 'rgba(255,255,255,0.1)' : 'transparent'
                            }}
                        >
                            {item.label}
                        </Button>
                    ))}
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;