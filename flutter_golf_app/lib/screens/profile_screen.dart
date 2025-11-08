import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_provider.dart';
import 'login_screen.dart';
import 'orders_screen.dart';
import 'favorites_screen.dart';
import 'address_manage_screen.dart';
import 'profile_edit_screen.dart';
import 'security_center_screen.dart';
import 'settings_screen.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('我的'),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // 用户信息卡片
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Theme.of(context).primaryColor,
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.1),
                    blurRadius: 4,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: authProvider.isAuthenticated
                  ? _buildUserInfo(context, authProvider)
                  : _buildLoginPrompt(context),
            ),

            const SizedBox(height: 8),

            // 订单管理
            if (authProvider.isAuthenticated) ...[
              Container(
                color: Colors.white,
                child: Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Text(
                            '我的订单',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const Spacer(),
                          TextButton(
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => const OrdersScreen(),
                                ),
                              );
                            },
                            child: const Text('查看全部'),
                          ),
                        ],
                      ),
                    ),
                    const Divider(height: 1),
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          _buildOrderStatusItem(
                            context,
                            icon: Icons.payment,
                            label: '待支付',
                            count: 2,
                          ),
                          _buildOrderStatusItem(
                            context,
                            icon: Icons.local_shipping_outlined,
                            label: '待发货',
                            count: 1,
                          ),
                          _buildOrderStatusItem(
                            context,
                            icon: Icons.inventory_2_outlined,
                            label: '待收货',
                            count: 3,
                          ),
                          _buildOrderStatusItem(
                            context,
                            icon: Icons.rate_review_outlined,
                            label: '待评价',
                            count: 0,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 8),

              // 功能菜单
              Container(
                color: Colors.white,
                child: Column(
                  children: [
                    _buildMenuItem(
                      context,
                      icon: Icons.favorite_outline,
                      title: '我的收藏',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const FavoritesScreen(),
                          ),
                        );
                      },
                    ),
                    const Divider(height: 1),
                    _buildMenuItem(
                      context,
                      icon: Icons.location_on_outlined,
                      title: '收货地址',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const AddressManageScreen(),
                          ),
                        );
                      },
                    ),
                    const Divider(height: 1),
                    _buildMenuItem(
                      context,
                      icon: Icons.account_circle_outlined,
                      title: '个人信息',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const ProfileEditScreen(),
                          ),
                        );
                      },
                    ),
                    const Divider(height: 1),
                    _buildMenuItem(
                      context,
                      icon: Icons.security_outlined,
                      title: '安全中心',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const SecurityCenterScreen(),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 8),

              // 其他功能
              Container(
                color: Colors.white,
                child: Column(
                  children: [
                    _buildMenuItem(
                      context,
                      icon: Icons.help_outline,
                      title: '帮助中心',
                      onTap: () {},
                    ),
                    const Divider(height: 1),
                    _buildMenuItem(
                      context,
                      icon: Icons.settings_outlined,
                      title: '设置',
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const SettingsScreen(),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 24),

              // 退出登录按钮
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: OutlinedButton(
                  onPressed: () {
                    _showLogoutDialog(context, authProvider);
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: Colors.red,
                    side: const BorderSide(color: Colors.red),
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    minimumSize: const Size(double.infinity, 48),
                  ),
                  child: const Text('退出登录'),
                ),
              ),
            ],

            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }

  Widget _buildUserInfo(BuildContext context, AuthProvider authProvider) {
    final user = authProvider.user!;
    return Row(
      children: [
        CircleAvatar(
          radius: 35,
          backgroundColor: Colors.white,
          child: Icon(
            Icons.person,
            size: 40,
            color: Theme.of(context).primaryColor,
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                user.username,
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                user.email,
                style: const TextStyle(
                  fontSize: 14,
                  color: Colors.white70,
                ),
              ),
            ],
          ),
        ),
        IconButton(
          onPressed: () {},
          icon: const Icon(
            Icons.edit,
            color: Colors.white,
          ),
        ),
      ],
    );
  }

  Widget _buildLoginPrompt(BuildContext context) {
    return InkWell(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const LoginScreen(),
          ),
        );
      },
      child: Row(
        children: [
          CircleAvatar(
            radius: 35,
            backgroundColor: Colors.white,
            child: Icon(
              Icons.person_outline,
              size: 40,
              color: Theme.of(context).primaryColor,
            ),
          ),
          const SizedBox(width: 16),
          const Text(
            '点击登录/注册',
            style: TextStyle(
              fontSize: 18,
              color: Colors.white,
            ),
          ),
          const Spacer(),
          const Icon(
            Icons.chevron_right,
            color: Colors.white,
          ),
        ],
      ),
    );
  }

  Widget _buildOrderStatusItem(
    BuildContext context, {
    required IconData icon,
    required String label,
    required int count,
  }) {
    return InkWell(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const OrdersScreen(),
          ),
        );
      },
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Column(
            children: [
              Icon(icon, size: 28),
              const SizedBox(height: 8),
              Text(
                label,
                style: const TextStyle(fontSize: 12),
              ),
            ],
          ),
          if (count > 0)
            Positioned(
              top: -4,
              right: -8,
              child: Container(
                padding: const EdgeInsets.all(4),
                decoration: const BoxDecoration(
                  color: Colors.red,
                  shape: BoxShape.circle,
                ),
                constraints: const BoxConstraints(
                  minWidth: 18,
                  minHeight: 18,
                ),
                child: Text(
                  count > 99 ? '99+' : count.toString(),
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildMenuItem(
    BuildContext context, {
    required IconData icon,
    required String title,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      trailing: const Icon(Icons.chevron_right),
      onTap: onTap,
    );
  }

  void _showLogoutDialog(BuildContext context, AuthProvider authProvider) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('退出登录'),
        content: const Text('确定要退出登录吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              authProvider.logout();
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('已退出登录'),
                  duration: Duration(seconds: 2),
                ),
              );
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }
}
