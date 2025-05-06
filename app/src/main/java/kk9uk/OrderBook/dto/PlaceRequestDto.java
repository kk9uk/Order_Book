package kk9uk.OrderBook.dto;

import java.util.List;

public record PlaceRequestDto(List<String> origin, List<String> destination) {
}
