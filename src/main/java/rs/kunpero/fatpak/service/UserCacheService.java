package rs.kunpero.fatpak.service;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.UsersListRequest;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toConcurrentMap;

@Service
@Slf4j
public class UserCacheService {
    private static Map<String, User> userCache;

    private final MethodsClient methodsClient;

    public UserCacheService(MethodsClient methodsClient) {
        this.methodsClient = methodsClient;
    }

    @Value("${slack.access.token}")
    private String accessToken;

    @PostConstruct
    public void init() throws IOException, SlackApiException {
        final List<User> members = retrieveUserList();
        userCache = members.stream()
                .filter(user -> !user.isDeleted())
                .collect(toConcurrentMap(user -> "@" + user.getName(), user -> user));
        log.info("Initialized userCache: [{}]", userCache);
    }

    public String getUserIdFromCache(final String username) throws IOException, SlackApiException {
        final User user = tryToFind(username);

        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public User getUser(String username) throws IOException, SlackApiException {
        return tryToFind(username);
    }

    private User tryToFind(String username) throws IOException, SlackApiException {
        final User user = userCache.get(username);

        if (user == null || user.getId() == null) {
            log.info("Username [{}] was not found in cache. Try to find it again using slack-api", username);
            final List<User> members = retrieveUserList();
            Optional<User> optionalUser = members.stream()
                    .filter(u -> username.equals(u.getName()))
                    .findFirst();
            if (optionalUser.isPresent()) {
                userCache.put(username, optionalUser.get());
                return optionalUser.get();
            } else {
                log.warn("No username [{}] was found in the workspace", username);
                return null;
            }
        }
        return user;
    }

    private List<User> retrieveUserList() throws IOException, SlackApiException {
        UsersListRequest request = UsersListRequest.builder()
                .token(accessToken)
                .build();
        UsersListResponse response = methodsClient.usersList(request);

        return response.getMembers();
    }
}
