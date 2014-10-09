package be.tjs.vubrestaurant.core;

/**
 * Holds application-wide constants.
 *
 * Using an enum would be a lot "nicer", but Android guidelines say to avoid these
 * due to performance overhead. This is a simple and unchanging situation so this'll do pig.
 *
 * Created by tjs on 09/10/14.
 */
public final class Constants {
    /*

     */
    public static final int RESTO_ETTERBEEK = 0;
    public static final int RESTO_JETTE = 1;
    public static final int LANG_EN = 0;
    public static final int LANG_NL = 1;
    public static final String ETTERBEEK = "Etterbeek";
    public static final String JETTE = "Jette";
    public static final String[] RESTAURANTS = new String[]{ETTERBEEK, JETTE};
}
