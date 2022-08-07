package com.discord.bot;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * The Discord Bot class sends a notification to a Discord channel
 * @author Gavin Wong
 * @version Aug 6, 2022
 */
public class Bot
{
	/**
	 * Sends a notification to the Discord channel in which the bot resides
	 * @param text: notification message
	 */
	@SuppressWarnings("static-access")
	public static void sendNotification(String text)
	{
		String token = "YOUR_BOT_TOKEN";
		JDABuilder builder = null;
		JDA jda;

		try
		{
			jda = builder.createDefault(token).build();
			jda.awaitReady();
			jda.getTextChannelsByName("general", true).get(0).sendMessage(text).queue();
		}
		catch (LoginException | InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
