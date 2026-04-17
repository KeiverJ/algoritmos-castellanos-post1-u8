package orders.adapter.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import orders.application.port.out.OrderRepository;
import orders.domain.model.Order;

/**
 * Implementacion en memoria del puerto OrderRepository.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        store.put(order.id(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(o -> o.customerId().equals(customerId))
                .toList();
    }
}
