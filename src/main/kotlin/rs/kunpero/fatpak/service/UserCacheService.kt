package rs.kunpero.fatpak.service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.users.UsersListRequest
import com.slack.api.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import javax.annotation.PostConstruct

@Service
class UserCacheService(
    private val methodsClient: MethodsClient,
    @Value("\${slack.access.token}")
    private val accessToken: String
) {
    companion object {
        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(UserCacheService::class.java)

        @JvmStatic
        private lateinit var userCache: MutableMap<String, User>
    }

    @PostConstruct
    fun init() {
        val members: List<User> = retrieveUserList()
        userCache = members.stream()
            .filter { user: User -> !user.isDeleted }
            .collect(
                Collectors.toConcurrentMap({ user: User -> "@${user.name}" }) { user: User -> user })
        log.info("Initialized userCache: [{}]", userCache)
    }

    fun getUserIdFromCache(username: String): String? {
        val user: User = tryToFind(username) ?: return null
        return user.id
    }

    fun getUser(username: String): User? {
        return tryToFind(username)
    }

    private fun tryToFind(username: String): User? {
        val user: User? = userCache[username]
        if (user?.id.isNullOrEmpty()) {
            log.info("Username [{}] was not found in cache. Try to find it again using slack-api", username)
            val optionalUser: Optional<User> = retrieveUserList().stream()
                .filter { u: User -> (username == u.name) }
                .findFirst()
            return if (optionalUser.isPresent) {
                userCache[username] = optionalUser.get()
                optionalUser.get()
            } else {
                log.warn("No username [{}] was found in the workspace", username)
                null
            }
        }
        return user
    }

    private fun retrieveUserList(): List<User> {
        val request = UsersListRequest.builder()
            .token(accessToken)
            .build()
        return methodsClient.usersList(request).members
    }
}