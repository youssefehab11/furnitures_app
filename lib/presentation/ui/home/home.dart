import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:furnitures_app/core/utils/dialogs.dart';
import 'package:permission_handler/permission_handler.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  static const platformMethodChannel = MethodChannel('flutter_android_channel');

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool isARSupported = false;

  @override
  void initState() {
    super.initState();
    _checkARAvailability();
  }

  void _launchAndroidActivity() {
    HomeScreen.platformMethodChannel.invokeMethod(
      'launchAR',
      'Hello from Flutter',
    );
  }

  void _checkARAvailability() async {
    isARSupported = await HomeScreen.platformMethodChannel.invokeMethod(
      'checkARSupport',
    );
    print(isARSupported);
    setState(() {});
  }

  Future<void> _requestPermission(BuildContext context) async {
    final permission = Permission.camera;
    final result = await permission.request();
    if (result.isGranted) {
      _launchAndroidActivity();
    } else if (result.isPermanentlyDenied) {
      if (context.mounted) {
        showMessageDialog(
          context: context,
          message: 'AR Feature requires camera permission',
          positiveActionText: 'Ok',
          title: 'Camera Permission',
          negativeActionText: 'Cancel',
          onPositiveActionPressed: () => openAppSettings(),
        );
      }
    } else {
      if (context.mounted) {
        showMessageDialog(
          context: context,
          message: 'AR Feature requires camera permission',
          positiveActionText: 'Ok',
          title: 'Camera Permission',
          onPositiveActionPressed: () => _requestPermission(context),
        );
      }
    }
  }

  void _onStartARClicked(BuildContext context) async {
    PermissionStatus cameraPermissionStatus = await Permission.camera.status;
    if (!cameraPermissionStatus.isGranted) {
      if (context.mounted) _requestPermission(context);
    } else {
      _launchAndroidActivity();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: ElevatedButton(
          onPressed: isARSupported ? () => _onStartARClicked(context) : null,
          child: Text('Start AR'),
        ),
      ),
    );
  }
}
