package com.reactlibrary.printer;

// SDK PayDevice
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
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

        printLogo();
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

            mPrinterManager.cmdSetPrintMode(PrinterManager.FONT_DEFAULT);
            mPrinterManager.cmdSetAlignMode(PrinterManager.ALIGN_MIDDLE);
            mPrinterManager.cmdLineFeed();
            mPrinterManager.sendData(txtToPrint);
            mPrinterManager.cmdLineFeed();
        } catch (SmartPosException e) {
        }
    }

    //print image
    private void printLogo() {
        try {
            //NOTE:For seriaport printer,need wait printer buffer flush before bitmap print if bitmap is not the first print
            //try {
            //	Thread.sleep(xxx);
            //} catch(InterruptedException ie) {
            //}

            String encodedImage = "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Sg42RC3btrY24ykyn95RkAYK0tHb2ysXLlyoHDuMq6mpSQ4PDysZ2BKVbI8dOyYnJyetpMh8ekNBGipIR1dXl4zH48rxw7R27NghBwYGlHO3LezZHj9+XI6Pj1tNkflMHQVpsCAdzvNHe/fuVW4j01csFpMXLlyw/o94OmHM1vmp7tatW9OcNRnOZNmaTwrScEE6nIdJDx48kOvXr1duKxPXwYMH5bt375TznA1hybaoqEi2tLTInz9/pj1F5nPmKEghrD3v4xz3+fPn8sSJE7KyslK53SCvxsZG949sv3z5opxXEGRittnZ2XLbtm3uL2L6+/tnPUXm879lSY9XizsXqqd6eVFQORez235pKCfm9+/fi+7ubtHZ2Sl6enrc/zovyDExMaF8frqUlpaK2tpa99K2yspKEY/HRXV1tfXreE0KYrbz588Xa9euFcuXL3fzdPJ1snXyDiLmc2qeCxLmDA4Oum8UPz4+npZvOs43hFgs5r5iTF5envLxMElntkVFRf/PNUwvKB3l+aQgAUCDSw0BQIOCBAANChIANChIANCgIAFAg4IEAA0KEgA0KEgA0KAgAUCDggQADQoSADQoSADQoCABQIOCBICpCCH+BwAtIgD2d9kBAAAAAElFTkSuQmCC";

            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap logo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            final int totalDots = mPrinterManager.getDotsPerLine();
            int xPos = (totalDots - logo.getWidth()) >> 1;//horizontal centre
            int yPos = 0;
            //NOTE: maybe different bitmap needs different heating param
            ////Note: width<dots_per_line. please resize the bmp size to small if printing bmp have issue
            //mPrinterManager.cmdBitmapPrint(logo, PrinterManager.BITMAP_ZOOM_NONE, xPos, 0);
            //mPrinterManager.cmdLineFeed();

            //the more black area of bitmap, the more delay(slower speed)
            mPrinterManager.cmdBitmapPrintEx(logo, xPos, yPos);
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
