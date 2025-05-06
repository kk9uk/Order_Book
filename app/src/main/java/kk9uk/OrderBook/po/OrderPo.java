package kk9uk.OrderBook.po;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "Orders")
@Data
@Builder
public class OrderPo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private long distance;
  private String status;
}
