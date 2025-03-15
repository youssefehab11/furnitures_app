import 'package:flutter/material.dart';
import 'package:furnitures_app/presentation/ui/details/details.dart';
import 'package:furnitures_app/presentation/ui/home/home.dart';

void main() {
  runApp(FurnituresApp());
}

class FurnituresApp extends StatelessWidget {
  const FurnituresApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      routes: {
        '/': (context) => HomeScreen(),
        '/detailsRoute': (context) => DetailsScreen(),
      },
    );
  }
}
