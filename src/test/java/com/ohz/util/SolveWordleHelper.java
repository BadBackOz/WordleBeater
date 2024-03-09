package com.ohz.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.ohz.common.Configuration;
import com.ohz.pages.HomePage;
import io.cucumber.java.Scenario;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;


public class SolveWordleHelper {

    private ArrayList<String> wordList = new ArrayList<>();

    public void beatWordle(String startingWord) {

        wordList.addAll(listOfWords());

        HashMap<String, String> knownPlacementMap = new HashMap<>();
        knownPlacementMap.put("char1", null);
        knownPlacementMap.put("char2", null);
        knownPlacementMap.put("char3", null);
        knownPlacementMap.put("char4", null);
        knownPlacementMap.put("char5", null);

        Set<String> knownCharacters = new HashSet<>();
        Set<String> charsNotPresent = new HashSet<>();
        Set<String> charsPresentButNotAtIndex0 = new HashSet<>();
        Set<String> charsPresentButNotAtIndex1 = new HashSet<>();
        Set<String> charsPresentButNotAtIndex2 = new HashSet<>();
        Set<String> charsPresentButNotAtIndex3 = new HashSet<>();
        Set<String> charsPresentButNotAtIndex4 = new HashSet<>();


        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        List<String> notWordsList = new ArrayList<>();

        HomePage homePage = new HomePage();
        try {
            homePage.buttonPlay.javascriptClick();
            homePage = new HomePage();
            homePage.howToPlayDialogCloseButton.javascriptClick();
            startDateTime = LocalDateTime.now();

            boolean isComplete = false;
            boolean isAnyPlacementKnown = false;
            String randomWord = null;
            boolean isStartingWordInvalid = false;

            int i = 0;
            while (i < 6 && !isComplete) {

                boolean isWord = false;
                while (!isWord) {
                    if (i == 0 && !isStartingWordInvalid) {
                        //randomWord = Configuration.getExcelData().get("startingWord");
                        randomWord = !"null".equalsIgnoreCase(startingWord) && startingWord.length() == 5 ? startingWord.toUpperCase() : getRandomWord(wordList).toUpperCase();

                        homePage.typeWord(randomWord);
                    } else {
                        randomWord = getRandomWord(wordList).toUpperCase();
                        homePage.typeWord(randomWord);
                    }
                    boolean isInvalidWord = homePage.messageInvalidWord.isDisplayed(2);
                    if (isInvalidWord) {
                        for (int x = 0; x < 5; x++) {
                            Thread.sleep(300);
                            homePage.buttonBackspace.clickWithoutLogging();
                        }
                        notWordsList.add(randomWord);
                        wordList.remove(randomWord.toLowerCase());
                        isStartingWordInvalid = true;
                    } else {
                        System.out.println("Words in list at iteration " + (i + 1) + ": " + wordList.size());
                        wordList.remove(randomWord.toLowerCase());
                        isWord = true;
                    }
                }
                Configuration.getScenario().log("Word played: " + randomWord);

                int friendlyCount = i + 1;
                List<WebElement> tileList = homePage.getDriver().findElements(By.xpath("(//div[@class='Row-module_row__pwpBq'])[" + friendlyCount + "]//div[@data-testid='tile']"));

                boolean isWordFound = true;
                for (int y = 0; y < tileList.size(); y++) {
                    String actualState = WebElementUtil.waitForAttributeToNotContain(tileList.get(y), "data-state", "empty", 5);
                    if ("absent".equalsIgnoreCase(actualState)) {
                        isWordFound = false;
                        if (!knownCharacters.contains(tileList.get(y).getText().toLowerCase())) {
                            charsNotPresent.add(tileList.get(y).getText().toLowerCase());
                        }
                    } else if ("correct".equalsIgnoreCase(actualState)) {
                        knownCharacters.add(tileList.get(y).getText().toLowerCase());
                        knownPlacementMap.put("char".concat(String.valueOf(y + 1)), tileList.get(y).getText().toLowerCase());
                        isAnyPlacementKnown = true;
                    } else if ("present".equalsIgnoreCase(actualState)) {
                        isWordFound = false;
                        knownCharacters.add(tileList.get(y).getText().toLowerCase());
                        if (y == 0) {
                            charsPresentButNotAtIndex0.add(tileList.get(y).getText().toLowerCase());
                        } else if (y == 1) {
                            charsPresentButNotAtIndex1.add(tileList.get(y).getText().toLowerCase());
                        } else if (y == 2) {
                            charsPresentButNotAtIndex2.add(tileList.get(y).getText().toLowerCase());
                        } else if (y == 3) {
                            charsPresentButNotAtIndex3.add(tileList.get(y).getText().toLowerCase());
                        } else if (y == 4) {
                            charsPresentButNotAtIndex4.add(tileList.get(y).getText().toLowerCase());
                        }
                    }
                }


                if (isWordFound) {
                    Configuration.getScenario().log("Wordle Solved: " + randomWord);
                    Configuration.getScenario().log("Wordle solved on attempt " + (i + 1));

                    endDateTime = LocalDateTime.now();
                    Configuration.logWithScreenshot("Seconds to complete Wordle: " + startDateTime.until(endDateTime, ChronoUnit.SECONDS));

                    return;
                } else if (i == 5) {
                    endDateTime = LocalDateTime.now();
                    Configuration.getScenario().log("Failed to solve Wordle");
                    Configuration.logWithScreenshot("Seconds to complete Wordle: " + startDateTime.until(endDateTime, ChronoUnit.SECONDS));
                    Assert.fail();
                }

                for (String character : knownCharacters) {
                    charsNotPresent.remove(character);
                }


                for (String character : charsNotPresent) {
                    for (String knownCharacter : knownCharacters) {
                        if (character.equals(knownCharacter)) {
                            charsNotPresent.remove(knownCharacter);
                        }
                    }
                }

                if (!charsNotPresent.isEmpty()) {
                    Iterator<String> iterator = wordList.iterator();
                    while (iterator.hasNext()) {
                        String word = iterator.next();
                        for (String character : charsNotPresent) {
                            if (word.toLowerCase().contains(character.toLowerCase())) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
                charsNotPresent = new HashSet<>();

                if (isAnyPlacementKnown) {
                    Iterator<String> iterator = wordList.iterator();
                    while (iterator.hasNext()) {
                        String word = iterator.next();
                        if (knownPlacementMap.get("char1") != null) {
                            if (!String.valueOf(word.charAt(0)).contains(knownPlacementMap.get("char1").toLowerCase())) {
                                iterator.remove();
                                continue;
                            }
                        }
                        if (knownPlacementMap.get("char2") != null) {
                            if (!String.valueOf(word.charAt(1)).contains(knownPlacementMap.get("char2").toLowerCase())) {
                                iterator.remove();
                                continue;
                            }
                        }
                        if (knownPlacementMap.get("char3") != null) {
                            if (!String.valueOf(word.charAt(2)).contains(knownPlacementMap.get("char3").toLowerCase())) {
                                iterator.remove();
                                continue;
                            }
                        }
                        if (knownPlacementMap.get("char4") != null) {
                            if (!String.valueOf(word.charAt(3)).contains(knownPlacementMap.get("char4").toLowerCase())) {
                                iterator.remove();
                                continue;
                            }
                        }
                        if (knownPlacementMap.get("char5") != null) {
                            if (!String.valueOf(word.charAt(4)).contains(knownPlacementMap.get("char5").toLowerCase())) {
                                iterator.remove();
                                continue;
                            }
                        }
                    }
                }

                if (!knownCharacters.isEmpty()) {
                    Iterator<String> iterator = wordList.iterator();
                    while (iterator.hasNext()) {
                        String word = iterator.next();
                        for (String character : knownCharacters) {
                            if (!word.toLowerCase().contains(character.toLowerCase())) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }


                Iterator<String> iterator = wordList.iterator();
                while (iterator.hasNext()) {
                    String word = iterator.next();
                    boolean isRemovalComplete = false;
                    while (!isRemovalComplete) {
                        for (String character : charsPresentButNotAtIndex0) {
                            if (String.valueOf(word.charAt(0)).toLowerCase().contains(character.toLowerCase())) {
                                iterator.remove();
                                isRemovalComplete = true;
                                break;
                            }
                        }

                        if (!isRemovalComplete) {
                            for (String character : charsPresentButNotAtIndex1) {
                                if (String.valueOf(word.charAt(1)).toLowerCase().contains(character.toLowerCase())) {
                                    iterator.remove();
                                    isRemovalComplete = true;
                                    break;
                                }
                            }
                        }


                        if (!isRemovalComplete) {
                            for (String character : charsPresentButNotAtIndex2) {
                                if (String.valueOf(word.charAt(2)).toLowerCase().contains(character.toLowerCase())) {
                                    iterator.remove();
                                    isRemovalComplete = true;
                                    break;
                                }
                            }
                        }

                        if (!isRemovalComplete) {
                            for (String character : charsPresentButNotAtIndex3) {
                                if (String.valueOf(word.charAt(3)).toLowerCase().contains(character.toLowerCase())) {
                                    iterator.remove();
                                    isRemovalComplete = true;
                                    break;
                                }
                            }
                        }

                        if (!isRemovalComplete) {
                            for (String character : charsPresentButNotAtIndex4) {
                                if (String.valueOf(word.charAt(4)).toLowerCase().contains(character.toLowerCase())) {
                                    iterator.remove();
                                    isRemovalComplete = true;
                                    break;
                                }
                            }
                        }
                        isRemovalComplete = true;
                    }

                }

                charsPresentButNotAtIndex0 = new HashSet<>();
                charsPresentButNotAtIndex1 = new HashSet<>();
                charsPresentButNotAtIndex2 = new HashSet<>();
                charsPresentButNotAtIndex3 = new HashSet<>();
                charsPresentButNotAtIndex4 = new HashSet<>();


                i = i + 1;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    /*public void getRealWords() throws IOException, InterruptedException {

        Set<String> listOfWords = listOfWords();
        Set<String> remainingWords = new HashSet<>();
        remainingWords.addAll(listOfWords);

        System.out.println(listOfWords.size());
        System.out.println(remainingWords.size());

        Set<String> validatedWords = new HashSet<>();
        for (String word : listOfWords) {
            try {
                String responseString = Request.get("https://api.dictionaryapi.dev/api/v2/entries/en/" + word).execute().returnContent().asString();

                if (!responseString.contains("No Definitions Found")) {
                    validatedWords.add(word);
                    System.out.println(word);
                    Thread.sleep(300);
                }
                remainingWords.remove(word);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }

        }

        File file = new File("validatedWords.txt");
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (String word : validatedWords) {
                bw.write(word);
                bw.newLine();
            }
        }

        File file2 = new File("remainingWords.txt");
        try (FileWriter fw = new FileWriter(file2, false);
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (String word : remainingWords) {
                bw.write(word);
                bw.newLine();
            }
        }

    }*/


    public static Set<String> listOfWords() {
        Set<String> wordList = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/validatedWords.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length() == 5){
                    wordList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    public static String getRandomWord(ArrayList<String> wordList) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
        return wordList.get(randomNum);
    }

}
