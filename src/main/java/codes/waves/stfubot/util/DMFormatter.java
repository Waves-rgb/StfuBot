package codes.waves.stfubot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class DMFormatter {
    /**
     * @param s The message to be formatted.
     * @return The first given argument, formatted, based on the given parameters.
     */
    public static String format(String s, Member m, VoiceChannel vc, Guild g)
    {
        // Replace the placeholders with actual values.
        return s.replaceAll("%USERNAME%", m.getEffectiveName())
                .replaceAll("%GUILDNAME%", g.getName())
                .replaceAll("%MENTION%", m.getAsMention())
                .replaceAll("%VCNAME%", vc.getName());
    }
}
