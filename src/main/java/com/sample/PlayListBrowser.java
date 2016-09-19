package com.sample;

import com.sample.model.SpotifyResponse;
import com.sample.model.Track;
import com.sample.service.JerseyClientUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by aniketbhide on 9/17/16.
 */

public class PlayListBrowser {
    public static void main(String[] args) {
        PlayListBrowser browser = new PlayListBrowser();
        List<Track> playList = new ArrayList<>();
        String command;
        System.out.println("Type the poem in one line. To Exit type Exit");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Poem : ");
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("Exit")) {
                break;
            } else {
                try {
                    browser.getPlayList(command.toUpperCase(), playList);
                } catch (IOException e) {
                    System.out.println("Error occurred : " + e.getMessage());
                }
                playList.stream().forEach(p -> System.out.println(p.getName() + " " +
                        p.getExternal_urls().getSpotify()));
                playList.clear();
            }

        }
    }

    /**
     * @param inputStr
     * @param playList
     * @return Adds the track with folllowing logic
     * Call the api with searchString split by space.
     * Add the next string payload and check if the song exists if not add the previous one
     * remove the found longest song name from the string and repeat this until its empty.
     * @throws IOException
     */
    public List<String> getPlayList(String inputStr, List<Track> playList) throws IOException {
        List<String> returnList = new ArrayList<>();
        String prefix = "";
        Track prevFound = null;
        SpotifyResponse spotifyResponse = null;
        boolean found;
        int total;
        while (!inputStr.equalsIgnoreCase("")) {
            found = false;
            String searchString = getPrefix(prefix, inputStr);
            if ("".equalsIgnoreCase(searchString.trim())) {
                total = 0;
            } else {
                spotifyResponse = JerseyClientUtil.getInstance().getResponseTrack(searchString);
                total = spotifyResponse.getTracks().getTotal();
                System.out.println(searchString + " " + total);
            }

            if (total > 0) {
                List<Track> tracksList = Arrays.asList(spotifyResponse.getTracks().getItems());
                String input = inputStr;
                List<Track> filteredList = tracksList.stream().filter(p -> input.startsWith(p.getName().toUpperCase()))
                        .collect(Collectors.toList());
                if (filteredList.size() > 0) {
                    Optional<Track> filteredTrack = filteredList.stream().max(Comparator.comparing(s -> s.getName().length()));
                    if (filteredTrack.isPresent()) {
                        if (prevFound == null || !prevFound.getName().equalsIgnoreCase(filteredTrack.get().getName())) {
                            prevFound = filteredTrack.get();
                            found = true;
                        }
                    }
                }
            }
            if (!found) {
                if (prevFound != null) {
                    playList.add(prevFound);
                    inputStr = inputStr.replaceFirst(prevFound.getName().toUpperCase(), "").trim();
                    prevFound = null;
                    prefix = "";
                } else {
                    System.out.println("Not Found : " + searchString);
                    searchString = searchString.replace("*", "");
                    if (searchString.equalsIgnoreCase(inputStr)) {
                        inputStr = "";
                    } else {
                        prefix = searchString.replace("*", "");
                    }
                }
            } else {
                prefix = prevFound.getName().toUpperCase().trim();
            }

        }


        return returnList;
    }

    public String getPrefix(String prefix, String phrase) {

        if (prefix.equalsIgnoreCase(phrase) || "".equalsIgnoreCase(phrase)) {
            return "";
        } else {
            StringBuffer buffer = new StringBuffer();
            if (!"".equalsIgnoreCase(prefix)) {
                buffer.append(prefix);
                buffer.append(" ");
                phrase = phrase.replace(prefix, "").trim();
            }
            String[] divPhrase = phrase.split(" ");
            for (String s : divPhrase) {
                //TODO : This can be made better to avoid Of, my, is kind of word
                if (s.length() < 3) {
                    buffer.append(s);
                    buffer.append(" ");
                } else {
                    buffer.append(s);
                    break;
                }
            }
            buffer.append("*");
            return buffer.toString();
        }
    }
}



