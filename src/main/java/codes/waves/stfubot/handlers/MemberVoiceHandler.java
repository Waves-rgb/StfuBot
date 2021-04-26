package codes.waves.stfubot.handlers;

import codes.waves.stfubot.generators.RandomDmMessage;
import codes.waves.stfubot.managers.MemberVoiceManager;
import codes.waves.stfubot.util.DMFormatter;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

/**
 * User-Specific audio handler for listening to users independently
 * and disconnecting them if they are too loud in a certain time
 * period.
 */

// TODO: 4/26/2021 Make this configurable, and add a config command.
//  so if one user's mic is really loud they dont get disconnected 24/7 and
//  if another user's mic is quiet enough to not get flagged by the bot,
//  they can actually get flagged.
public class MemberVoiceHandler {

    private static final Logger log = LoggerFactory.getLogger(MemberVoiceHandler.class);

    private final Guild g;
    private final Member m;
    private final VoiceChannel vc;

    public MemberVoiceHandler(Member m, VoiceChannel vc, Guild g) {
        log.info("Made a new MemberVoiceHandler: for {}, in {}, in {}", m.getEffectiveName(),
                vc.getName(),
                g.getName());
        this.g = g;
        this.m = m;
        this.vc = vc;
    }

    public Guild getGuild() {
        return g;
    }

    public Member getMember() {
        return m;
    }

    public VoiceChannel getVc() {
        return vc;
    }

    private int iTimesExceededLimit = 0;

    private boolean bIgnoreIncoming = false;

    /**
     * @param db The peak of the audio clip from this user.
     * Checks if the user needs to be disconnected. And attempts to do so.
     */
    public void runKickCheck(double db) {

        // If we are set to ignore incoming data, we will return and not execute any of the following code.
        if (bIgnoreIncoming)
            return;

        // If they have exceeded the set limit more than 10 times, we kick them and send them a random message in dms (from resources/dms.txt).
        if (iTimesExceededLimit >= 10) {
            // Ignore incoming sounds so we do not try to execute twice.
            bIgnoreIncoming = true;

            // Kick them.
            g.kickVoiceMember(m).submit();

            // Generate a random message from resources/dms.txt.
            String msg = RandomDmMessage.getRandomMessage();

            // Format the message to fill in the necessary variables.
            msg = DMFormatter.format(msg, m, vc, g);
            // Open the users dms.
            PrivateChannel dm = null;
            try {
                dm = m.getUser().openPrivateChannel().submit().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            errorCatcher: {
                try {
                    // If there is a latest message, check that it is no younger than one second. If it is, then we do not send another message. (to fix sending multiple messages).
                    assert dm != null;
                    if (dm.hasLatestMessage()) {
                        // Get the latest message, first get the id, then get the Message from id.
                        String lmid = dm.getLatestMessageId();
                        Message lm = dm.retrieveMessageById(lmid).submit().get();

                        // Get the difference between message created and message now.
                        long diff = ChronoUnit.SECONDS.between(lm.getTimeCreated().toInstant(), Instant.now());

                        System.out.println(diff);

                        if (diff < 1) {
                            // The message is more than 1 second young, exit the try catch.
                            log.info("not sending message LO");
                            break errorCatcher;
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // Actually send the message.
            dm.sendMessage(msg).submit();

            // Destroy ourselves (memory management 100 :sunglasses:).
            MemberVoiceManager.destroy(m, g, vc);
        }

        // If the peak is above the limit, we add one to the counter. If it is not, we reset the counter. This is to avoid kicking someone if their mic peaks.
        if (db >= -1)
            iTimesExceededLimit++;
        else
            iTimesExceededLimit = 0;
    }

}
