package restapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import restapi.Serializables.SerializableAttachment;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Rik on 10/06/2014.
 */

public class Sanitizer {
    /**
     * Sanitizes a unsafe string using Jsoup, removes all html
     *
     * @param unsafe the string that needs sanitization
     * @return the sanitized input
     */
    public String sanitizeInput(String unsafe) {
        return Jsoup.clean(unsafe, Whitelist.none());
    }

    /**
     * Calls sanitizeInput for a list of strings
     *
     * @param unsafeStrings
     * @return sanitized list
     */
    public List<String> sanitizeInput(List<String> unsafeStrings) {
        List<String> safeStrings = new ArrayList<String>();
        for (String unsafe : unsafeStrings) {
            safeStrings.add(sanitizeInput(unsafe));
        }
        return unsafeStrings;
    }

    public List<String> parseJson(String json) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        List<String> outputStrings = new ArrayList<String>();
        for (JsonElement element : newArrayList(jsonArray.iterator())) {
            outputStrings.add(element.getAsJsonObject().get("name").getAsString());
        }
        return sanitizeInput(outputStrings);
    }

    public List<SerializableAttachment> parseAttachmentJson(String json) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        List<SerializableAttachment> serializableAttachments = new ArrayList<SerializableAttachment>();
        for (JsonElement element : newArrayList(jsonArray.iterator())) {
            JsonObject attachment = element.getAsJsonObject();
            String name = sanitizeInput(attachment.get("name").getAsString());
            String url = sanitizeInput(attachment.get("url").getAsString());
            SerializableAttachment sAttachment = SerializableAttachment.build();
            sAttachment.setName(name);
            sAttachment.setUrl(url);
            serializableAttachments.add(sAttachment);
        }
        return serializableAttachments;
    }
}
