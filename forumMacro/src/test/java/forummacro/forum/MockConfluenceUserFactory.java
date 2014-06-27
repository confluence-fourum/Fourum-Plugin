package forummacro.forum;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import org.apache.commons.lang.RandomStringUtils;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Generates mock instances of {@link ConfluenceUser}.
 * Source: https://answers.atlassian.com/questions/298030/confluenceuser-unittesting
 */
public class MockConfluenceUserFactory {
    /**
     * Generates a mock user with a random user key and user name. There is no guarantee that the generated user won't
     * clash with a user who's already been created, although the chances of this are quite slim.
     */
    public static ConfluenceUser mockUser() {
        return mockUser(getRandomUserKey(), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10) + "@example.com");
    }

    public static ConfluenceUser mockUser(final String userKey, final String username, final String displayName, final String email) {
        ConfluenceUser user = mock(ConfluenceUser.class);
        when(user.getKey()).thenReturn(new UserKey(userKey));
        when(user.getEmail()).thenReturn(email);
        when(user.getName()).thenReturn(username);
        when(user.getFullName()).thenReturn(displayName);
        return user;
    }

    private static String getRandomUserKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}