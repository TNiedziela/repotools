package com.niedzielat.repotools.github;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedzielat.repotools.github.dto.BranchDTO;
import com.niedzielat.repotools.github.dto.RepositoryDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
@RequestMapping("repositories")
public class GithubRestController {

    @GetMapping("/{owner}")
    public ResponseEntity<?> getRepositoryDetailsByOwner(@PathVariable(name = "owner") String ownerName) {


        try {
            List<RepositoryDTO> repository = getResponse(ownerName);
            return ResponseEntity.ok(repository);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        } catch (HttpClientErrorException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\n \"status\": 404 \n \"message:\" could not find user with provided name: " + ownerName + "\n}"); //todo
        }
    }


    private List<RepositoryDTO> getResponse(String userName) throws JsonProcessingException {
        String reposResponse = getGitHubRepos(userName);

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<RepositoryDTO> mapping = mapper.readValue(reposResponse, new TypeReference<>() {});

        List<RepositoryDTO> filteredMapping =  mapping.stream()
                .filter(repositoryDTO -> !repositoryDTO.isFork())
                .toList();

        filteredMapping.forEach(repositoryDTO -> setRepositoryBranchesDetails(userName, repositoryDTO));
        return filteredMapping;
    }

    private String getGitHubRepos(String userName) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("User-Agent", "http://developer.github.com/v3/#user-agent-required");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> exchange = rest.exchange(
                "https://api.github.com/users/" + userName + "/repos",
                HttpMethod.GET,
                entity,
                String.class);

        String body = exchange.getBody();
        return body;
    }

    private String getGitHubBranches(String userName,String repoName) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> exchange = rest.exchange(
                "https://api.github.com/repos/" + userName + "/" +  repoName + "/branches",
                HttpMethod.GET,
                entity,
                String.class);

        String body = exchange.getBody();
        return body;
    }

    private void setRepositoryBranchesDetails(String ownerName, RepositoryDTO repositoryDTO){
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            List<BranchDTO> mapping = mapper.readValue(getGitHubBranches(ownerName, repositoryDTO.getNameOfRepository()), new TypeReference<>() {});
            repositoryDTO.setBranchesDTO(mapping);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't map values: " + e);
        }
    }
}
