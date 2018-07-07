package io.github.blamebutton.breadbox.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedditCommandTest {

    @Test
    void handle() {
        // Arrange
        RedditCommand redditCommand = new RedditCommand();

        // Act


        // Assert
    }

    @Test
    void getUsage() {
        // Arrange
        RedditCommand redditCommand = new RedditCommand();
        String expected = "sr <subreddit>, user <user>, post <post> + <hour;day;week;month;year;all>";

        // Act
        String result = redditCommand.getUsage();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getDescription() {
        // Arrange
        RedditCommand redditCommand = new RedditCommand();
        String expected = "A command for retreiving info from reddit (subreddit, post, user)";

        // Act
        String result = redditCommand.getDescription();

        // Assert
        assertEquals(expected, result);
    }
}