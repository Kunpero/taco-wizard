package rs.kunpero.fatpak.service

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload
import com.slack.api.model.User
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import rs.kunpero.fatpak.util.exception.PayloadParserException

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [UserCacheService::class, PayloadParserService::class])
class PayloadParserServiceTest {
    @MockBean
    lateinit var userCacheService: UserCacheService

    @Autowired
    lateinit var payloadParserService: PayloadParserService

    @Test(expected = PayloadParserException::class)
    fun noUserIdInTheWorkspaceFailTest() {
        val payload = SlashCommandPayload()
        payload.userName = "admin"
        payload.text = "@user4 3"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(true))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn(null)
        payloadParserService.parse(payload)
    }

    @Test
    fun userInCacheSuccessTest() {
        val payload = SlashCommandPayload()
        payload.userId = "user0Id"
        payload.userName = "admin"
        payload.text = "@user4 3 test test test"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(true))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn("user4Id")
        val requestDto = payloadParserService.parse(payload)
        Assert.assertEquals("user0Id", requestDto!!.fromUser)
        Assert.assertEquals("user4Id", requestDto.toUser)
        Assert.assertEquals("test test test", requestDto.commentary)
        Assert.assertEquals(3, requestDto.amount.toLong())
    }


    @Test(expected = NumberFormatException::class)
    fun incorrectAmountFormatFailTest() {
        val payload = SlashCommandPayload()
        payload.text = "@user4 pepe"
        payload.userName = "admin"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(true))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn("user4Id")
        payloadParserService.parse(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun zeroAmountFailTest() {
        val payload = SlashCommandPayload()
        payload.text = "@user4 0"
        payload.userName = "admin"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(true))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn("user4Id")
        payloadParserService.parse(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun negativeAmountFailTest() {
        val payload = SlashCommandPayload()
        payload.text = "@user4 -1"
        payload.userName = "admin"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(true))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn("user4Id")
        payloadParserService.parse(payload)
    }

    @Test(expected = PayloadParserException::class)
    fun commandNotPermittedFailTest() {
        val payload = SlashCommandPayload()
        payload.text = "@user4 -1"
        payload.userName = "notAdmin"
        Mockito.`when`(userCacheService.getUser(ArgumentMatchers.anyString()))
            .thenReturn(buildUser(false))
        Mockito.`when`(userCacheService.getUserIdFromCache(ArgumentMatchers.anyString()))
            .thenReturn("user4Id")
        payloadParserService.parse(payload)
    }

    private fun buildUser(isAdmin: Boolean): User? {
        val user = User()
        user.isAdmin = isAdmin
        return user
    }
}