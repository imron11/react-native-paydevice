package com.reactlibrary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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
    private PrintTask mPrintTask = null;
    private Printer mPrinter = null;

    private static final String KEY_PRINTING_RESULT = "printing_result";
    private static final String KEY_PRINTER_UPDATE = "printer_update_result";

    //Message type
    private static final int MSG_PRINTING_RESULT = 1;
    private static final int MSG_PRINTER_UPDATE_START = 2;
    private static final int MSG_PRINTER_UPDATE_DONE = 3;
    private static final int MSG_PRINTER_SET_BMP_NVRAM = 4;
    private static final int MSG_PRINTER_DEL_BMP_NVRAM = 5;

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

    /**
     * @brief Printing task class
     */
    private class PrintTask extends AsyncTask<PosSalesSlip, Void, Boolean> {
        protected Boolean doInBackground(PosSalesSlip... templates) {
            if (isCancelled()) {
                return false;
            }
            try {
                if (templates[0].validate()) {
                    templates[0].print();
                } else {
                    Log.d(TAG, "mTemplate data illegal!");
                    return false;
                }
            } catch (SmartPosException e) {
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_PRINTING_RESULT, result);
            Message msg = new Message();
            msg.what = MSG_PRINTING_RESULT;
            msg.setData(bundle);
        }
    }

    @ReactMethod
    public void checkPrinter(Callback callback) {
        initDevice();
        if (mTemplate == null) {
            mTemplate = new PosSalesSlip(this.reactContext, mPrinterManager);
        }
        int err = mTemplate.prepare();
        callback.invoke(err);
    }

    @ReactMethod
    public void setMainLogoToPrint(String mainLogoToPrint){
        mTemplate.setMainLogoToPrint(mainLogoToPrint);
    }

    @ReactMethod
    public void setTextToPrint(String txtToPrint){
        mTemplate.setTxtToPrint(txtToPrint);
    }

    @ReactMethod
    public void setFooterLogoToPrint(String footerLogoToPrint) {
        mTemplate.setFooterLogoToPrint(footerLogoToPrint);
    }

    @ReactMethod
    public void setTxtFooterToPrint(String txtFooterToPrint) {
        mTemplate.setTxtFooterToPrint(txtFooterToPrint);
    }

    @ReactMethod
    public void  PrintText() {
        initDevice();
        if (mTemplate == null) {
            mTemplate = new PosSalesSlip(this.reactContext, mPrinterManager);
        }
        int err = mTemplate.prepare();
        if (err == 0) {
            mPrintTask = new PrintTask();
            mPrintTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTemplate);
        } else {
            Log.v(TAG, String.valueOf(err));
        }
    }
}
