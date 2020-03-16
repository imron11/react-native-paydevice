package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

// SDK PayDevice
import com.paydevice.smartpos.sdk.SmartPosException;
import com.paydevice.smartpos.sdk.printer.PrinterManager;
import com.paydevice.smartpos.sdk.printer.Printer;
import com.paydevice.smartpos.sdk.printer.UsbPrinter;
import com.paydevice.smartpos.sdk.printer.SerialPortPrinter;
import com.paydevice.smartpos.sdk.cashdrawer.CashDrawer;

public class PaydeviceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    // PayDevice Variable
    private Printer mPrinter = null;

    public PaydeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Paydevice";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void testPrinter() {

    }
}
