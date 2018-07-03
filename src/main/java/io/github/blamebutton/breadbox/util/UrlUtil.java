package io.github.blamebutton.breadbox.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class UrlUtil {

    private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    /**
     * Private constructor so it cannot be constructed.
     */
    private UrlUtil() {
        // Private constructor
    }

    /**
     * Encode a string to an URL safe format
     *
     * @param url the url/string
     * @return the encoded string
     */
    public static String encode(String url) {
        return encode(url, "UTF-8");
    }

    /**
     * Encode a string to an URL safe format
     *
     * @param url     the url/string
     * @param charset the charset to use for encoding
     * @return the encoded string
     */
    static String encode(String url, String charset) {
        try {
            return URLEncoder.encode(url, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(I18n.get("unsupported.charset"), e);
            return null;
        }
    }
}
