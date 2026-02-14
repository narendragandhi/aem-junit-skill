package com.example.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class HeroComponent {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String linkText;

    @ValueMapValue
    private String linkURL;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLinkText() {
        return linkText;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public boolean hasContent() {
        return title != null && !title.isEmpty();
    }
}
