package restapi.Serializables;

import forummacro.forum.ao.AttachmentAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rik on 11/06/2014.
 */
@XmlRootElement
public class SerializableAttachment {
    private String name;
    private String url;

    private SerializableAttachment(String name, String url) {
        this.name = name;
        this.url = url;
    }

    private SerializableAttachment() {
    }

    public static SerializableAttachment build(AttachmentAO attachment) {
        return new SerializableAttachment(attachment.getName(), attachment.getUrl());
    }

    public static SerializableAttachment build() {
        return new SerializableAttachment();
    }

    public static SerializableAttachment[] build(AttachmentAO[] attachments) {
        if (attachments.length == 0 || attachments == null) {
            return new SerializableAttachment[]{};
        }
        List<SerializableAttachment> serializableAttachments = new ArrayList<SerializableAttachment>();
        for (AttachmentAO attachment : attachments) {
            serializableAttachments.add(build(attachment));
        }
        return serializableAttachments.toArray(new SerializableAttachment[]{});
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SerializableAttachment so = (SerializableAttachment) obj;
        return (this.name == so.name && this.url == so.url);
    }
}
