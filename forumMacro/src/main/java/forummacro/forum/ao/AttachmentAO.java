package forummacro.forum.ao;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;

/**
 * Created by Rik on 11/06/2014.
 */
public interface AttachmentAO extends Entity {
    @NotNull
    public String getName();

    public void setName(String name);

    @NotNull
    public String getUrl();

    public void setUrl(String url);

    public PostAO getPost();

    public void setPost(PostAO post);
}
