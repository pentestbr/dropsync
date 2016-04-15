import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brsc2909 on 4/9/16.
 * reads your googlechrome bookmarks and extracts the links fom the dropshipping folder
 */
class GetTimes {
    // tf =  time format
    static String nowTime(String tf) {
        Date date = new Date();
        SimpleDateFormat cbi = new SimpleDateFormat(tf);
        return cbi.format(date);
    }
}
