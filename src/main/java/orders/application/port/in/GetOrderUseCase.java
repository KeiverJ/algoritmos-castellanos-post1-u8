package orders.application.port.in;

import orders.domain.model.Order;

/**
 * Puerto de entrada para consultar un pedido por ID.
 * Postcondicion: retorna el pedido si existe.
 * Excepcion: lanza OrderNotFoundException si no existe.
 */
public interface GetOrderUseCase {
    Order execute(String orderId);
}
