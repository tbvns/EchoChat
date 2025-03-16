package xyz.tbvns.NsiWebsite;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class StringUtils {
    private static final int BCRYPT_COST = 12;
    public static String hashPassword(String plaintextPassword) {
        BCrypt.Hasher hasher = BCrypt.withDefaults();
        return hasher.hashToString(BCRYPT_COST, plaintextPassword.toCharArray());
    }

    public static boolean verifyPassword(String plaintextPassword, String hashedPassword) {
        BCrypt.Verifyer verifyer = BCrypt.verifyer();
        BCrypt.Result result = verifyer.verify(plaintextPassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':
                    escaped.append("&amp;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#x27;"); // Alternatively use "&#39;"
                    break;
                case '/':
                    escaped.append("&#x2F;"); // Helps prevent closing tags
                    break;
                default:
                    escaped.append(c);
                    break;
            }
        }
        return escaped.toString();
    }
}
