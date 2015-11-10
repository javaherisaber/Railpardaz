package ir.androjavan.railpardaz.net.bundle;

public class BundleLog {

	public String tagName;
	public String logBody;
	public String logInfo;
	public String dateTime;
	public String lineNumber;

	public BundleLog(String tagname,String logbody,String loginfo) {
		
		this.tagName = tagname;
		this.logBody = logbody;
		this.logInfo = loginfo;
	}
}
