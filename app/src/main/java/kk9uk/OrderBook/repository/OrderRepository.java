package kk9uk.OrderBook.repository;

import kk9uk.OrderBook.po.OrderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<OrderPo, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE OrderPo o SET o.status = 'TAKEN' WHERE o.id = :id AND o.status = 'UNASSIGNED'")
    int atomicTakeOrder(@Param("id") Long id); // returns no. of rows updated
}
