package com.space.controller;

import com.space.exception.BadRequestException;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
// @Qualifier(value = "ShipServiceImp")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.POST)
    @ResponseBody
    public Ship createShip(@RequestBody Ship ship) {
        Ship createShip = shipService.createShip(ship);
        if (createShip == null) {
            throw new BadRequestException();
        }
        return createShip;
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Ship updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException();
        }

        return shipService.updateShip(ship, id);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public void deleteShip(@PathVariable Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException();
        }
        shipService.delete(id);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET)
    public Ship getShipById(@PathVariable Long id) {
        if (id == null || id < 1) {
            throw new BadRequestException();
        }

        return shipService.getShipById(id);
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getShipsList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String planet,
                                   @RequestParam(required = false) ShipType shipType,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean isUsed,
                                   @RequestParam(required = false) Double minSpeed,
                                   @RequestParam(required = false) Double maxSpeed,
                                   @RequestParam(required = false) Integer minCrewSize,
                                   @RequestParam(required = false) Integer maxCrewSize,
                                   @RequestParam(required = false) Double minRating,
                                   @RequestParam(required = false) Double maxRating,
                                   @RequestParam(required = false) ShipOrder order,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize) {
        List<Ship> filteredShips = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.filteredShips(filteredShips, order, pageNumber, pageSize);
    }

    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET)
    public int getShipsCount(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String planet,
                             @RequestParam(required = false) ShipType shipType,
                             @RequestParam(required = false) Long after,
                             @RequestParam(required = false) Long before,
                             @RequestParam(required = false) Boolean isUsed,
                             @RequestParam(required = false) Double minSpeed,
                             @RequestParam(required = false) Double maxSpeed,
                             @RequestParam(required = false) Integer minCrewSize,
                             @RequestParam(required = false) Integer maxCrewSize,
                             @RequestParam(required = false) Double minRating,
                             @RequestParam(required = false) Double maxRating) {
        return shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating).size();
    }
}