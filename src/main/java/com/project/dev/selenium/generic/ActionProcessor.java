/*
 * @fileoverview    {ActionProcessor}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementación realizada.
 * @version 2.0     Documentación agregada.
 */
package com.project.dev.selenium.generic;

import com.google.common.collect.ImmutableMap;
import com.project.dev.file.generic.FileProcessor;
import com.project.dev.flag.processor.Flag;
import com.project.dev.flag.processor.FlagMap;
import com.project.dev.struct.Action;
import com.project.dev.struct.Element;
import com.project.dev.struct.Page;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;

/**
 * TODO: Definición de {@code ActionProcessor}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
public class ActionProcessor {

    private static int currentIndex = 0;
    private static String outputPath;
    private static String outputFile;

    /**
     * TODO: Definición de {@code runPageActions}.
     *
     * @param driver
     * @param pages
     * @return
     */
    public static boolean runPageActions(@NonNull WebDriver driver, @NonNull List<Page> pages) {
        Page page = pages.get(currentIndex++);
        System.out.println(page);
        for (Element element : page.getElements()) {
            //System.out.println(element);
            for (Action action : element.getActions()) {
                System.out.println(action);
                try {
                    WebElement webElm;
                    if (element.getId() != null)
                        webElm = driver.findElement(By.id(element.getId()));
                    else if (element.getName() != null)
                        webElm = driver.findElement(By.name(element.getName()));
                    else if (element.getPlaceholder() != null)
                        webElm = driver.findElement(By.xpath("//" + element.getType()
                                + "[@placeholder='" + element.getPlaceholder() + "']"));
                    else
                        webElm = driver.findElement(By.xpath(element.getXpath()));
                    action.executeAction(driver, webElm);
                } catch (Exception e) {
                    System.out.println("Error executing action in element: " + element);
                    e.printStackTrace();
                    System.out.println("");
                    return false;
                }
                try {
                    Thread.sleep(action.getDelay());
                } catch (InterruptedException e) {
                    System.out.println("Error executing sleep");
                }
            }
        }

        try {
            Thread.sleep(page.getDelay());
        } catch (InterruptedException e) {
            System.out.println("Error executing sleep");
        }

        System.out.println("Current page:" + driver.getCurrentUrl());
        System.out.println("");
        try (FileOutputStream fos = new FileOutputStream(outputPath + "\\" + outputFile, true);
                OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osr);) {
            writer.write(driver.getCurrentUrl() + "\n");

        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
        return true;
    }

    /**
     * TODO: Definición de {@code processFlags}.
     *
     * @param flags
     * @return
     */
    public static boolean processFlags(Flag[] flags) {
        boolean result = true;

        String actionsPackage = "com.project.dev.struct.action.";
        Map<String, String> flagsMap = FlagMap.convertFlagsArrayToMap(flags);
        String chromeDriverPath = flagsMap.get("-chromeDriverPath");
        String configFilePath = flagsMap.get("-configFilePath");
        outputPath = flagsMap.get("-outputPath");
        outputFile = flagsMap.get("-outputFile");
        String chromeUserDataDir = System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data";
        String chromeProfileDir = flagsMap.get("-chromeProfileDir");

        int maxLoadPageTries = 3;
        int maxActionPageTries = 10;
        int delayTimeBeforeRetry = 2000;
        int loadPageTimeOut = 10000;
        int delayTimeBeforeEnd = 10000;

        chromeUserDataDir = FlagMap.validateFlagInMap(flagsMap, "-chromeUserDataDir", chromeUserDataDir, String.class);
        maxLoadPageTries = FlagMap.validateFlagInMap(flagsMap, "-maxLoadPageTries", maxLoadPageTries, Integer.class);
        maxActionPageTries = FlagMap.validateFlagInMap(flagsMap, "-maxActionPageTries", maxActionPageTries, Integer.class);
        delayTimeBeforeRetry = FlagMap.validateFlagInMap(flagsMap, "-delayTimeBeforeRetry", delayTimeBeforeRetry, Integer.class);
        loadPageTimeOut = FlagMap.validateFlagInMap(flagsMap, "-loadPageTimeOut", loadPageTimeOut, Integer.class);
        delayTimeBeforeEnd = FlagMap.validateFlagInMap(flagsMap, "-delayTimeBeforeEnd", delayTimeBeforeEnd, Integer.class);

        if (!FileProcessor.validateFile(chromeDriverPath)) {
            System.out.println("Invalid file in flag '-chromeDriverPath'");
            result = false;
        } else if (!FileProcessor.validateFile(configFilePath)) {
            System.out.println("Invalid file in flag '-configFilePath'");
            result = false;
        } else if (!FileProcessor.validatePath(outputPath)) {
            System.out.println("Invalid path in flag '-outputPath'");
            result = false;
        } else if (!FileProcessor.validatePath(chromeUserDataDir)) {
            System.out.println("Invalid path in flag '-chromeUserDataDir'");
            result = false;
        } else {
            System.out.println("Parsing config file...");
            //System.out.println("");

            JSONArray config = null;
            try {
                JSONParser parser = new JSONParser();
                config = (JSONArray) parser.parse(new FileReader(configFilePath));
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Error parsing the file: '" + configFilePath + "'");
                result = false;
            }

            List<Page> pages = new ArrayList<>();
            List<String> urlPages = new ArrayList<>();

            if (result && config != null) {
                int index = 0;
                for (Object currentPage : config) {
                    Page page = null;
                    List<Element> elementsArray = new ArrayList<>();
                    JSONArray elements = null;
                    try {
                        JSONObject jsonCurrentPage = (JSONObject) currentPage;
                        page = Page.builder()
                                .id(index++)
                                .url((String) jsonCurrentPage.get("url"))
                                .delay((long) jsonCurrentPage.get("delay-before-next"))
                                .build();
                        if (page.getUrl() == null)
                            throw new Exception("Page can't be null");
                        elements = (JSONArray) jsonCurrentPage.get("elements");
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("Error getting info of current page");
                        result = false;
                    }
                    if (result && page != null && elements != null) {
                        System.out.println("Page:  " + page.getUrl());
                        //System.out.println("Delay: " + page.getDelay());
                        //System.out.println("Elements:");
                        for (Object currentElement : elements) {
                            try {
                                JSONObject jsonCurrentElement = (JSONObject) currentElement;
                                Element element = Element.builder()
                                        .id((String) jsonCurrentElement.get("id"))
                                        .name((String) jsonCurrentElement.get("name"))
                                        .placeholder((String) jsonCurrentElement.get("placeholder"))
                                        .xpath((String) jsonCurrentElement.get("xpath"))
                                        .type((String) jsonCurrentElement.get("type"))
                                        .build();
                                List<Action> actions = new ArrayList<>();
                                for (Object currentAction : (JSONArray) jsonCurrentElement.get("actions")) {
                                    JSONObject jsonCurrentAction = (JSONObject) currentAction;
                                    String type = (String) jsonCurrentAction.get("type");
                                    Object value = jsonCurrentAction.get("value");
                                    long delay_element = (long) jsonCurrentAction.get("delay-before-next");

                                    String className = actionsPackage;
                                    String[] classNameAux = type.split("-");
                                    for (String name : classNameAux)
                                        className += name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

                                    Constructor cons = Class.forName(className).getConstructors()[0];

                                    Action action = (Action) cons.newInstance();
                                    action.setType(type);
                                    action.setValue(value);
                                    action.setDelay(delay_element);
                                    actions.add(action);
                                }

                                element.setActions(actions);
                                //System.out.println(element);
                                elementsArray.add(element);
                            } catch (Exception e) {
                                System.out.println("Error parsing element:");
                                System.out.println(currentElement);
                                //e.printStackTrace();
                                result = false;
                                break;
                            }
                        }
                        page.setElements(elementsArray);
                        pages.add(page);
                        urlPages.add(page.getUrl());
                        //System.out.println("");
                        if (!result)
                            break;
                    }
                }
            }

            if (result) {
                System.out.println("\nPages:");
                for (Page page : pages)
                    System.out.println(page);
                System.out.println("");

                //for (Page page : pages)
                //    runPageActions(null, pages);
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                ChromeOptions options = new ChromeOptions();
                options.addArguments("user-data-dir=" + chromeUserDataDir);
                options.addArguments("--remote-allow-origins=*");
                if (flagsMap.get("--notUseIncognito") == null)
                    options.addArguments("--incognito");
                if (chromeProfileDir != null)
                    options.addArguments("--profile-directory=" + chromeProfileDir);
                ChromeDriver driver = new ChromeDriver(options);
                DevTools devTools = driver.getDevTools();
                devTools.createSession();
                devTools.send(new Command<>("Network.enable", ImmutableMap.of()));

                for (String url : urlPages) {
                    if (!result)
                        break;
                    for (int i = 1; i <= maxActionPageTries; i++) {
                        if (SeleniumProcessor.forEachPage(driver, Arrays.asList(url), maxLoadPageTries,
                                delayTimeBeforeRetry, loadPageTimeOut, ActionProcessor::runPageActions, pages)) {
                            break;
                        }
                        currentIndex--;
                        if (i == maxActionPageTries) {
                            result = false;
                            System.out.println("Error executing actions on page: " + url + "\n");
                            break;
                        }

                        try {
                            Thread.sleep(delayTimeBeforeRetry);
                        } catch (InterruptedException e) {
                            System.out.println("Error executing sleep");
                        }
                        System.out.println("");
                    }
                }
                System.out.println("Finish processing pages...");

                try {
                    Thread.sleep(delayTimeBeforeEnd);
                } catch (InterruptedException e) {
                    System.out.println("Error executing sleep");
                }
                driver.quit();

            }
        }
        return result;
    }

}
