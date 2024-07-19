package com.niedzielat.repotools.github.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"name", "login", "branches"})
public class RepositoryDTO {
    @JsonProperty("name")
    private String nameOfRepository;

    private List<BranchDTO> branchesDTO;

    private String ownerLogin;

    private boolean isFork;

    public String getNameOfRepository() {
        return nameOfRepository;
    }

    public void setNameOfRepository(String nameOfRepository) {
        this.nameOfRepository = nameOfRepository;
    }

    @JsonGetter("login")
    public String getOwnerLogin() {
        return ownerLogin;
    }

    @JsonSetter("owner")
    public void setOwnerLogin(Map<String, String> owner) {
        this.ownerLogin = owner.get("login");
    }

    @JsonIgnore
    public boolean isFork() {
        return isFork;
    }

    @JsonSetter("fork")
    public void setFork(boolean fork) {
        isFork = fork;
    }

    @JsonGetter("branches")
    public List<BranchDTO> getBranchesDTO() {
        return branchesDTO;
    }

    public void setBranchesDTO(List<BranchDTO> branchesDTO) {
        this.branchesDTO = branchesDTO;
    }
}
