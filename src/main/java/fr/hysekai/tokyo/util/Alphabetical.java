package fr.hysekai.tokyo.util;

public class Alphabetical {

    private static final char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final String[] cachedOrders = new String[81];

    public static String getStringOrder(int index) {
        return getStringOrder(index, false);
    }

    public static String getStringOrder(int index, boolean cache) {
        boolean caching = cache && index <= 80;
        if (caching) {
            String cachedString = cachedOrders[index];
            if (cachedString != null) return cachedString;
        }
        final StringBuilder builder = new StringBuilder();
        final double size = index / 26.0;
        for (int i = 0; i < Math.floor(size); ++i) {
            builder.append(chars[25]);
        }
        String result = builder.append(chars[(int) Math.round(26.0 * (size - Math.floor(size)))]).toString();
        if (caching) cachedOrders[index] = result;
        return result;
    }
}
