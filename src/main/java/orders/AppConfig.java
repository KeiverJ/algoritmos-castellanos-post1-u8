package orders;

import orders.adapter.in.http.OrderController;
import orders.adapter.out.persistence.InMemoryOrderRepository;
import orders.application.service.OrderService;

/**
 * Wiring manual del sistema sin framework de inyeccion.
 */
public final class AppConfig {

    private AppConfig() {
    }

    public static OrderController orderController() {
        var repository = new InMemoryOrderRepository();
        var orderService = new OrderService(repository);
        return new OrderController(orderService, orderService, orderService);
    }
}
