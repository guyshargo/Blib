package client;

import model.Librarian;
import model.Subscriber;

public class SessionManager {
    public static Subscriber currentSubscriber = null;
    public static Librarian currentLibrarian = null;

    public static void clearSession() {
        currentSubscriber = null;
        currentLibrarian = null;
    }
}