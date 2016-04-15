/**
 * Created by brsc2909 on 4/5/16.
 * this class grabs an item from a dropshipping website and extracts
 * all of the necessary detailsMap used for reselling
 */

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class GetItem {
    static List<String> pictures = new ArrayList<>();
    static Map<Object, Object> specificsMap = new HashMap<>();
    static Map detailsMap = new HashMap<>();
    static Map<Object, Object> generalInfo = new HashMap<>();
    static Map ShippingMap = new HashMap<>();


    GetItem(String username, String password) throws IOException {
        List<String> itemURL = ReadBookmarks.read("dropshipping");

        for (int index = 0; index < itemURL.size(); index++) {
            System.out.println("item " + index + " " + itemURL.get(index));

            String BROWSER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";

            Document itemPage = Jsoup.connect(itemURL.get(index))
                    .timeout(0).userAgent(BROWSER_AGENT)//.cookies(loginCookie)
                    .get();
            /*getDetails(itemPage);
            getSpecs(itemPage);
            getGeneralInfo(itemPage);
            */
            getShippingInfo(itemPage);
        }

    }

    private void getShippingInfo(Document page) {
        Element shipping = page.getElementById("shippingAndPayment");
        System.out.println(shipping);
    }

    /*
    * grab all the general info from the web page and populate the generalinfo hash map
    * with the info
    */
    private void getGeneralInfo(Document page) {
        Element itemHeading = page.getElementsByTag("h1").get(0);
        String itemPrice = page.getElementsByClass("js-wholesale-list")
                .get(0).getElementsByAttribute("price")
                .get(0).attr("price");

        String inventoryQtyNum_new = "-";
        try {
            inventoryQtyNum_new = page.getElementById("inventoryQtyNum_new").html();
        } catch (Exception e) {
            System.out.println("qty unknown");
            ;
        }
        String lot = "-";
        if (page.getElementsByClass("lot-pie").hasText()) {
            lot = page.getElementsByClass("lot-pie").html();
        }
        float price = Float.parseFloat(itemPrice);
        generalInfo.put("$price", price);
        generalInfo.put("$rrp", String.format("%.2f", (price * 1.25)));

        generalInfo.put("title", itemHeading);
        generalInfo.put("in_stock", inventoryQtyNum_new);
        generalInfo.put("set", lot.replace("&nbsp;", ""));
        generalInfo.put("item_id", detailsMap.get("Item Code"));

        System.out.println("sku: " + detailsMap.get("Item Code"));
        System.out.println("set: " + generalInfo.get("set"));
        System.out.println("in stock: " + generalInfo.get("in_stock"));
        System.out.println("cost: $" + generalInfo.get("$price"));
        System.out.println("rrp $" + generalInfo.get("$rrp"));


    }

    /*
    *grab all the items from the details section and populate the detailsMap hashmap
    *with the info
    */
    private String getDetails(Document page) throws IOException {

        //set up regex to find each detail item
        String detailsPattern = "<li><strong>(.+?): </strong>(.+?)</li>";
        Pattern details_p = Pattern.compile(detailsPattern);
        Matcher detailsMatch;


        Elements detailsElements = page.getElementById("itemDescription")
                .getElementsByTag("li");
        Elements itemPics = page.getElementById("simgList").getElementsByTag("li");
        for (Element adetailsElements : detailsElements) {
            detailsMatch = details_p.matcher(adetailsElements.toString());
            if (detailsMatch.find()) {
                detailsMap.put(detailsMatch.group(1), detailsMatch.group(2));
            }
        }

        String sku_num;
        sku_num = (String) detailsMap.get("Item Code");

        for (int x = 0; x < itemPics.size(); x++) {
            String j = Integer.toString(x);
            String pic = itemPics.get(x).attr("b-init");
            pictures.add(DLPic(pic, sku_num, j));
        }
        return "sku = " + sku_num;
    }

    private String getSpecs(Document page) {

        String specsPattern = "<li><strong>(.+?): </strong> <span class=\"des-wrap\"> <b>(.+?)</b> </span> </li>";
        Pattern specs_p = Pattern.compile(specsPattern);
        Matcher specsMatch;
        try {
            Element itemDetails = page.getElementById("itemDescription");
            Elements specifics = itemDetails.getElementsByClass("item-specifics").get(0).getElementsByTag("li");
            for (Element aspecifics : specifics) {
                specsMatch = specs_p.matcher(aspecifics.toString());
                if (specsMatch.find()) {
                    specificsMap.put(specsMatch.group(1), specsMatch.group(2));
                }
            }
        } catch (Exception e) {
            System.out.println("no specs");
        }

        return "";
    }

    private String DLPic(String url, String sku, String i) throws IOException {
        String currentUsersHomeDir = System.getProperty("user.home");
        //Open a URL Stream
        Connection.Response resultImageResponse = Jsoup.connect(url)//.cookies(cookies)
                .ignoreContentType(true).execute();

        String folder = currentUsersHomeDir + "/dropshipping/pics/" + sku + "/";
        Path path = Paths.get(folder);
        Files.createDirectories(path);

        // output here
        FileOutputStream out = (
                new FileOutputStream(
                        new java.io.File(folder + sku + "-" + i + ".jpg")));
        out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
        out.close();
        return folder + sku + "-" + i;
    }

    public static Map<String, String> login(String username, String password, String BA) throws IOException {
        String epochTime = String.valueOf(System.currentTimeMillis() / 1000);
        String cbi = ":SecuritySettings::CookieEn:true,HighSec:false;FD::SWidth:1366,SHeight:768,CWidth:1286,CHeight:734,ColorDepth:24,DeviceXDPI:undefined,DeviceYDPI:undefined,LogicalXDPI:undefined,LogicalYDPI:undefined,fontSEn:undefined;PlugIn::Flash:true|21.0 r0,AcrobatReader:0,WinMessenger:0,Silverlight:0,Director:0,QuickTime:0,MediaPlayer:0,SVGViewer:0,IPIXViewer:0,CrystalReport:0,Viewpoint:true|,MapGuide:0,Citrix:0;JavaInf::JavaEn:false,JSBuild:Not tested,VBSBuild:Not tested,XMLVer:-1,AJAXSup:true,MsJVMBuild:;sysLan::;userLan::;CDateTime::"
                + GetTimes.nowTime("yyyy-MM-dd HH:mm:ss") + ";TimeZone::-7";
        String returnUrl = "http%3A%2F%2Fwww.dhgate.com%2F";
        Connection.Response loginForm = Jsoup.connect("https://secure.dhgate.com/usr/signin.do")
                .data("CA001", "")
                .data("CA002", "")
                .data("act", "login")
                .data("returnURL", returnUrl)
                .data("cbi", cbi)
                .data("username", username)
                .data("password", password)
                .data("remember", "true")
                .data("smt_signin", "Sign in")
                .referrer("http://www.google.com").userAgent(BA)
                .method(Connection.Method.POST).execute();
        System.out.println(loginForm.parse());
        // hold onto the cookie
        return loginForm.cookies();
    }
}
