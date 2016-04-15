import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.util.Arrays.asList;

/**
 * Created by brsc2909 on 4/9/16.
 * reads your googlechrome bookmarks and extracts the links fom the dropshipping folder
 */
class MongoDBConnect {
    private static MongoDatabase connect(String database) {
        MongoClient mongoClient = new MongoClient();
        return mongoClient.getDatabase(database);
    }

    //search the database
    static void searchdb() {
        FindIterable<Document> iterable = connect("dropshipping").getCollection("inventory")
                .find(new Document("category", "Electronics"));

        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });
    }

    // add items to the database
    public void addData(MongoDatabase db) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        Document details = new Document();
        for (Object key : GetItem.detailsMap.keySet()) {
            Object value = GetItem.detailsMap.get(key);
            details.append(key.toString(), value.toString());
            System.out.println(key + " : " + value);
        }
        Document specs = new Document();
        for (Object key : GetItem.specificsMap.keySet()) {
            Object value = GetItem.specificsMap.get(key);
            specs.append(key.toString(), value.toString());
            System.out.println(key + " : " + value);
        }

        db.getCollection("inventory").insertOne(
                new Document("item",
                        new Document()
                                .append("make", "2 Avenue")
                                .append("model", "10075")
                                .append("&price", GetItem.generalInfo.get("$price"))
                                .append("&rrp", GetItem.generalInfo.get("$rrp"))
                )
                        .append("category", "")
                        .append("subcategory", "")
                        .append("information", asList(
                                new Document()
                                        .append("shipping", asList(
                                                new Document()
                                                        .append("location", "")
                                                        .append("cost", "A")
                                                        .append("delivery_time", 11))

                                        )
                                        .append("specifics", asList().add(specs)
                                        )
                                        .append("detailsMap", asList().add(details)
                                        )
                                        .append("pictures", asList().add(GetItem.pictures))
                                )
                        )
                        .append("title", GetItem.generalInfo.get("title"))
                        .append("in_stock", GetItem.generalInfo.get("in_stock"))
                        .append("set", GetItem.generalInfo.get("set"))
                        .append("item_id", "41704620"));
    }
}
