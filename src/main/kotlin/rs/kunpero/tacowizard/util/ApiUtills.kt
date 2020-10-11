package rs.kunpero.tacowizard.util

import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject

class ApiUtills {
    companion object {
        @JvmStatic
        fun buildResponse(message: String?): SlashCommandResponse {
            return SlashCommandResponse.builder()
                .responseType("ephemeral")
                .blocks(
                    listOf(
                        SectionBlock.builder()
                            .text(
                                MarkdownTextObject.builder()
                                    .text(message).build()
                            ).build()
                    )
                ).build()
        }
    }
}