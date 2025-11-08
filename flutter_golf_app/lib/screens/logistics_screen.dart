import 'package:flutter/material.dart';

class LogisticsScreen extends StatelessWidget {
  final String trackingNumber;
  final String orderId;

  const LogisticsScreen({
    super.key,
    required this.trackingNumber,
    required this.orderId,
  });

  @override
  Widget build(BuildContext context) {
    final List<Map<String, String>> logistics = [
      {
        'time': '2025-01-08 14:30',
        'status': '已签收',
        'detail': '您的快件已被本人签收，感谢使用顺丰速运，期待再次为您服务'
      },
      {
        'time': '2025-01-08 10:15',
        'status': '派送中',
        'detail': '快件正在派送中，快递员：张三，联系电话：138****0000'
      },
      {
        'time': '2025-01-08 08:00',
        'status': '到达网点',
        'detail': '快件已到达【北京朝阳营业点】'
      },
      {
        'time': '2025-01-07 22:30',
        'status': '运输中',
        'detail': '快件已发往【北京朝阳营业点】'
      },
      {
        'time': '2025-01-07 18:00',
        'status': '运输中',
        'detail': '快件已发车'
      },
      {
        'time': '2025-01-07 15:00',
        'status': '已揽收',
        'detail': '您的快件已由【上海浦东营业点】揽收'
      },
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text('物流信息'),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 物流信息头部
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              color: Theme.of(context).primaryColor.withOpacity(0.1),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(
                        Icons.local_shipping,
                        color: Theme.of(context).primaryColor,
                      ),
                      const SizedBox(width: 8),
                      const Text(
                        '顺丰速运',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '运单号：$trackingNumber',
                    style: TextStyle(
                      fontSize: 14,
                      color: Colors.grey[600],
                    ),
                  ),
                  Text(
                    '订单号：$orderId',
                    style: TextStyle(
                      fontSize: 14,
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            ),

            // 物流进度
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              padding: const EdgeInsets.all(16),
              itemCount: logistics.length,
              itemBuilder: (context, index) {
                final isFirst = index == 0;
                return _buildLogisticsItem(
                  context,
                  time: logistics[index]['time']!,
                  status: logistics[index]['status']!,
                  detail: logistics[index]['detail']!,
                  isFirst: isFirst,
                  isLast: index == logistics.length - 1,
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLogisticsItem(
    BuildContext context, {
    required String time,
    required String status,
    required String detail,
    required bool isFirst,
    required bool isLast,
  }) {
    return IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 时间轴
          SizedBox(
            width: 60,
            child: Text(
              time.split(' ')[1],
              style: TextStyle(
                fontSize: 12,
                color: isFirst
                    ? Theme.of(context).primaryColor
                    : Colors.grey[600],
              ),
            ),
          ),
          Column(
            children: [
              Container(
                width: 12,
                height: 12,
                decoration: BoxDecoration(
                  color: isFirst
                      ? Theme.of(context).primaryColor
                      : Colors.grey[400],
                  shape: BoxShape.circle,
                ),
              ),
              if (!isLast)
                Expanded(
                  child: Container(
                    width: 2,
                    color: Colors.grey[300],
                  ),
                ),
            ],
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.only(bottom: 24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        status,
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight:
                              isFirst ? FontWeight.bold : FontWeight.normal,
                          color: isFirst
                              ? Theme.of(context).primaryColor
                              : Colors.black87,
                        ),
                      ),
                      const Spacer(),
                      Text(
                        time.split(' ')[0],
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    detail,
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey[600],
                      height: 1.5,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
