import 'package:flutter/material.dart';
import '../models/address.dart';
import 'address_edit_screen.dart';

class AddressManageScreen extends StatefulWidget {
  const AddressManageScreen({super.key});

  @override
  State<AddressManageScreen> createState() => _AddressManageScreenState();
}

class _AddressManageScreenState extends State<AddressManageScreen> {
  List<Address> addresses = [
    Address(
      id: '1',
      name: '张三',
      phone: '13800138000',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detail: 'xxx街道xxx号',
      isDefault: true,
    ),
    Address(
      id: '2',
      name: '李四',
      phone: '13900139000',
      province: '上海市',
      city: '上海市',
      district: '浦东新区',
      detail: 'yyy路yyy号',
      isDefault: false,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('收货地址'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: addresses.length,
        itemBuilder: (context, index) {
          return _buildAddressCard(addresses[index]);
        },
      ),
      bottomNavigationBar: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 4,
              offset: const Offset(0, -2),
            ),
          ],
        ),
        child: SafeArea(
          child: ElevatedButton.icon(
            onPressed: () async {
              final result = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const AddressEditScreen(),
                ),
              );
              if (result != null && result is Address) {
                setState(() {
                  addresses.add(result);
                });
              }
            },
            icon: const Icon(Icons.add),
            label: const Text('新增收货地址'),
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 14),
              minimumSize: const Size(double.infinity, 48),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildAddressCard(Address address) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: address.isDefault
              ? Theme.of(context).primaryColor
              : Colors.grey[300]!,
          width: address.isDefault ? 2 : 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                address.name,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(width: 16),
              Text(
                address.phone,
                style: TextStyle(
                  fontSize: 14,
                  color: Colors.grey[600],
                ),
              ),
              const Spacer(),
              if (address.isDefault)
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: const Text(
                    '默认',
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.white,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            address.fullAddress,
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[800],
              height: 1.5,
            ),
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              if (!address.isDefault)
                TextButton.icon(
                  onPressed: () {
                    setState(() {
                      for (var addr in addresses) {
                        addr = Address(
                          id: addr.id,
                          name: addr.name,
                          phone: addr.phone,
                          province: addr.province,
                          city: addr.city,
                          district: addr.district,
                          detail: addr.detail,
                          isDefault: addr.id == address.id,
                        );
                      }
                    });
                  },
                  icon: const Icon(Icons.check_circle_outline, size: 18),
                  label: const Text('设为默认'),
                ),
              TextButton.icon(
                onPressed: () async {
                  final result = await Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) =>
                          AddressEditScreen(address: address),
                    ),
                  );
                  if (result != null && result is Address) {
                    setState(() {
                      final index =
                          addresses.indexWhere((a) => a.id == address.id);
                      if (index != -1) {
                        addresses[index] = result;
                      }
                    });
                  }
                },
                icon: const Icon(Icons.edit_outlined, size: 18),
                label: const Text('编辑'),
              ),
              TextButton.icon(
                onPressed: () {
                  _showDeleteDialog(address);
                },
                icon: const Icon(Icons.delete_outline, size: 18),
                label: const Text('删除'),
                style: TextButton.styleFrom(
                  foregroundColor: Colors.red,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  void _showDeleteDialog(Address address) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('删除地址'),
        content: const Text('确定要删除这个地址吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              setState(() {
                addresses.removeWhere((a) => a.id == address.id);
              });
              Navigator.pop(context);
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }
}
