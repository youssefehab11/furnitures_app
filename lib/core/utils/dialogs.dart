import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

void showMessageDialog({
  required BuildContext context,
  required String title,
  required String message,
  required String positiveActionText,
  String? negativeActionText,
  VoidCallback? onPositiveActionPressed,
  VoidCallback? onNegativeActionPressed,
}) {
  List<Widget> actions = [
    MaterialButton(
      onPressed: () {
        hideDialog(context);
        onPositiveActionPressed?.call();
      },
      child: Text(positiveActionText),
    ),
    if (negativeActionText != null)
      MaterialButton(
        onPressed: () {
          hideDialog(context);
          onNegativeActionPressed?.call();
        },
        child: Text(negativeActionText),
      ),
  ];
  showDialog(
    context: context,
    barrierDismissible: false,
    builder: (context) {
      return CupertinoAlertDialog(
        title: Text(title),
        content: Text(message),
        actions: actions,
      );
    },
  );
}

void hideDialog(BuildContext context) {
  Navigator.of(context).pop();
}
