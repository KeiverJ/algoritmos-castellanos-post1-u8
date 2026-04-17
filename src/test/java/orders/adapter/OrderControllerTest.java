package orders.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import orders.adapter.in.http.OrderController;
import orders.adapter.out.persistence.InMemoryOrderRepository;
import orders.application.service.OrderService;

class OrderControllerTest {

    private OrderController controller;

    @BeforeEach
    void setUp() {
        var repository = new InMemoryOrderRepository();
        var service = new OrderService(repository);
        controller = new OrderController(service, service, service);
    }

    @Test
    @DisplayName("handleCreate retorna 201 con respuesta correcta")
    void shouldReturnCreatedWhenHandleCreate() {
        var req = new OrderController.CreateOrderRequest(
                "CUST-HTTP-1",
                List.of(new OrderController.OrderItemRequest("PR-1", 2, 25.0))
        );

        var response = controller.handleCreate(req);

        assertEquals(201, response.status());
        assertNotNull(response.body());
        assertEquals("CUST-HTTP-1", response.body().customerId());
        assertEquals("PENDING", response.body().status());
        assertEquals(50.0, response.body().total());
    }

    @Test
    @DisplayName("handleGet retorna 404 para ID inexistente")
    void shouldReturnNotFoundWhenHandleGetMissingOrder() {
        var response = controller.handleGet("ID-NO-EXISTE");

        assertEquals(404, response.status());
        assertEquals(null, response.body());
    }

    @Test
    @DisplayName("handleUpdateStatus retorna 400 para transicion invalida")
    void shouldReturnBadRequestWhenTransitionIsInvalid() {
        var req = new OrderController.CreateOrderRequest(
                "CUST-HTTP-2",
                List.of(new OrderController.OrderItemRequest("PR-2", 1, 30.0))
        );

        var created = controller.handleCreate(req);
        var response = controller.handleUpdateStatus(created.body().id(), "DELIVERED");

        assertEquals(400, response.status());
        assertNotNull(response.error());
    }
}
