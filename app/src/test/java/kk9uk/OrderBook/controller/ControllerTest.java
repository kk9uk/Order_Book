package kk9uk.OrderBook.controller;

import kk9uk.OrderBook.dto.PlaceRequestDto;
import kk9uk.OrderBook.dto.TakeOrderDto;
import kk9uk.OrderBook.po.OrderPo;
import kk9uk.OrderBook.repository.OrderRepository;
import kk9uk.OrderBook.service.PlaceOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private PlaceOrderService placeOrderService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private Controller controller;

    // Tests for placeOrder method

    @Test
    void placeOrder_InvalidOriginSize_ThrowsException() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("12.34"),
                List.of("56.78", "90.12")
        );
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.placeOrder(dto));
        assertEquals("Coordinates in request must be an array of exactly two strings", exception.getMessage());
        verifyNoInteractions(placeOrderService);
    }

    @Test
    void placeOrder_InvalidDestinationSize_ThrowsException() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("12.34", "56.78"),
                List.of("90.12")
        );
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.placeOrder(dto));
        assertEquals("Coordinates in request must be an array of exactly two strings", exception.getMessage());
        verifyNoInteractions(placeOrderService);
    }

    @Test
    void placeOrder_InvalidLatitude_ThrowsException() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("91.0", "0.0"),
                List.of("45.0", "180.0")
        );
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.placeOrder(dto));
        assertEquals("Invalid latitude or longitude value in coordinates", exception.getMessage());
        verifyNoInteractions(placeOrderService);
    }

    @Test
    void placeOrder_InvalidLongitude_ThrowsException() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("45.0", "181.0"),
                List.of("90.0", "180.0")
        );
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.placeOrder(dto));
        assertEquals("Invalid latitude or longitude value in coordinates", exception.getMessage());
        verifyNoInteractions(placeOrderService);
    }

    @Test
    void placeOrder_NonNumericOrigin_ThrowsNumberFormatException() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("invalid", "123.45"),
                List.of("67.89", "101.12")
        );
        assertThrows(NumberFormatException.class, () -> controller.placeOrder(dto));
        verifyNoInteractions(placeOrderService);
    }

    @Test
    void placeOrder_ValidInput_ReturnsOrderPo() {
        PlaceRequestDto dto = new PlaceRequestDto(
                List.of("12.34", "56.78"),
                List.of("90.0", "180.0")
        );
        OrderPo expectedOrder = new OrderPo();
        when(placeOrderService.placeOrder(12.34, 56.78, 90.0, 180.0)).thenReturn(expectedOrder);

        OrderPo result = controller.placeOrder(dto);

        assertEquals(expectedOrder, result);
        verify(placeOrderService).placeOrder(12.34, 56.78, 90.0, 180.0);
    }

    // Tests for takeOrder method

    @Test
    void takeOrder_InvalidStatus_ThrowsException() {
        TakeOrderDto dto = new TakeOrderDto("INVALID");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.takeOrder(1L, dto));
        assertEquals("Invalid order-taking request", exception.getMessage());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void takeOrder_AtomicTakeOrderSuccess_ReturnsSuccessDto() {
        TakeOrderDto dto = new TakeOrderDto("TAKEN");
        when(orderRepository.atomicTakeOrder(1L)).thenReturn(1);

        TakeOrderDto result = controller.takeOrder(1L, dto);

        assertEquals("SUCCESS", result.status());
        verify(orderRepository).atomicTakeOrder(1L);
    }

    @Test
    void takeOrder_AtomicTakeOrderFailure_ThrowsException() {
        TakeOrderDto dto = new TakeOrderDto("TAKEN");
        when(orderRepository.atomicTakeOrder(1L)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.takeOrder(1L, dto));
        assertEquals("FAILURE", exception.getMessage());
        verify(orderRepository).atomicTakeOrder(1L);
    }

    // Tests for listOrders method

    @Test
    void listOrders_InvalidPage_ThrowsException() {
        assertThrows(RuntimeException.class, () -> controller.listOrders(0, 10));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void listOrders_InvalidLimit_ThrowsException() {
        assertThrows(RuntimeException.class, () -> controller.listOrders(1, 0));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void listOrders_ValidPageAndLimit_ReturnsOrders() {
        List<OrderPo> expectedOrders = List.of(new OrderPo());
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(orderRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(expectedOrders));

        List<OrderPo> result = controller.listOrders(1, 10);

        assertEquals(expectedOrders, result);
        verify(orderRepository).findAll(pageRequest);
    }
}