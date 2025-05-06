package kk9uk.OrderBook.controller;

import kk9uk.OrderBook.dto.PlaceRequestDto;
import kk9uk.OrderBook.po.OrderPo;
import kk9uk.OrderBook.repository.OrderRepository;
import kk9uk.OrderBook.service.PlaceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final PlaceOrderService placeOrderService;
    private final OrderRepository orderRepository;

    @PostMapping("/orders")
    public OrderPo placeOrder(@RequestBody PlaceRequestDto placeRequestDto) {

        if (placeRequestDto.origin().size() != 2 || placeRequestDto.destination().size() != 2) {
            throw new RuntimeException("Coordinates in request must be an array of exactly two strings");
        }

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
            throw new RuntimeException("Invalid latitude or longitude value in coordinates");
        }

        return placeOrderService.placeOrder(startLat, startLong, endLat, endLong);

    }

    @GetMapping("/orders")
    public List<OrderPo> listOrders(@RequestParam int page, @RequestParam int limit) {

        if (page < 1 || limit < 1) {
            throw new RuntimeException("Invalid page or limit value");
        }

        // page is 0-based in Spring
        return orderRepository.findAll(PageRequest.of(page - 1, limit)).getContent();

    }

}
