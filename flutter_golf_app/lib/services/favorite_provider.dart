import 'package:flutter/foundation.dart';
import '../models/product.dart';

class FavoriteProvider extends ChangeNotifier {
  final List<Product> _favorites = [];

  List<Product> get favorites => _favorites;

  bool isFavorite(String productId) {
    return _favorites.any((p) => p.id == productId);
  }

  void toggleFavorite(Product product) {
    if (isFavorite(product.id)) {
      _favorites.removeWhere((p) => p.id == product.id);
    } else {
      _favorites.add(product);
    }
    notifyListeners();
  }

  void removeFavorite(String productId) {
    _favorites.removeWhere((p) => p.id == productId);
    notifyListeners();
  }
}
