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

    // Сохраняем справочники для отображения имен вместо ID
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
            // Загружаем всё параллельно
            const [ordersRes, clientsRes, cargosRes, routesRes, vehiclesRes] = await Promise.all([
                orderApi.getAll(0, 100), // Берем последние 100 заказов
                clientApi.getAll(),
                cargoApi.getAll(),
                routeApi.getAll(),
                vehicleApi.getAll()
            ]);

            const rawOrders = ordersRes.data.content || ordersRes.data;

            // Сохраняем справочники
            setClients(clientsRes.data || []);
            setCargos(cargosRes.data || []);
            setRoutes(routesRes.data || []);
            setVehicles(vehiclesRes.data || []);

            // Обновляем статистику
            setStats({
                totalOrders: ordersRes.data.totalElements || rawOrders.length,
                totalClients: clientsRes.data.length,
                totalCargos: cargosRes.data.length,
                totalVehicles: vehiclesRes.data.length,
            });

            // Обогащаем заказы (преобразуем плоский JSON в удобный вид)
            const enrichedOrders = enrichOrders(rawOrders, clientsRes.data, cargosRes.data, routesRes.data, vehiclesRes.data);

            // Берем только последние 5 для таблицы
            setRecentOrders(enrichedOrders.slice(0, 5));

        } catch (error) {
            console.error('Error loading dashboard:', error);
        }
    };

    // Функция трансформации данных (аналогично Orders.js)
    const enrichOrders = (ordersList, clientsList, cargosList, routesList, vehiclesList) => {
        return ordersList.map(order => {
            // 1. Клиент
            let clientData = order.client;
            if (!clientData && order.clientId) {
                clientData = clientsList.find(c => String(c.id) === String(order.clientId));
            }

            // 2. Сборка Items из массивов cargos, routes, vehicles
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

    // Хелперы для отображения
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
        <Box>
            <Typography variant="h4" gutterBottom>
                Dashboard
            </Typography>

            {/* Карточки статистики */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                {Object.entries(stats).map(([key, value]) => (
                    <Grid item xs={12} sm={6} md={3} key={key}>
                        <Card>
                            <CardContent>
                                <Typography color="textSecondary" gutterBottom>
                                    {key.replace('total', 'Total ').replace(/([A-Z])/g, ' $1').trim()}
                                </Typography>
                                <Typography variant="h4">
                                    {value}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            {/* Таблица Recent Orders (Теперь на всю ширину и с полной инфо) */}
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Paper sx={{ p: 2 }}>
                        <Typography variant="h6" gutterBottom>
                            Recent Orders
                        </Typography>
                        <TableContainer>
                            <Table size="small">
                                <TableHead>
                                    <TableRow>
                                        {/* ID убран */}
                                        <TableCell>Client</TableCell>
                                        <TableCell>Price</TableCell>
                                        <TableCell>Status</TableCell>
                                        <TableCell>Items</TableCell>
                                        <TableCell>Weight</TableCell>
                                        <TableCell>Vehicle</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {recentOrders.length > 0 ? recentOrders.map((order) => (
                                        <TableRow key={order.id}>
                                            <TableCell>
                                                {/* Отображаем имя клиента */}
                                                {order.client?.name || 'Unknown'}
                                            </TableCell>
                                            <TableCell>${order.price}</TableCell>
                                            <TableCell>
                                                <Chip
                                                    label={order.status}
                                                    color={order.status === 'COMPLETED' ? 'success' : order.status === 'IN_PROGRESS' ? 'warning' : 'info'}
                                                    size="small"
                                                />
                                            </TableCell>
                                            <TableCell>
                                                {/* Список товаров */}
                                                {getOrderItems(order).map((item, idx) => (
                                                    <div key={idx} style={{ fontSize: '0.85rem' }}>
                                                        • {item.cargo?.name || 'Item'}
                                                    </div>
                                                ))}
                                                {getOrderItems(order).length === 0 && 'No items'}
                                            </TableCell>
                                            <TableCell>{getTotalWeight(order)} kg</TableCell>
                                            <TableCell>
                                                {/* Привязанная машина */}
                                                <Typography variant="body2" color="textSecondary">
                                                    {getAssignedVehicle(order)}
                                                </Typography>
                                            </TableCell>
                                        </TableRow>
                                    )) : (
                                        <TableRow>
                                            <TableCell colSpan={6} align="center">No recent orders found</TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard;