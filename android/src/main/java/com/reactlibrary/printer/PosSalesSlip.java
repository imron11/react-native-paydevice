package com.reactlibrary.printer;

// SDK PayDevice
import android.content.Context;
import android.util.Log;

import com.paydevice.smartpos.sdk.SmartPosException;
import com.paydevice.smartpos.sdk.printer.PrinterManager;

public class PosSalesSlip {

    private static String TAG = "PosSalesSlip";
    /** Printer object */
    private PrinterManager mPrinterManager;
    /** Application context */
    private Context mContext;

    public PosSalesSlip(Context context, PrinterManager printer) {
        this.mContext = context;
        this.mPrinterManager = printer;
    }

    // prepare printer
    public int prepare() {
        try {
            mPrinterManager.connect();
            if (mPrinterManager.getPrinterType() == PrinterManager.PRINTER_TYPE_SERIAL) {
                if (mPrinterManager.cmdGetPrinterModel() == PrinterManager.PRINTER_MODEL_PRN2103) {
                    Log.d(TAG,"model:PRN 2103");
                } else {
                    Log.d(TAG,"model:UNKNOWN");
                }
            }
            mPrinterManager.checkPaper();
        } catch (SmartPosException e) {
            return e.getErrorCode();
        }
        return 0;
    }
}
