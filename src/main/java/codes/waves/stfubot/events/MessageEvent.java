package codes.waves.stfubot.events;

import codes.waves.stfubot.managers.VoiceReceiveManager;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Objects;

/**
 * Class designated to listening for messages.
 */
public class MessageEvent extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MessageEvent.class);

    /**
     * Whenever the bot receives a message, check for a
     * command issued to the bot, and execute the desired
     * code.
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        try {
            // Log the message we recieved.
            if (event.isFromType(ChannelType.PRIVATE))
                log.info("[PM] {}: {}\n", event.getAuthor().getName(),
                        event.getMessage().getContentDisplay());
            else
                log.info("[{}] [{}] {}: {}\n", event.getGuild().getName(),
                        event.getTextChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(),
                        event.getMessage().getContentDisplay());
        }
        // Catch a NullPointerException and ignore it (to stop random error messages being printed to the console when a bot sends an embed.)
        catch (NullPointerException ignored) {}

        Message msg = event.getMessage();

        // TODO: Make a command registration system, and add commands.
        // If the message starts with "stfu!listen" then we join the channel and begin listening to people.
        if (msg.getContentRaw().toLowerCase(Locale.ROOT).startsWith("stfu!listen"))
        {
            if (!Objects.requireNonNull(Objects.requireNonNull(msg.getMember()).getVoiceState()).inVoiceChannel()) {
                msg.getChannel().sendMessage("You are not in a vc.").submit();
                return;
            }

            VoiceChannel channel = Objects.requireNonNull(msg.getMember().getVoiceState()).getChannel();

            assert channel != null;
            // Join the channel
            channel.getGuild().getAudioManager().openAudioConnection(channel);
            // Set the ReceivingHandler for this guild to an instance of our VoiceRecieveManager.
            channel.getGuild().getAudioManager().setReceivingHandler(VoiceReceiveManager.get(channel.getGuild(), channel));

            // Send a message to the channel the command was issued in.
            msg.getChannel().sendMessage("Connected to your vc.").submit();
        }

        if (msg.getContentRaw().toLowerCase(Locale.ROOT).startsWith("stfu!leave"))
        {
            // TODO: Make this clean up MemberVoiceHandlers.
            // Close the audio connection, and leave the channel.
            msg.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
