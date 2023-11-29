package info.dmerej.contacts;


import java.io.File;
import java.util.stream.Stream;

public class App {

    private Database database;
    private ContactsGenerator contactsGenerator;

    public App() {
        File file = new File("contacts.sqlite3");
        if (!file.exists()) {
            database = new Database(file);
            database.migrate();
        } else {
            database = new Database(file);
        }
        contactsGenerator = new ContactsGenerator();
    }
 
    public static void main(String[] args) {
        /*
        if (args.length != 1) {
            System.err.println("Not enough args");
            System.exit(2);
        }
        int count = Integer.parseInt(args[0]);
        */
        int count = 1000000;
        App app = null;
        try {
            app = new App();
            app.insertContacts(count);
            app.lookupContact(count);
        } finally {
            if (app != null) {
                app.close();
            }
        }
    }

    private void insertContacts(int count) {
        int c = count;
        while(c > 0) {
            if(c >= 100) {
                Stream<Contact> contacts = contactsGenerator.generateContacts(100);
                database.insertContacts(contacts);
                c = c-100;
            }
            else {
                Stream<Contact> contacts = contactsGenerator.generateContacts(c%100);
                database.insertContacts(contacts);
                c = 0;
            }
        }
    }

    private void lookupContact(int count) {
        String email = String.format("email-%d@aol.com", count);
        long start = System.currentTimeMillis();
        database.getContactNameFromEmail(email);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.format("Query took %f seconds\n", elapsed / 1000.0);
    }

    public void close() {
        database.close();
    }

}

