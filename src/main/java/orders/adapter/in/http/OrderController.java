package orders.adapter.in.http;

import java.util.List;
import java.util.Objects;
import orders.application.port.in.CreateOrderUseCase;
import orders.application.port.in.GetOrderUseCase;
import orders.application.port.in.UpdateOrderStatusUseCase;
import orders.application.service.InvalidOrderStatusTransitionException;
import orders.application.service.OrderNotFoundException;
import orders.domain.model.Order;
import orders.domain.model.OrderItem;
import orders.domain.model.OrderStatus;

/**
 * Adaptador HTTP que traduce requests del exterior a comandos de aplicacion.
 */
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final GetOrderUseCase getOrder;
    private final UpdateOrderStatusUseCase updateStatus;

    public OrderController(CreateOrderUseCase createOrder,
                           GetOrderUseCase getOrder,
                           UpdateOrderStatusUseCase updateStatus) {
        this.createOrder = Objects.requireNonNull(createOrder, "createOrder is required");
        this.getOrder = Objects.requireNonNull(getOrder, "getOrder is required");
        this.updateStatus = Objects.requireNonNull(updateStatus, "updateStatus is required");
    }

    public HttpResponse<OrderResponse> handleCreate(CreateOrderRequest req) {
        Objects.requireNonNull(req, "request is required");

        try {
            var cmd = new CreateOrderUseCase.Command(
                    req.customerId(),
                    req.items().stream()
                            .map(i -> new OrderItem(i.productId(), i.qty(), i.price()))
                            .toList()
            );
            return HttpResponse.created(OrderResponse.from(createOrder.execute(cmd)));
        } catch (IllegalArgumentException ex) {
            return HttpResponse.badRequest(ex.getMessage());
        }
    }

    public HttpResponse<OrderResponse> handleGet(String id) {
        try {
            return HttpResponse.ok(OrderResponse.from(getOrder.execute(id)));
        } catch (OrderNotFoundException ex) {
            return HttpResponse.notFound(ex.getMessage());
        }
    }

    public HttpResponse<OrderResponse> handleUpdateStatus(String id, String status) {
        final OrderStatus parsedStatus;
        try {
            parsedStatus = OrderStatus.valueOf(status);
        } catch (RuntimeException ex) {
            return HttpResponse.badRequest("Invalid status: " + status);
        }

        try {
            var cmd = new UpdateOrderStatusUseCase.Command(id, parsedStatus);
            return HttpResponse.ok(OrderResponse.from(updateStatus.execute(cmd)));
        } catch (OrderNotFoundException ex) {
            return HttpResponse.notFound(ex.getMessage());
        } catch (InvalidOrderStatusTransitionException ex) {
            return HttpResponse.badRequest(ex.getMessage());
        }
    }

    public record CreateOrderRequest(String customerId, List<OrderItemRequest> items) {
    }

    public record OrderItemRequest(String productId, int qty, double price) {
    }

    public record OrderResponse(String id,
                                String customerId,
                                List<OrderItemResponse> items,
                                String status,
                                double total) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.id(),
                    order.customerId(),
                    order.items().stream()
                            .map(i -> new OrderItemResponse(i.productId(), i.quantity(), i.price()))
                            .toList(),
                    order.status().name(),
                    order.total()
            );
        }
    }

    public record OrderItemResponse(String productId, int quantity, double price) {
    }

    public record HttpResponse<T>(int status, T body, String error) {
        public static <T> HttpResponse<T> ok(T body) {
            return new HttpResponse<>(200, body, null);
        }

        public static <T> HttpResponse<T> created(T body) {
            return new HttpResponse<>(201, body, null);
        }

        public static <T> HttpResponse<T> badRequest(String error) {
            return new HttpResponse<>(400, null, error);
        }

        public static <T> HttpResponse<T> notFound(String error) {
            return new HttpResponse<>(404, null, error);
        }
    }
}
