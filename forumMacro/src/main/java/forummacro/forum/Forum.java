package forummacro.forum;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import forummacro.forum.ao.ForumAO;
import forummacro.rightsmanagement.RightsManagement;
import forummacro.rightsmanagement.ao.PermissionAO;
import net.java.ao.DBParam;

import java.util.Map;

/**
 * The macro main class When the macro is added this class will be called first
 *
 * @author Nico Smolders
 */
public class Forum extends BaseMacro implements Macro, IForum {
    private final ActiveObjects ao;
    private final RightsManagement rightsManagement;
    private final String forumTitel;
    private ForumAO forum = null;

    /**
     * The forum constructor.
     *
     * @param ao
     * @param rightsManagement
     */
    public Forum(ActiveObjects ao, RightsManagement rightsManagement) {
        this.ao = ao;
        this.rightsManagement = rightsManagement;
        this.forumTitel = "forum_title";
    }

    /**
     * The execute class, executes when the plugin is created.
     *
     * @param parameters
     * @param bodyContext
     * @param renderContext
     */
    @Override
    public String execute(Map parameters, String bodyContext,
                          RenderContext renderContext) throws MacroException {
        /**
         * Checks if Forum doesn't exsist. If it doenst it creates a new one,
         * Else it uses the one that already exsist.
         */
        Map context = MacroUtils.defaultVelocityContext();
        if (ao.find(ForumAO.class, "TITLE = ?",
                (String) parameters.get(forumTitel)).length == 0) {
            if (hasEmptyTitle(parameters)) {
                context.put("title", "Demo Forum");
                context.put("description", "This is a Demo of the Forum Macro");
                context.put("forumID", "-1");
                return VelocityUtils.getRenderedTemplate("templates/body.vm", context);
            }
            forum = createNewForum(parameters);
        } else {
            forum = getExistingForum(parameters);
        }
        context.put("title", forum.getTitle());
        context.put("description", forum.getDescription());
        context.put("forumID", forum.getID());
        return VelocityUtils.getRenderedTemplate("templates/body.vm", context);
    }

    private ForumAO getExistingForum(Map parameters) {
        ForumAO[] forums = ao.find(ForumAO.class, "TITLE = ?",
                (String) parameters.get(forumTitel));
        return forums[0];
    }

    private ForumAO createNewForum(Map parameters) {
        ForumAO newForum = ao.create(ForumAO.class, new DBParam("TITLE", "title"));
        newForum.setTitle((String) parameters.get(forumTitel));
        newForum.setDescription((String) parameters.get("forum_description"));
        newForum.save();
        // Initialize permissions if not yet done and add current user as
        // admin
        if (ao.find(PermissionAO.class).length == 0) {
            rightsManagement.initPermissions();
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        rightsManagement.initAdmin(currentUser.getKey().getStringValue(), newForum.getID());
        rightsManagement.initDeletedUserRole(newForum.getID());
        return newForum;
    }

    private boolean hasEmptyTitle(Map parameters) {
        if ("".equals((String) parameters.get(forumTitel)) || parameters.get(forumTitel) == null) {
            return true;
        }
        return false;
    }

    /**
     * The execute class, executes when the plugin is created.
     *
     * @param parameters
     * @param bodyContent
     * @param conversionContext
     */
    @Override
    public String execute(Map<String, String> parameters, String bodyContent,
                          ConversionContext conversionContext) throws MacroExecutionException {
        try {
            DefaultConversionContext defaultConversionContext = (DefaultConversionContext) conversionContext;
            RenderContext renderContext = defaultConversionContext
                    .getPageContext();
            return execute(parameters, bodyContent, renderContext);
        } catch (MacroException e) {
            throw new MacroExecutionException(e);
        }
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public ForumAO getForum() {
        return forum;
    }
}