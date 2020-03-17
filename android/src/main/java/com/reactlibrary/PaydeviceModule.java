package com.reactlibrary;

import android.util.Log;

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

import com.reactlibrary.printer.PosSalesSlip;

public class PaydeviceModule extends ReactContextBaseJavaModule {

    private static String TAG = "PayDevice";
    private PosSalesSlip mTemplate = null;
    private PrinterManager mPrinterManager = null;

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

    private void initDevice() {
        //check printer for different models
        //Built-in serialport printer: FH070H-A,FH100H-A,FH070A2
        //Built-in usb printer:        FH116A3

        if (mPrinter == null) {
            //if usb printer no found then try serialport printer
            try {
                //80mm USB printer
                mPrinter = new UsbPrinter(this.reactContext);
                mPrinter.selectBuiltInPrinter();
                mPrinter.open();
                mPrinter.close();
            } catch (SmartPosException e) {
                Log.d(TAG,"no usb printer,try serialport printer");
                //58mm serialport printer
                mPrinter = new SerialPortPrinter();
                mPrinter.selectBuiltInPrinter();
            }

            mPrinterManager = new PrinterManager(mPrinter,
                    (mPrinter.getType() == PrinterManager.PRINTER_TYPE_USB)
                            ? PrinterManager.TYPE_PAPER_WIDTH_80MM
                            : PrinterManager.TYPE_PAPER_WIDTH_58MM);
        }
    }

    @ReactMethod
    public void testPrinter() {
        initDevice();
        if (mTemplate == null) {
            mTemplate = new PosSalesSlip(this.reactContext, mPrinterManager);
        }
        int err = mTemplate.prepare();
        Log.v(TAG, "prepare " + String.valueOf(err));
    }
}
