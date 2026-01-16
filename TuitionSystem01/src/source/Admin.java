package source;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Admin extends User {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("phone")
    private String phone;

    @JsonCreator
    public Admin(
            @JsonProperty("name") String fullName,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("id") String adminId,
            @JsonProperty("phone") String phone) {
        super(adminId, fullName, username, password, "Admin");
        this.id = adminId;
        this.phone = phone;
    }
    
    @Override
    public String getUserId() {
        return id;
    }
    
    // Helper to get ID specifically as Admin ID
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getAdminID() {
        return id;
    }
}
