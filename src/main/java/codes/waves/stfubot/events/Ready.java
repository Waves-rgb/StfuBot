package codes.waves.stfubot.events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class designated to listening for the ready event
 * to be fired.
 */
public class Ready extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Ready.class);


    @SubscribeEvent
    public void onReady(ReadyEvent e) {
        log.info("Ready! Logged in as {}. Found {} guilds, {} are available while {} are not. Found {} users.",
                e.getJDA().getSelfUser().getAsTag(),
                e.getGuildTotalCount(),
                e.getGuildAvailableCount(),
                e.getGuildUnavailableCount(),
                e.getJDA().getUsers().size());
    }
}
