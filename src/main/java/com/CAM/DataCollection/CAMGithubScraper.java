package com.CAM.DataCollection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CAMGithubScraper extends GitHubScraper {
    public CAMGithubScraper(String repo) throws ScrapeException {
        super(repo, null, true, true);
    }

    public String getReleaseJarDownload() throws ScrapeException {
        JsonArray jsonArray = getRepoArray();
        JsonArray assets = ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets"));
        int assetIndex = 0;
        for(int i=0; i<assets.size(); i++){
            String fileName = ((JsonObject) assets.get(i)).get("name").getAsString();
            if(!fileName.contains(".jar")){
                continue;
            }
            assetIndex = i;
            break;
        }
        String downloadLink = ((JsonObject) ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets")).get(assetIndex)).get("browser_download_url").getAsString();
        return downloadLink;
    }

    public String getReleaseExeDownload() throws ScrapeException {
        JsonArray jsonArray = getRepoArray();
        JsonArray assets = ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets"));
        int assetIndex = 0;
        for(int i=0; i<assets.size(); i++){
            String fileName = ((JsonObject) assets.get(i)).get("name").getAsString();
            if(!fileName.contains(".exe")){
                continue;
            }
            assetIndex = i;
            break;
        }
        String downloadLink = ((JsonObject) ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets")).get(assetIndex)).get("browser_download_url").getAsString();
        return downloadLink;
    }

    public String getChangelogDownload() throws ScrapeException {
        JsonArray jsonArray = getRepoArray();
        JsonArray assets = ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets"));
        int assetIndex = 0;
        for(int i=0; i<assets.size(); i++){
            String fileName = ((JsonObject) assets.get(i)).get("name").getAsString();
            if(!fileName.contains("CHANGELOG.txt")){
                continue;
            }
            assetIndex = i;
            break;
        }
        String downloadLink = ((JsonObject) ((JsonArray) ((JsonObject) jsonArray.get(0)).get("assets")).get(assetIndex)).get("browser_download_url").getAsString();
        return downloadLink;
    }

}
