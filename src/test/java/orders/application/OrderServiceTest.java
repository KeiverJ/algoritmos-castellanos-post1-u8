package orders.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import orders.adapter.out.persistence.InMemoryOrderRepository;
import orders.application.port.in.CreateOrderUseCase;
import orders.application.port.in.UpdateOrderStatusUseCase;
import orders.application.service.InvalidOrderStatusTransitionException;
import orders.application.service.OrderNotFoundException;
import orders.application.service.OrderService;
import orders.domain.model.Order;
import orders.domain.model.OrderItem;
import orders.domain.model.OrderStatus;

class OrderServiceTest {

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(new InMemoryOrderRepository());
    }

    @Test
    @DisplayName("Crea pedido con estado PENDING y total calculado")
    void shouldCreateOrder() {
        Order created = service.execute(new CreateOrderUseCase.Command(
                "CUST-1",
                List.of(new OrderItem("P-1", 2, 100.0))
        ));

        assertNotNull(created.id());
        assertEquals(OrderStatus.PENDING, created.status());
        assertEquals(200.0, created.total());
    }

    @Test
    @DisplayName("Obtiene pedido por ID existente")
    void shouldGetOrderById() {
        Order created = service.execute(new CreateOrderUseCase.Command(
                "CUST-2",
                List.of(new OrderItem("P-2", 1, 50.0))
        ));

        Order loaded = service.execute(created.id());

        assertEquals(created.id(), loaded.id());
        assertEquals("CUST-2", loaded.customerId());
    }

    @Test
    @DisplayName("Actualiza estado con transicion valida")
    void shouldUpdateOrderStatus() {
        Order created = service.execute(new CreateOrderUseCase.Command(
                "CUST-3",
                List.of(new OrderItem("P-3", 1, 30.0))
        ));

        Order updated = service.execute(new UpdateOrderStatusUseCase.Command(
                created.id(),
                OrderStatus.CONFIRMED
        ));

        assertEquals(OrderStatus.CONFIRMED, updated.status());
    }

    @Test
    @DisplayName("Falla al crear pedido sin items")
    void shouldFailWhenCreateOrderWithoutItems() {
        assertThrows(IllegalArgumentException.class, () -> service.execute(
                new CreateOrderUseCase.Command("CUST-4", List.of())
        ));
    }

    @Test
    @DisplayName("Falla al consultar ID inexistente")
    void shouldFailWhenOrderIdDoesNotExist() {
        assertThrows(OrderNotFoundException.class, () -> service.execute("does-not-exist"));
    }

    @Test
    @DisplayName("Falla cuando la transicion de estado es invalida")
    void shouldFailWhenStatusTransitionIsInvalid() {
        Order created = service.execute(new CreateOrderUseCase.Command(
                "CUST-5",
                List.of(new OrderItem("P-5", 1, 10.0))
        ));

        assertThrows(InvalidOrderStatusTransitionException.class, () -> service.execute(
                new UpdateOrderStatusUseCase.Command(created.id(), OrderStatus.DELIVERED)
        ));
    }
}
