package roombooking.fyp.fyproomviewfinal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuthResponse extends MainActivity {

    @SerializedName("sessionToken")
    @Expose
    private String sessionToken;
    @SerializedName("sessionExpires")
    @Expose
    private String sessionExpires;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("isAuthenticated")
    @Expose
    private Boolean isAuthenticated;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("links")
    @Expose
    private List<Object> links = null;
    @SerializedName("message")
    @Expose
    private Object message;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionExpires() {
        return sessionExpires;
    }

    public void setSessionExpires(String sessionExpires) {
        this.sessionExpires = sessionExpires;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Object> getLinks() {
        return links;
    }

    public void setLinks(List<Object> links) {
        this.links = links;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}