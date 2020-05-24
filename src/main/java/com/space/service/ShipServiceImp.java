package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipServiceImp implements ShipService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImp(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public Ship createShip(Ship ship) {
        if (ship.getName() == null ||
                ship.getName().isEmpty() ||
                ship.getName().length() > 50 ||
                ship.getPlanet() == null ||
                ship.getPlanet().isEmpty() ||
                ship.getPlanet().length() > 50 ||
                ship.getShipType() == null ||
                ship.getProdDate() == null ||
                (ship.getProdDate().getYear() + 1900) < 2800 ||
                (ship.getProdDate().getYear() + 1900) > 3019 ||
                ship.getSpeed() == null ||
                ship.getSpeed() < 0.01d ||
                ship.getSpeed() > 0.99d ||
                ship.getCrewSize() == null ||
                ship.getCrewSize() < 1 ||
                ship.getCrewSize() > 9999) {
            throw new BadRequestException();
        } else if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        ship.setSpeed((double) Math.round(ship.getSpeed() * 100) / 100);
        ship.setRating(getRating(ship));
        return shipRepository.save(ship);
    }

    public Ship updateShip(Ship newShip, Long id) {
        Ship shipUpdate = getShipById(id);

        if (newShip == null || shipUpdate == null) {
            throw new BadRequestException();
        }
        if (newShip.getName() != null) {
            if (newShip.getName().length() > 50 ||
                    newShip.getName().isEmpty()) {
                throw new BadRequestException();
            }
            shipUpdate.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().length() > 50 ||
                    newShip.getPlanet().isEmpty()) {
                throw new BadRequestException();
            }
            shipUpdate.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            shipUpdate.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            if ((newShip.getProdDate().getYear() + 1900) < 2800 ||
                    (newShip.getProdDate().getYear() + 1900) > 3019) {
                throw new BadRequestException();
            }
            shipUpdate.setProdDate(newShip.getProdDate());
        }
        if (newShip.getUsed() != null) {
            shipUpdate.setUsed(newShip.getUsed());
        }
        if (newShip.getSpeed() != null) {
            if (newShip.getSpeed() < 0.01d ||
                    newShip.getSpeed() > 0.99d) {
                throw new BadRequestException();
            }
            shipUpdate.setSpeed(newShip.getSpeed());
        }
        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 1 ||
                    newShip.getCrewSize() > 9999) {
                throw new BadRequestException();
            }
            shipUpdate.setCrewSize(newShip.getCrewSize());
        }

        shipUpdate.setRating(getRating(shipUpdate));
        return shipRepository.save(shipUpdate);
    }

    public void delete(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }

        shipRepository.deleteById(id);
    }

    public Ship getShipById(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean getUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> filteredShips = shipRepository.findAll();

        filteredShips = filteredShips.stream()
                .filter(ship -> name != null ? ship.getName().contains(name) : true)
                .filter(ship -> planet != null ? ship.getPlanet().contains(planet) : true)
                .filter(ship -> shipType != null ? ship.getShipType().equals(shipType) : true)
                .filter(ship -> after != null ? ship.getProdDate().after(new Date(after)) : true)
                .filter(ship -> before != null ? ship.getProdDate().before(new Date(before)) : true)
                .filter(ship -> getUsed != null ? ship.getUsed().equals(getUsed) : true)
                .filter(ship -> minSpeed != null ? Double.compare(ship.getSpeed(), minSpeed) >= 0 : true)
                .filter(ship -> maxSpeed != null ? Double.compare(ship.getSpeed(), maxSpeed) <= 0 : true)
                .filter(ship -> minCrewSize != null ? ship.getCrewSize() >= minCrewSize : true)
                .filter(ship -> maxCrewSize != null ? ship.getCrewSize() <= maxCrewSize : true)
                .filter(ship -> minRating != null ? Double.compare(ship.getRating(), minRating) >= 0 : true)
                .filter(ship -> maxRating != null ? Double.compare(ship.getRating(), maxRating) <= 0 : true)
                .collect(Collectors.toList());

        return filteredShips;
    }

    public List<Ship> filteredShips(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;

        if (order == null) {
            order = ShipOrder.ID;
        }
        Comparator<Ship> comparator = order == ShipOrder.RATING ?
                Comparator.comparingDouble(o -> o.getRating()) :
                order == ShipOrder.DATE ?
                        Comparator.comparing(o -> o.getProdDate()) :
                        order == ShipOrder.SPEED ?
                                Comparator.comparingDouble(o -> o.getSpeed()) :
                                Comparator.comparing(o -> o.getId());

        List<Ship> filteredShips = ships.stream()
                .sorted(comparator)
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        return filteredShips;
    }


    private Double getRating(Ship ship) {
        double v = ship.getSpeed();
        double k = ship.getUsed() ? 0.5 : 1.0;
        int y0 = 3019;
        int y1 = ship.getProdDate().getYear() + 1900;
        double rating = (80 * v * k) / (y0 - y1 + 1);
        return (double) Math.round(rating * 100) / 100;
    }
}