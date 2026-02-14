package com.example.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class NavigationItem {

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String url;

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}
