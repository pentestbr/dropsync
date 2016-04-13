import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brsc2909 on 4/9/16.
 * reads your googlechrome bookmarks and extracts the links fom the dropshipping folder
 */
class ReadBookmarks {
    static List<String> read(String folder) {
        List<String> items = new ArrayList<>();
        String currentUsersHomeDir = System.getProperty("user.home");
        System.out.println(currentUsersHomeDir);

        JsonParser parser = new JsonParser();

        try {
            Object obj = parser.parse(new FileReader(
                    currentUsersHomeDir + "/.config/google-chrome/Default/Bookmarks"));

            JsonObject jsonObject = (JsonObject) obj;

            JsonArray j = jsonObject.getAsJsonObject("roots")
                    .getAsJsonObject("bookmark_bar")
                    .getAsJsonArray("children");

            for (int i = 0; i < j.size(); i++) {
                JsonObject x = j.get(i).getAsJsonObject();
                String type = x.get("type").getAsJsonPrimitive().getAsString();
                String name = x.get("name").getAsJsonPrimitive().getAsString();
                if (type.equalsIgnoreCase("folder") && name.equalsIgnoreCase(folder)) {
                    JsonArray bookmark = x.getAsJsonArray("children");

                    for (int ii = 0; ii < bookmark.size(); ii++) {
                        String xx = bookmark.get(ii).getAsJsonObject()
                                .get("url").getAsString();
                        items.add(xx);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
