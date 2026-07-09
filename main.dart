import 'package:flutter/material.dart';
import 'views/home_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? null}) : super(key: null);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SMS Manager',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const HomeScreen(), // تشغيل شاشة الواجهة التي رتبناها داخل مجلد views
      debugShowCheckedModeBanner: false,
    );
  }
}
