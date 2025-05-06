package kk9uk.OrderBook.repository;

import kk9uk.OrderBook.po.OrderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderPo, Long> {
}
