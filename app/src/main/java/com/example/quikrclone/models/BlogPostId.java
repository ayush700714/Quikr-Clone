package com.example.quikrclone.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String BlogPostId;
    public <T extends BlogPostId> T withId(@NonNull final String Id)
    {
        this.BlogPostId=Id;
        return (T) this;
    }
}
