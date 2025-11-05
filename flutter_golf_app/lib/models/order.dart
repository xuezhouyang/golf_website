import 'cart_item.dart';

enum OrderStatus {
  pending,
  paid,
  shipped,
  delivered,
  cancelled,
  refunding,
  refunded,
}

class Order {
  final String id;
  final List<CartItem> items;
  final double totalAmount;
  final OrderStatus status;
  final DateTime createdAt;
  final String? trackingNumber;
  final String shippingAddress;

  Order({
    required this.id,
    required this.items,
    required this.totalAmount,
    required this.status,
    required this.createdAt,
    this.trackingNumber,
    required this.shippingAddress,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      id: json['id'],
      items: (json['items'] as List)
          .map((item) => CartItem.fromJson(item))
          .toList(),
      totalAmount: json['totalAmount'].toDouble(),
      status: OrderStatus.values.firstWhere(
        (e) => e.toString() == 'OrderStatus.${json['status']}',
        orElse: () => OrderStatus.pending,
      ),
      createdAt: DateTime.parse(json['createdAt']),
      trackingNumber: json['trackingNumber'],
      shippingAddress: json['shippingAddress'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'items': items.map((item) => item.toJson()).toList(),
      'totalAmount': totalAmount,
      'status': status.toString().split('.').last,
      'createdAt': createdAt.toIso8601String(),
      'trackingNumber': trackingNumber,
      'shippingAddress': shippingAddress,
    };
  }

  String get statusText {
    switch (status) {
      case OrderStatus.pending:
        return '待支付';
      case OrderStatus.paid:
        return '已支付';
      case OrderStatus.shipped:
        return '已发货';
      case OrderStatus.delivered:
        return '已送达';
      case OrderStatus.cancelled:
        return '已取消';
      case OrderStatus.refunding:
        return '退款中';
      case OrderStatus.refunded:
        return '已退款';
    }
  }
}
