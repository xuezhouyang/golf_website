import 'package:flutter/material.dart';
import '../models/order.dart';
import '../models/cart_item.dart';
import '../models/product.dart';

class OrdersScreen extends StatefulWidget {
  const OrdersScreen({super.key});

  @override
  State<OrdersScreen> createState() => _OrdersScreenState();
}

class _OrdersScreenState extends State<OrdersScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 5, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  // 模拟订单数据
  List<Order> _getMockOrders() {
    final product1 = Product(
      id: '1',
      name: 'TaylorMade M6 一号木',
      description: '全新 TaylorMade M6 系列一号木',
      price: 2999.00,
      imageUrl: '',
      category: '一号木',
      brand: 'TaylorMade',
      images: [],
    );

    final product2 = Product(
      id: '2',
      name: 'Callaway Epic Flash 一号木',
      description: 'Callaway Epic Flash',
      price: 3299.00,
      imageUrl: '',
      category: '一号木',
      brand: 'Callaway',
      images: [],
    );

    return [
      Order(
        id: 'ORD001',
        items: [CartItem(product: product1, quantity: 1)],
        totalAmount: 2999.00,
        status: OrderStatus.pending,
        createdAt: DateTime.now().subtract(const Duration(hours: 2)),
        shippingAddress: '北京市朝阳区xxx街道xxx号',
      ),
      Order(
        id: 'ORD002',
        items: [
          CartItem(product: product1, quantity: 1),
          CartItem(product: product2, quantity: 1),
        ],
        totalAmount: 6298.00,
        status: OrderStatus.paid,
        createdAt: DateTime.now().subtract(const Duration(days: 1)),
        shippingAddress: '北京市朝阳区xxx街道xxx号',
      ),
      Order(
        id: 'ORD003',
        items: [CartItem(product: product2, quantity: 2)],
        totalAmount: 6598.00,
        status: OrderStatus.shipped,
        createdAt: DateTime.now().subtract(const Duration(days: 3)),
        trackingNumber: 'SF1234567890',
        shippingAddress: '北京市朝阳区xxx街道xxx号',
      ),
      Order(
        id: 'ORD004',
        items: [CartItem(product: product1, quantity: 1)],
        totalAmount: 2999.00,
        status: OrderStatus.delivered,
        createdAt: DateTime.now().subtract(const Duration(days: 7)),
        trackingNumber: 'SF0987654321',
        shippingAddress: '北京市朝阳区xxx街道xxx号',
      ),
    ];
  }

  @override
  Widget build(BuildContext context) {
    final orders = _getMockOrders();

    return Scaffold(
      appBar: AppBar(
        title: const Text('我的订单'),
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: const [
            Tab(text: '全部'),
            Tab(text: '待支付'),
            Tab(text: '待发货'),
            Tab(text: '待收货'),
            Tab(text: '已完成'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildOrderList(orders),
          _buildOrderList(
              orders.where((o) => o.status == OrderStatus.pending).toList()),
          _buildOrderList(
              orders.where((o) => o.status == OrderStatus.paid).toList()),
          _buildOrderList(
              orders.where((o) => o.status == OrderStatus.shipped).toList()),
          _buildOrderList(
              orders.where((o) => o.status == OrderStatus.delivered).toList()),
        ],
      ),
    );
  }

  Widget _buildOrderList(List<Order> orders) {
    if (orders.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.receipt_long_outlined,
              size: 80,
              color: Colors.grey[300],
            ),
            const SizedBox(height: 16),
            Text(
              '暂无订单',
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: orders.length,
      itemBuilder: (context, index) {
        return _buildOrderCard(context, orders[index]);
      },
    );
  }

  Widget _buildOrderCard(BuildContext context, Order order) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 订单头部
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.grey[50],
              borderRadius: const BorderRadius.vertical(
                top: Radius.circular(8),
              ),
            ),
            child: Row(
              children: [
                Text(
                  '订单号：${order.id}',
                  style: const TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const Spacer(),
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: _getStatusColor(order.status).withOpacity(0.1),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    order.statusText,
                    style: TextStyle(
                      fontSize: 12,
                      color: _getStatusColor(order.status),
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ],
            ),
          ),

          // 商品列表
          ...order.items.map((item) => _buildOrderItem(item)),

          const Divider(height: 1),

          // 订单底部
          Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    const Text('共 '),
                    Text(
                      '${order.items.fold(0, (sum, item) => sum + item.quantity)}',
                      style: TextStyle(
                        color: Theme.of(context).primaryColor,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const Text(' 件 合计：'),
                    Text(
                      '¥${order.totalAmount.toStringAsFixed(2)}',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Theme.of(context).primaryColor,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    if (order.status == OrderStatus.pending)
                      OutlinedButton(
                        onPressed: () {
                          // TODO: Cancel order
                        },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.grey[700],
                        ),
                        child: const Text('取消订单'),
                      ),
                    const SizedBox(width: 8),
                    if (order.status == OrderStatus.pending)
                      ElevatedButton(
                        onPressed: () {
                          // TODO: Pay order
                        },
                        child: const Text('立即支付'),
                      ),
                    if (order.status == OrderStatus.shipped)
                      ElevatedButton(
                        onPressed: () {
                          // TODO: View logistics
                        },
                        child: const Text('查看物流'),
                      ),
                    if (order.status == OrderStatus.delivered)
                      ElevatedButton(
                        onPressed: () {
                          // TODO: Review
                        },
                        child: const Text('评价'),
                      ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOrderItem(CartItem item) {
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Row(
        children: [
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              color: Colors.grey[200],
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(
              Icons.sports_golf,
              size: 30,
              color: Colors.grey[400],
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.product.name,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  item.product.brand,
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                '¥${item.product.price.toStringAsFixed(2)}',
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                'x${item.quantity}',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[600],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _getStatusColor(OrderStatus status) {
    switch (status) {
      case OrderStatus.pending:
        return Colors.orange;
      case OrderStatus.paid:
        return Colors.blue;
      case OrderStatus.shipped:
        return Colors.purple;
      case OrderStatus.delivered:
        return Colors.green;
      case OrderStatus.cancelled:
        return Colors.grey;
      case OrderStatus.refunding:
      case OrderStatus.refunded:
        return Colors.red;
    }
  }
}
