import '../models/product.dart';

class ProductService {
  // 模拟产品数据
  static List<Product> getMockProducts() {
    return [
      Product(
        id: '1',
        name: 'TaylorMade M6 一号木',
        description: '全新 TaylorMade M6 系列一号木，采用 Speed Injection 技术，提供卓越的距离和容错性。',
        price: 2999.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '一号木',
        brand: 'TaylorMade',
        images: [
          'https://via.placeholder.com/400x300',
          'https://via.placeholder.com/400x300/ff0000',
          'https://via.placeholder.com/400x300/00ff00',
        ],
        rating: 4.8,
        reviewCount: 128,
      ),
      Product(
        id: '2',
        name: 'Callaway Epic Flash 一号木',
        description: 'Callaway Epic Flash 采用人工智能设计的 Flash Face 技术，带来更快的球速。',
        price: 3299.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '一号木',
        brand: 'Callaway',
        images: [
          'https://via.placeholder.com/400x300',
          'https://via.placeholder.com/400x300/0000ff',
        ],
        rating: 4.9,
        reviewCount: 256,
      ),
      Product(
        id: '3',
        name: 'Titleist TS2 一号木',
        description: 'Titleist TS2 一号木，超薄钛合金杆面，提供卓越的速度和容错性。',
        price: 3599.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '一号木',
        brand: 'Titleist',
        images: ['https://via.placeholder.com/400x300'],
        rating: 4.7,
        reviewCount: 89,
      ),
      Product(
        id: '4',
        name: 'PING G410 Plus 一号木',
        description: 'PING G410 Plus 采用锻造杆面技术，可调节的配重系统，满足不同击球习惯。',
        price: 3199.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '一号木',
        brand: 'PING',
        images: ['https://via.placeholder.com/400x300'],
        rating: 4.6,
        reviewCount: 67,
      ),
      Product(
        id: '5',
        name: 'Mizuno JPX 919 铁杆组',
        description: 'Mizuno JPX 919 铁杆组，采用 Chromoly 4140M 钢材，提供卓越的手感和距离。',
        price: 5999.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '铁杆',
        brand: 'Mizuno',
        images: ['https://via.placeholder.com/400x300'],
        rating: 4.9,
        reviewCount: 145,
      ),
      Product(
        id: '6',
        name: 'Odyssey White Hot 推杆',
        description: 'Odyssey White Hot 推杆，经典的白色嵌入式杆面，提供柔软的击球感和出色的滚动。',
        price: 1299.00,
        imageUrl: 'https://via.placeholder.com/400x300',
        category: '推杆',
        brand: 'Odyssey',
        images: ['https://via.placeholder.com/400x300'],
        rating: 4.8,
        reviewCount: 234,
      ),
    ];
  }

  static Future<List<Product>> getProducts({String? category}) async {
    // 模拟网络延迟
    await Future.delayed(const Duration(milliseconds: 500));

    var products = getMockProducts();

    if (category != null && category.isNotEmpty) {
      products = products.where((p) => p.category == category).toList();
    }

    return products;
  }

  static Future<Product?> getProductById(String id) async {
    await Future.delayed(const Duration(milliseconds: 300));

    final products = getMockProducts();
    try {
      return products.firstWhere((p) => p.id == id);
    } catch (e) {
      return null;
    }
  }

  static List<String> getCategories() {
    return ['一号木', '球道木', '铁杆', '铁木杆', '推杆', '挖起杆'];
  }
}
