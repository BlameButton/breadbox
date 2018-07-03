package io.github.blamebutton.breadbox.util;

import io.github.blamebutton.breadbox.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrlUtilTest extends BaseTest {

    @Test
    void encodeDefaultCharset() {
        String string0 = UrlUtil.encode("hello world");
        String string1 = UrlUtil.encode("hello world & universe");

        assertEquals("hello+world", string0);
        assertEquals("hello+world+%26+universe", string1);
    }

    @Test
    void encodeGivenCharset() {
        String string0 = UrlUtil.encode("hello world", "UTF-8");
        String string1 = UrlUtil.encode("hello world", "not a charset");

        assertEquals("hello+world", string0);
        assertNull(string1);
    }
}