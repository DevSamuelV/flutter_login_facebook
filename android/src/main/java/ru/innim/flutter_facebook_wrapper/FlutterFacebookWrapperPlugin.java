package ru.innim.flutter_facebook_wrapper;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class FlutterFacebookWrapperPlugin implements FlutterPlugin, ActivityAware {
    private static final String _CHANNEL_NAME = "flutter_facebook_wrapper";

    private MethodCallHandler _methodCallHandler;
    private ActivityListener _activityListener;
    private CallbackManager _callbackManager;
    private ActivityPluginBinding _activityPluginBinding;
    private LoginCallback _loginCallback;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        final BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        final MethodChannel channel = new MethodChannel(messenger, _CHANNEL_NAME);
        _callbackManager = CallbackManager.Factory.create();
        _loginCallback = new LoginCallback();
        _activityListener = new ActivityListener(_callbackManager);
        _methodCallHandler = new MethodCallHandler(_loginCallback);
        channel.setMethodCallHandler(_methodCallHandler);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        _setActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        _resetActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        _setActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivity() {
        _resetActivity();
    }


    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        _methodCallHandler = null;
        _activityListener = null;
        _callbackManager = null;
        _activityPluginBinding = null;
        _loginCallback = null;
    }

    private void _setActivity(ActivityPluginBinding activityPluginBinding) {
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(_callbackManager, _loginCallback);
        activityPluginBinding.addActivityResultListener(_activityListener);
        _methodCallHandler.updateActivity(activityPluginBinding.getActivity());
    }

    private void _resetActivity() {
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.unregisterCallback(_callbackManager);
        _activityPluginBinding.removeActivityResultListener(_activityListener);
        _activityPluginBinding = null;
        _methodCallHandler.updateActivity(null);
    }
}
