import React, { useState, useEffect } from 'react';
import {
    Grid, Card, CardContent, Typography, Box, Paper, Table, TableBody,
    TableCell, TableContainer, TableHead, TableRow, Chip
} from '@mui/material';
import { orderApi, clientApi, cargoApi, routeApi, vehicleApi } from '../services/api';

const Dashboard = () => {
    const [stats, setStats] = useState({
        totalOrders: 0,
        totalClients: 0,
        totalCargos: 0,
        totalVehicles: 0,
    });

    const [clients, setClients] = useState([]);
    const [cargos, setCargos] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [recentOrders, setRecentOrders] = useState([]);

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            const [ordersRes, clientsRes, cargosRes, routesRes, vehiclesRes] = await Promise.all([
                orderApi.getAll(0, 100),
                clientApi.getAll(),
                cargoApi.getAll(),
                routeApi.getAll(),
                vehicleApi.getAll()
            ]);

            const rawOrders = ordersRes.data.content || ordersRes.data;

            setClients(clientsRes.data || []);
            setCargos(cargosRes.data || []);
            setRoutes(routesRes.data || []);
            setVehicles(vehiclesRes.data || []);

            setStats({
                totalOrders: ordersRes.data.totalElements || rawOrders.length,
                totalClients: clientsRes.data.length,
                totalCargos: cargosRes.data.length,
                totalVehicles: vehiclesRes.data.length,
            });

            const enrichedOrders = enrichOrders(rawOrders, clientsRes.data, cargosRes.data, routesRes.data, vehiclesRes.data);
            setRecentOrders(enrichedOrders.slice(0, 5));

        } catch (error) {
            console.error('Error loading dashboard:', error);
        }
    };

    const enrichOrders = (ordersList, clientsList, cargosList, routesList, vehiclesList) => {
        return ordersList.map(order => {
            let clientData = order.client;
            if (!clientData && order.clientId) {
                clientData = clientsList.find(c => String(c.id) === String(order.clientId));
            }

            const rawCargos = order.cargos || [];
            const rawRoutes = order.routes || [];
            const rawVehicles = order.vehicles || [];

            const enrichedItems = rawCargos.map((cargo, index) => {
                const route = (rawRoutes[index] && rawRoutes[index] !== null) ? rawRoutes[index] : null;
                const vehicle = (rawVehicles[index] && rawVehicles[index] !== null) ? rawVehicles[index] : null;

                return {
                    cargo: { id: cargo.id, name: cargo.name, weight: cargo.weight },
                    route: route,
                    vehicle: vehicle
                };
            });

            return {
                ...order,
                client: clientData,
                routeVehicleCargoList: enrichedItems
            };
        });
    };

    const getOrderItems = (order) => order.routeVehicleCargoList || [];

    const getTotalWeight = (order) => {
        const items = order.routeVehicleCargoList || [];
        return items.reduce((sum, item) => sum + (item.cargo?.weight || 0), 0);
    };

    const getAssignedVehicle = (order) => {
        const items = order.routeVehicleCargoList || [];
        if (items.length === 0) return 'Not assigned';
        const vehicle = items[0]?.vehicle;
        return vehicle && vehicle.plateNumber ? `${vehicle.plateNumber} (${vehicle.model || ''})` : 'Not assigned';
    };

    return (
        <Box className="fade-in" sx={{ bgcolor: '#f5f5f5', minHeight: '100vh', p: { xs: 1, sm: 2, md: 3 } }}>
            {/* Заголовок */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3, flexWrap: 'wrap', gap: 2 }}>
                <Typography variant="h4" sx={{ color: '#000000', fontWeight: 600 }}>
                    Dashboard
                </Typography>
            </Box>

            {/* Таблица Recent Orders */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Client</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Price</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Items</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Weight</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Vehicle</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {recentOrders.length > 0 ? recentOrders.map((order) => (
                            <TableRow
                                key={order.id}
                                sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                            >
                                <TableCell sx={{ fontWeight: 500 }}>
                                    {order.client?.name || 'Unknown'}
                                </TableCell>
                                <TableCell>${order.price}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={order.status}
                                        color={order.status === 'COMPLETED' ? 'success' : order.status === 'IN_PROGRESS' ? 'warning' : 'info'}
                                        size="small"
                                        sx={{ fontWeight: 500 }}
                                    />
                                </TableCell>
                                <TableCell>
                                    {getOrderItems(order).map((item, idx) => (
                                        <Typography key={idx} variant="body2" sx={{ mb: 0.25 }}>
                                            • {item.cargo?.name || 'Item'}
                                        </Typography>
                                    ))}
                                    {getOrderItems(order).length === 0 && (
                                        <Typography variant="body2" color="textSecondary">No items</Typography>
                                    )}
                                </TableCell>
                                <TableCell>{getTotalWeight(order)} kg</TableCell>
                                <TableCell>
                                    <Typography variant="body2" color="textSecondary">
                                        {getAssignedVehicle(order)}
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        )) : (
                            <TableRow>
                                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No recent orders found
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default Dashboard;