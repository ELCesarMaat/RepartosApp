package com.robles.cesar.pedidosapp.models

data class Order(
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val orderDetails: String,
    val deliveryId: String,
    val status: OrderStatus
)
enum class OrderStatus(val code: Long) {
    PENDIENTE(1L),
    EN_REPARTO(2L),
    ENTREGADO(3L);

    companion object {
        fun fromInt(value: Long): OrderStatus {
            return when (value) {
                1L -> PENDIENTE
                2L -> EN_REPARTO
                3L -> ENTREGADO
                else -> throw IllegalArgumentException("Invalid status code")
            }
        }
    }
}
