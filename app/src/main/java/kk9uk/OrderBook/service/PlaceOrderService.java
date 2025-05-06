package kk9uk.OrderBook.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import kk9uk.OrderBook.po.OrderPo;
import kk9uk.OrderBook.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PlaceOrderService {

    private final GeoApiContext geoApiContext;
    private final OrderRepository orderRepository;

    public OrderPo placeOrder(double startLat, double startLong, double endLat, double endLong) {

        long distance;
        try {
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(new LatLng(startLat, startLong))
                    .destinations(new LatLng(endLat, endLong))
                    .await();

            if (distanceMatrix.rows[0].elements[0].status != DistanceMatrixElementStatus.OK) {
                throw new RuntimeException("Something wrong with the Google Maps API");
            }
            distance = distanceMatrix.rows[0].elements[0].distance.inMeters;
        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException("Something wrong with the Google Maps API");
        }

        return orderRepository.save(
                OrderPo.builder()
                        .distance(distance)
                        .status("UNASSIGNED")
                        .build()
        );

    }

}
