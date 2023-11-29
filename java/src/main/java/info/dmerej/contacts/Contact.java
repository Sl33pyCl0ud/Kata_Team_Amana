package info.dmerej.contacts;

public record Contact(String name, String email) {
    
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

