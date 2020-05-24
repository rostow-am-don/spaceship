package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    public Ship createShip(Ship ship);

    public Ship updateShip(Ship newShip, Long id);

    public void delete(Long id);

    public Ship getShipById(Long id);

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean getUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating);

    public List<Ship> filteredShips(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize);
}
