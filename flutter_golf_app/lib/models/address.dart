class Address {
  final String id;
  final String name;
  final String phone;
  final String province;
  final String city;
  final String district;
  final String detail;
  final bool isDefault;

  Address({
    required this.id,
    required this.name,
    required this.phone,
    required this.province,
    required this.city,
    required this.district,
    required this.detail,
    this.isDefault = false,
  });

  String get fullAddress => '$province$city$district$detail';

  factory Address.fromJson(Map<String, dynamic> json) {
    return Address(
      id: json['id'],
      name: json['name'],
      phone: json['phone'],
      province: json['province'],
      city: json['city'],
      district: json['district'],
      detail: json['detail'],
      isDefault: json['isDefault'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'phone': phone,
      'province': province,
      'city': city,
      'district': district,
      'detail': detail,
      'isDefault': isDefault,
    };
  }
}
