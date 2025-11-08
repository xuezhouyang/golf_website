import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_provider.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _notifications = true;
  bool _promotions = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('设置'),
      ),
      body: ListView(
        children: [
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '通知设置',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: Colors.grey,
              ),
            ),
          ),
          SwitchListTile(
            title: const Text('接收推送通知'),
            subtitle: const Text('订单状态、物流信息等'),
            value: _notifications,
            onChanged: (value) {
              setState(() {
                _notifications = value;
              });
            },
          ),
          SwitchListTile(
            title: const Text('促销活动通知'),
            subtitle: const Text('优惠券、新品上架等'),
            value: _promotions,
            onChanged: (value) {
              setState(() {
                _promotions = value;
              });
            },
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '其他设置',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: Colors.grey,
              ),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.language),
            title: const Text('语言'),
            subtitle: const Text('简体中文'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              // TODO: Language settings
            },
          ),
          ListTile(
            leading: const Icon(Icons.delete_outline),
            title: const Text('清除缓存'),
            subtitle: const Text('12.5 MB'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              _showClearCacheDialog();
            },
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '关于',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: Colors.grey,
              ),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.info_outline),
            title: const Text('关于我们'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              _showAboutDialog();
            },
          ),
          ListTile(
            leading: const Icon(Icons.description_outlined),
            title: const Text('用户协议'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              // TODO: Show user agreement
            },
          ),
          ListTile(
            leading: const Icon(Icons.privacy_tip_outlined),
            title: const Text('隐私政策'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              // TODO: Show privacy policy
            },
          ),
          ListTile(
            leading: const Icon(Icons.star_outline),
            title: const Text('版本信息'),
            subtitle: const Text('v1.0.0'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              // TODO: Check for updates
            },
          ),
          const SizedBox(height: 32),
          if (Provider.of<AuthProvider>(context).isAuthenticated)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: OutlinedButton(
                onPressed: () {
                  _showLogoutDialog();
                },
                style: OutlinedButton.styleFrom(
                  foregroundColor: Colors.red,
                  side: const BorderSide(color: Colors.red),
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
                child: const Text('退出登录'),
              ),
            ),
          const SizedBox(height: 32),
        ],
      ),
    );
  }

  void _showClearCacheDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('清除缓存'),
        content: const Text('确定要清除缓存吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('缓存已清除')),
              );
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _showAboutDialog() {
    showAboutDialog(
      context: context,
      applicationName: '高迷网',
      applicationVersion: 'v1.0.0',
      applicationIcon: Icon(
        Icons.sports_golf,
        size: 48,
        color: Theme.of(context).primaryColor,
      ),
      children: [
        const Text('高尔夫球杆电商平台'),
        const SizedBox(height: 8),
        const Text('为高尔夫爱好者提供优质的球杆和装备'),
      ],
    );
  }

  void _showLogoutDialog() {
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
              Provider.of<AuthProvider>(context, listen: false).logout();
              Navigator.pop(context);
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('已退出登录')),
              );
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }
}
