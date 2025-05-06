package kk9uk.OrderBook.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import kk9uk.OrderBook.po.OrderPo;
import kk9uk.OrderBook.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    @Mock
    private GeoApiContext geoApiContext;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Test
    void placeOrder_Success() throws Exception {
        try (MockedStatic<DistanceMatrixApi> mockedDistanceMatrixApi = mockStatic(DistanceMatrixApi.class)) {
            // Mock DistanceMatrixApiRequest chain
            DistanceMatrixApiRequest requestMock = mock(DistanceMatrixApiRequest.class);
            mockedDistanceMatrixApi.when(() -> DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(requestMock);
            when(requestMock.origins(any(LatLng.class))).thenReturn(requestMock);
            when(requestMock.destinations(any(LatLng.class))).thenReturn(requestMock);

            // Setup successful DistanceMatrix response
            DistanceMatrixRow row = new DistanceMatrixRow();
            DistanceMatrixElement element = new DistanceMatrixElement();
            element.status = DistanceMatrixElementStatus.OK;
            element.distance = new Distance();
            element.distance.inMeters = 5000L;
            row.elements = new DistanceMatrixElement[]{element};
            DistanceMatrixRow[] rows = new DistanceMatrixRow[]{row};
            DistanceMatrix distanceMatrix = new DistanceMatrix(null, null, rows);
            when(requestMock.await()).thenReturn(distanceMatrix);

            // Mock repository save
            OrderPo expectedOrder = OrderPo.builder().distance(5000L).status("UNASSIGNED").build();
            when(orderRepository.save(any(OrderPo.class))).thenReturn(expectedOrder);

            // Execute service method
            OrderPo result = placeOrderService.placeOrder(1.0, 2.0, 3.0, 4.0);

            // Verify saved OrderPo
            ArgumentCaptor<OrderPo> orderPoCaptor = ArgumentCaptor.forClass(OrderPo.class);
            verify(orderRepository).save(orderPoCaptor.capture());
            OrderPo savedOrder = orderPoCaptor.getValue();

            assertThat(savedOrder.getDistance()).isEqualTo(5000L);
            assertThat(savedOrder.getStatus()).isEqualTo("UNASSIGNED");
            assertThat(result).isEqualTo(expectedOrder);

            // Verify API request parameters
            ArgumentCaptor<LatLng> originsCaptor = ArgumentCaptor.forClass(LatLng.class);
            verify(requestMock).origins(originsCaptor.capture());
            LatLng origins = originsCaptor.getValue();
            assertThat(origins.lat).isEqualTo(1.0);
            assertThat(origins.lng).isEqualTo(2.0);

            ArgumentCaptor<LatLng> destinationsCaptor = ArgumentCaptor.forClass(LatLng.class);
            verify(requestMock).destinations(destinationsCaptor.capture());
            LatLng destinations = destinationsCaptor.getValue();
            assertThat(destinations.lat).isEqualTo(3.0);
            assertThat(destinations.lng).isEqualTo(4.0);
        }
    }

    @Test
    void placeOrder_ApiReturnsErrorStatus() throws Exception {
        try (MockedStatic<DistanceMatrixApi> mockedDistanceMatrixApi = mockStatic(DistanceMatrixApi.class)) {
            DistanceMatrixApiRequest requestMock = mock(DistanceMatrixApiRequest.class);
            mockedDistanceMatrixApi.when(() -> DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(requestMock);
            when(requestMock.origins(any(LatLng.class))).thenReturn(requestMock);
            when(requestMock.destinations(any(LatLng.class))).thenReturn(requestMock);

            // Setup error status response
            DistanceMatrixRow row = new DistanceMatrixRow();
            DistanceMatrixElement element = new DistanceMatrixElement();
            element.status = DistanceMatrixElementStatus.NOT_FOUND; // Example error status
            row.elements = new DistanceMatrixElement[]{element};
            DistanceMatrixRow[] rows = new DistanceMatrixRow[]{row};
            DistanceMatrix distanceMatrix = new DistanceMatrix(null, null, rows);
            when(requestMock.await()).thenReturn(distanceMatrix);

            // Execute and verify exception
            assertThrows(RuntimeException.class, () -> placeOrderService.placeOrder(1.0, 2.0, 3.0, 4.0));
            verify(orderRepository, never()).save(any(OrderPo.class));
        }
    }

    @Test
    void placeOrder_ApiThrowsIOException() throws Exception {
        try (MockedStatic<DistanceMatrixApi> mockedDistanceMatrixApi = mockStatic(DistanceMatrixApi.class)) {
            DistanceMatrixApiRequest requestMock = mock(DistanceMatrixApiRequest.class);
            mockedDistanceMatrixApi.when(() -> DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(requestMock);
            when(requestMock.origins(any(LatLng.class))).thenReturn(requestMock);
            when(requestMock.destinations(any(LatLng.class))).thenReturn(requestMock);

            // Simulate IO exception
            when(requestMock.await()).thenThrow(new IOException("IO Exception"));

            // Execute and verify exception
            assertThrows(RuntimeException.class, () -> placeOrderService.placeOrder(1.0, 2.0, 3.0, 4.0));
            verify(orderRepository, never()).save(any(OrderPo.class));
        }
    }

    @Test
    void placeOrder_InterruptedException() throws Exception {
        try (MockedStatic<DistanceMatrixApi> mockedDistanceMatrixApi = mockStatic(DistanceMatrixApi.class)) {
            DistanceMatrixApiRequest requestMock = mock(DistanceMatrixApiRequest.class);
            mockedDistanceMatrixApi.when(() -> DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(requestMock);
            when(requestMock.origins(any(LatLng.class))).thenReturn(requestMock);
            when(requestMock.destinations(any(LatLng.class))).thenReturn(requestMock);

            // Simulate InterruptedException
            when(requestMock.await()).thenThrow(new InterruptedException());

            // Execute and verify exception
            assertThrows(RuntimeException.class, () -> placeOrderService.placeOrder(1.0, 2.0, 3.0, 4.0));
            verify(orderRepository, never()).save(any(OrderPo.class));
        }
    }
}