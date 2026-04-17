package orders.application.port.out;

import java.util.List;
import java.util.Optional;
import orders.domain.model.Order;

/**
 * Puerto de salida para persistir pedidos.
 * El dominio define esta interfaz y la infraestructura la implementa.
 */
public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(String id);

    List<Order> findByCustomerId(String customerId);
}
