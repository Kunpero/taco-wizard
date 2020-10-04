package rs.kunpero.fatpak.util;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import rs.kunpero.fatpak.util.exception.PayloadParserException;

@Slf4j
public class FeedParserUtils {
    private FeedParserUtils() {
    }

    public static String[] parsePayload(final SlashCommandPayload payload) {
        final String text = payload.getText();
        if (StringUtils.isEmpty(text)) {
            log.error("Empty payload text");
            throw new PayloadParserException("empty.payload.text");
        }
        String[] arr = text.split(" ", 3);
        if ((arr.length < 2 || arr.length > 3)) {
            log.error("Not enough arguments");
            throw new PayloadParserException("not.enough.arguments");
        }
        log.info("Parsed array: username:[{}], amount:[{}], commentary:[{}]", arr[0], arr[1], arr.length == 2 ? null : arr[2]);
        return arr;
    }
}
