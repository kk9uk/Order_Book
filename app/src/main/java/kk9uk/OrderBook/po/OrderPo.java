package kk9uk.OrderBook.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPo {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private long distance;
  private String status;
}
