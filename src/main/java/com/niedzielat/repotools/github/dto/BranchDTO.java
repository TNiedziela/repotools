package com.niedzielat.repotools.github.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Map;

public class BranchDTO {

    @JsonProperty("name")
    private String name;
    private String commitSha;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("commitSha")
    public String getCommitSha() {
        return commitSha;
    }

    @JsonSetter("commit")
    public void setCommitSha(Map<String, String> commit) {
        this.commitSha = commit.get("sha");
    }
}
