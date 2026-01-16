package source;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "role",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Student.class, name = "Student"),
    @JsonSubTypes.Type(value = Tutor.class, name = "Tutor")
})
public abstract class User {
    protected String userId; // Mapped to 'id' or 'tutorID' in subclasses
    protected String fullName; // Mapped to 'name'
    protected String username;
    protected String password;
    protected String role;

    public User() {}

    public User(String userId, String fullName, String username, String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Abstract method to get the ID, as JSON keys differ (id vs tutorID)
    public abstract String getUserId();

    @JsonProperty("name")
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

