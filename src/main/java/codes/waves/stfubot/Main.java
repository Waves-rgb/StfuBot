package codes.waves.stfubot;

import codes.waves.stfubot.events.MessageEvent;
import codes.waves.stfubot.events.Ready;
import codes.waves.stfubot.generators.RandomDmMessage;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

// TODO: Make all the multi-line single comments comments into block comments.
// TODO: Make a better system for managing the token, like .env on node or whatever.

/**
 * Main class for the program, starts the bot.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static JDABuilder builder;
    public static JDA jda;

    /**
     * The entry point of the program, sets the first given argument
     * to the bot's token, creates a new JDABuilder, and attempts
     * to login to a bot account.
     */
    public static void main(String[] args) {

        // Check if the bot's token was provided.
        if (args.length <= 0)
        {
            log.error("Token was not provided! Exiting.");
            return;
        }

        // Set the token to the first provided argument.
        final String token = args[0];

        // Create a JDABuilder.
        JDABuilder jdab = JDABuilder.createDefault(token);

        // try to build the JDA class.
        try {
            jda = jdab.build();
        } catch (LoginException e) {
            log.error(e.getStackTrace().toString());
        }

        // Add the event listeners.
        jda.addEventListener(new MessageEvent());
        jda.addEventListener(new Ready());

        // Initialize our classes that need it.
        RandomDmMessage.init();
    }
}
