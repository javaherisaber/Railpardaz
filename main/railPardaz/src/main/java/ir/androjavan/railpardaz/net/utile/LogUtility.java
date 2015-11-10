package ir.androjavan.railpardaz.net.utile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ir.androjavan.railpardaz.net.R;
import ir.androjavan.railpardaz.net.bundle.BundleLog;
import ir.androjavan.railpardaz.net.database.StatisticsDB;

@SuppressLint("SimpleDateFormat")
public class LogUtility {

	Context context;
	
	public LogUtility(Context ctx) 
	{
		this.context = ctx;
	}
	
	public void InsertLog(BundleLog bunLog)
	{
		Log.e(bunLog.tagName, bunLog.logBody+bunLog.logInfo);
		setLog(bunLog);
	}

	private void setLog(BundleLog bundleLog) {
		
		bundleLog.dateTime = getCurrentDateTime();
        bundleLog.lineNumber = String.valueOf(getLineNumber());
		StatisticsDB db = new StatisticsDB(context);
		db.open();
		db.insertLog(bundleLog);
		db.close();

        writeToFile(bundleLog);
	}

    private int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    private String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        return df.format(Calendar.getInstance().getTime());
    }

    public void writeToFile(BundleLog bundleLog)
    {
        String logBuilder = "####"+bundleLog.tagName+"####\n" + "@Line : " + bundleLog.lineNumber
                + "\nBody : " + bundleLog.logBody + "\nInfo : " + bundleLog.logInfo + "\nDate : " + bundleLog.dateTime;
        // Get the directory for the user's public pictures directory.
        final File path = Environment.getExternalStoragePublicDirectory(Environment.getDataDirectory() + "/"+getLogFolderName()+"/");

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, getLogFileName()+".txt");

        // Save your stream, don't forget to flush() it before closing it.
        if(file.exists()) {
            try {
                FileOutputStream fOut = new FileOutputStream(file,true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write("\n****************************************\n"+logBuilder);

                myOutWriter.close();

                fOut.flush();
                fOut.close();

                updateLogInfo(bundleLog);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                file.createNewFile();

                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                String header = "#"+getLogFileName()+" Android application log file ::: \n\n\n";
                myOutWriter.write(header+logBuilder);

                myOutWriter.close();

                fOut.flush();
                fOut.close();

                updateLogInfo(bundleLog);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLogInfo(BundleLog bundleLog)
    {
        StatisticsDB db = new StatisticsDB(context);
        db.open();
        db.updateLog(bundleLog);
        db.close();
    }

    private String getLogFileName()
    {
        return context.getResources().getString(R.string.log_file_name);
    }

    private String getLogFolderName()
    {
        return context.getResources().getString(R.string.log_folder_name);
    }
	
}
