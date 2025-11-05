import 'package:flutter/foundation.dart';
import '../models/user.dart';

class AuthProvider extends ChangeNotifier {
  User? _user;
  bool _isLoading = false;

  User? get user => _user;
  bool get isAuthenticated => _user != null;
  bool get isLoading => _isLoading;

  Future<bool> login(String email, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      // 模拟 API 调用
      await Future.delayed(const Duration(seconds: 1));

      // 模拟登录成功
      _user = User(
        id: '1',
        username: '毛躁涵',
        email: email,
        phone: '13800138000',
        avatar: 'https://via.placeholder.com/150',
      );

      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> register(String username, String email, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      // 模拟 API 调用
      await Future.delayed(const Duration(seconds: 1));

      // 模拟注册成功
      _user = User(
        id: '1',
        username: username,
        email: email,
      );

      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  void logout() {
    _user = null;
    notifyListeners();
  }
}
