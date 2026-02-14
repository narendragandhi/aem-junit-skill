package com.example.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationModel {

    @Self
    private Resource resource;

    @ValueMapValue
    private String title;

    @ChildResource(name = "items")
    private List<NavigationItem> items;

    public String getPath() {
        return resource != null ? resource.getPath() : null;
    }

    public String getTitle() {
        return title;
    }

    public List<NavigationItem> getItems() {
        return items;
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }
}
