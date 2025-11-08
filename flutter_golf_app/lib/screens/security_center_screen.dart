import 'package:flutter/material.dart';
import 'change_password_screen.dart';
import 'change_phone_screen.dart';
import 'change_email_screen.dart';

class SecurityCenterScreen extends StatelessWidget {
  const SecurityCenterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('安全中心'),
      ),
      body: ListView(
        children: [
          _buildSecurityItem(
            context,
            icon: Icons.lock_outline,
            title: '登录密码',
            subtitle: '定期修改密码可保护账户安全',
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const ChangePasswordScreen(),
                ),
              );
            },
          ),
          const Divider(height: 1),
          _buildSecurityItem(
            context,
            icon: Icons.phone_outlined,
            title: '手机号',
            subtitle: '已绑定：138****8000',
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const ChangePhoneScreen(),
                ),
              );
            },
          ),
          const Divider(height: 1),
          _buildSecurityItem(
            context,
            icon: Icons.email_outlined,
            title: '邮箱',
            subtitle: '已绑定：user@example.com',
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const ChangeEmailScreen(),
                ),
              );
            },
          ),
          const SizedBox(height: 24),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '安全提示',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: Colors.grey[800],
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  '• 定期修改密码，不使用简单密码\n'
                  '• 不要在公共场所使用账号\n'
                  '• 绑定手机和邮箱，以便找回密码',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                    height: 1.5,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSecurityItem(
    BuildContext context, {
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Icon(icon, color: Theme.of(context).primaryColor),
      title: Text(title),
      subtitle: Text(subtitle),
      trailing: const Icon(Icons.chevron_right),
      onTap: onTap,
    );
  }
}
