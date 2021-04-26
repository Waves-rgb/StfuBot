package codes.waves.stfubot.managers;

import codes.waves.stfubot.handlers.MemberVoiceHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Vector;

// Manages the MemberVoiceHandler classes.
public class MemberVoiceManager {

    // Holds all of the MemberVoiceHandlers.
    static Vector<MemberVoiceHandler> vrs = new Vector<>();

    /**
     * @return True if we find a MemberVoiceHandler with the desired traits
     * (can probably delete)
     */
    public static boolean exists(Guild g, VoiceChannel vc) {
        // Iterate over all of the members of the vrs list.
        for (MemberVoiceHandler vr: vrs) {
            // If we find a receiver with the desired elements, we return true.
            if (vr.getGuild() == g && vr.getVc() == vc)
                return true;
        }
        // We return false if we did not successfully find a MemberVoiceHandler with the desired elements.
        return false;
    }

    /**
     * @return Returns a new instance of a MemberVoiceHandler if we failed to find an existing one. But returns the found one if we find one.
     */
    public static MemberVoiceHandler get(Member m, Guild g, VoiceChannel vc) {
        // Iterates over all of the MemberVoiceHandlers and makes sure there isn't already a handler. But only if the vrs list contains some elements.
        if (!vrs.isEmpty()) {
            for (MemberVoiceHandler vr: vrs) {
                // If the MemberVoiceHandler has the values we are trying to find, we return it.
                if (vr.getGuild() == g && vr.getVc() == vc && vr.getMember() == m)
                    return vr;
            }
        }
        // If we did not return a MemberVoiceHandler, we make a new one, add it to our list, and return it.
        MemberVoiceHandler mvh = new MemberVoiceHandler(m, vc, g);
        vrs.add(mvh);
        return mvh;
    }

    /**
     * @return Returns true if a MemberVoiceHandler was found and destroyed, but false if it was not.
     */
    public static boolean destroy(Member u, Guild g, VoiceChannel vc) {
        // Iterate over all of the MemberVoiceHandlers.
        for (MemberVoiceHandler vr: vrs) {
            /*
             If the MemberVoiceHandler has the values we are trying to find, we nullify it, remove it from our list, and return true.
             Unlike the VoiceHandlers that are usually automatically cleaned up by JDA, MemberVoiceHandlers are not, so we need to do this manually.
             TODO: Clean up the unnecessary MemberVoiceHandlers when we are disconnected, moved, or are removed from the server.
            */
            if (vr.getGuild() == g && vr.getVc() == vc) {
                vrs.remove(vr);
                return true;
            }
        }

        // If we did not return true, then we failed to find and destroy the MemberVoiceHandler, so we return false.
        return false;
    }
}