package com.markzfilter.courses.model;

import com.github.slugify.Slugify;

import java.util.*;

public class CourseIdea {

    // Fields
    private final String slug;
    private String title;
    private String creator;
    private Set<String> voters;

    // Constructor
    public CourseIdea(String title, String creator) {
        this.title = title;
        this.creator = creator;
        voters = new HashSet<>();

        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);
    }


    // Access Modifiers

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }


    public boolean addVoter(String voterUserName) {
        return voters.add(voterUserName);
    }

    public int getVoteCount() {
        return voters.size();
    }

    public List<String> getVoters() {
        return new ArrayList<>(voters);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseIdea that = (CourseIdea) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(creator, that.creator);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, creator);
    }
}
