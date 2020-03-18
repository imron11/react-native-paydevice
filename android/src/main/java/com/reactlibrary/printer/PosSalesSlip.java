package com.reactlibrary.printer;

// SDK PayDevice
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.paydevice.smartpos.sdk.SmartPosException;
import com.paydevice.smartpos.sdk.printer.PrinterManager;

import java.util.Locale;

public class PosSalesSlip {

    private static String TAG = "PosSalesSlip";
    /** Printer object */
    private PrinterManager mPrinterManager;
    /** Application context */
    private Context mContext;

    // var text to print
    private String txtToPrint;

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

    //validate var
    public boolean validate() {
        if (TextUtils.isEmpty(txtToPrint)) {
            return false;
        }
        return true;
    }

    /**
     * @brief print template
     *
     * @return
     */
    synchronized public void print() throws SmartPosException {

        if (mPrinterManager.isBuiltInSlow()) {
            mPrinterManager.cmdSetHeatingParam(7, 140, 10);
        } else {
            mPrinterManager.cmdSetHeatingParam(15, 100, 10);
        }

        printTemplate();

        mPrinterManager.cmdLineFeed(3);
        if (mPrinterManager.getPrinterType() == PrinterManager.PRINTER_TYPE_USB) {
            mPrinterManager.cmdCutPaper(PrinterManager.FULL_CUT);
        }
    }

    //print template
    private void printTemplate() {
        try {
            Locale locale = mContext.getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            boolean leftIsDoubleByte = false;
            if (language.endsWith("zh")) {
                mPrinterManager.cmdSetPrinterLanguage(PrinterManager.CODE_PAGE_GB18030);
                mPrinterManager.setStringEncoding("GB18030");
                leftIsDoubleByte = true;
            } else {
                mPrinterManager.cmdSetPrinterLanguage(PrinterManager.CODE_PAGE_CP437);
                mPrinterManager.setStringEncoding("CP437");
            }

            mPrinterManager.cmdSetPrintMode(PrinterManager.FONT_DOUBLE_HEIGHT
                    |PrinterManager.FONT_DOUBLE_WIDTH
                    |PrinterManager.FONT_EMPHASIZED);
            mPrinterManager.cmdSetAlignMode(PrinterManager.ALIGN_MIDDLE);
            mPrinterManager.cmdLineFeed();
            mPrinterManager.sendData("");
            mPrinterManager.cmdLineFeed();
        } catch (SmartPosException e) {
        }
    }

    //deviding line
    private void printDevidingLine() throws SmartPosException {
        final int totalDots = mPrinterManager.getDotsPerLine();
        int[] pLineStartPosition_58 = {0,192};
        int[] pLineEndPosition_58 = {192,383};
        int[] pLineStartPosition_80 = {0,200,400};
        int[] pLineEndPosition_80 = {200,400,575};
        int height=5;
        while(height-- > 0) {
            try {
                if (totalDots == 384)
                    mPrinterManager.cmdPrintMultipleLines(pLineStartPosition_58.length, pLineStartPosition_58, pLineEndPosition_58);
                else
                    mPrinterManager.cmdPrintMultipleLines(pLineStartPosition_80.length, pLineStartPosition_80, pLineEndPosition_80);
            } catch (SmartPosException e) {
            }
        }
    }

    //getter and setter text to print
    public void setTxtToPrint(String str) {
        this.txtToPrint = str;
    }

    public String getTxtToPrint() {
        return txtToPrint;
    }
}
