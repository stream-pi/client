// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.settings.About;

public class Contributor
{
    public String name;
    public String email;
    public String description;
    public String location;
    
    public Contributor(final String name, final String email, final String description, final String location) {
        this.name = null;
        this.email = null;
        this.description = null;
        this.location = null;
        this.name = name;
        this.email = email;
        this.description = description;
        this.location = location;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
}
