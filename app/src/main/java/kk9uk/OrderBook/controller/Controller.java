package kk9uk.OrderBook.controller;

import kk9uk.OrderBook.dto.PlaceRequestDto;
import kk9uk.OrderBook.po.OrderPo;
import kk9uk.OrderBook.service.PlaceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final PlaceOrderService placeOrderService;

    @PostMapping("/orders")
    public OrderPo placeOrder(@RequestBody PlaceRequestDto placeRequestDto) {

        // Coordinates in request must be an array of exactly two strings
        if (placeRequestDto.origin().size() != 2 || placeRequestDto.destination().size() != 2) {
            throw new RuntimeException("Coordinates in request must be an array of exactly two strings");
        }

        // The latitude and longitude value of coordinates must be correctly validated
        double startLat = Double.parseDouble(placeRequestDto.origin().get(0).trim()),
               startLong = Double.parseDouble(placeRequestDto.origin().get(1).trim()),
               endLat = Double.parseDouble(placeRequestDto.destination().get(0).trim()),
               endLong = Double.parseDouble(placeRequestDto.destination().get(1).trim());
        if (
                (-90 > startLat || startLat > 90) ||
                (-90 > endLat || endLat > 90) ||
                (-180 > startLong || startLong > 180) ||
                (-180 > endLong || endLong > 180)
        ) {
            throw new RuntimeException("Invalid latitude and longitude value of coordinates");
        }

        return placeOrderService.placeOrder(startLat, startLong, endLat, endLong);

    }

}
