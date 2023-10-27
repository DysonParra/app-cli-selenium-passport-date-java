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
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev.selenium.generic;

import com.google.common.collect.ImmutableMap;
import com.project.dev.file.generic.FileProcessor;
import com.project.dev.flag.processor.Flag;
import com.project.dev.flag.processor.FlagMap;
import com.project.dev.selenium.generic.struct.Action;
import com.project.dev.selenium.generic.struct.Config;
import com.project.dev.selenium.generic.struct.Element;
import com.project.dev.selenium.generic.struct.Page;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * TODO: Definición de {@code replaceData}.
     *
     * @param jsonData
     * @param field
     * @return
     */
    public static String replaceData(@NonNull JSONObject jsonData, String field) {
        if (field != null) {
            for (Iterator iterator = jsonData.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                field = field.replaceAll("<" + key + ">", (String) jsonData.get(key));
            }
        }
        return field;
    }

    /**
     * TODO: Definición de {@code setConfigValues}.
     *
     * @param jsonConfig
     * @param configMap
     */
    public static void setConfigValues(JSONObject jsonConfig, @NonNull Map<String, Config> configMap) {
        if (jsonConfig != null) {
            String key;
            for (Iterator iterator = jsonConfig.keySet().iterator(); iterator.hasNext();) {
                key = (String) iterator.next();
                Config conf = configMap.get(key);
                try {
                    if (conf != null)
                        conf.setValue(conf.getType().cast(jsonConfig.get(key)));
                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println("Error setting config value to '" + key + "', using default.");
                }
            }
        }
        for (Map.Entry<String, Config> entry : configMap.entrySet())
            entry.getValue().setName(entry.getKey());
    }

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
            WebElement webElm = null;
            System.out.println(element);
            for (Action action : element.getActions()) {
                System.out.println("    " + action);
                try {
                    if (element.getId() != null)
                        webElm = driver.findElement(By.id(element.getId()));
                    else if (element.getName() != null)
                        webElm = driver.findElement(By.name(element.getName()));
                    else
                        webElm = driver.findElement(By.xpath(element.getXpath()));
                    action.executeAction(driver, webElm);
                } catch (Exception e) {
                    System.out.println("Error executing action in element: " + element);
                    System.out.println("Date:    " + DATETIME_FORMAT.format(new Date()));
                    System.out.println("Element: " + webElm);
                    System.out.println("Message: " + e.getMessage().split("\n")[0]);
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

        String actionsPackage = "com.project.dev.selenium.generic.struct.action.";
        Map<String, String> flagsMap = FlagMap.convertFlagsArrayToMap(flags);
        String chromeDriverPath = flagsMap.get("-chromeDriverPath");
        String navigationFilePath = flagsMap.get("-navigationFilePath");
        String dataFilePath = flagsMap.get("-dataFilePath");
        outputPath = flagsMap.get("-outputPath");
        String chromeUserDataDir = System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data";
        String chromeProfileDir = flagsMap.get("-chromeProfileDir");
        chromeUserDataDir = FlagMap.validateFlagInMap(flagsMap, "-chromeUserDataDir", chromeUserDataDir, String.class);

        Map<String, Config> configMap = new HashMap<>();
        configMap.put("start-date", Config.builder().type(String.class).defaultValue(null).build());
        configMap.put("load-page-timeout", Config.builder().type(Long.class).defaultValue(10000l).build());
        configMap.put("max-load-page-tries", Config.builder().type(Long.class).defaultValue(3l).build());
        configMap.put("max-action-page-tries", Config.builder().type(Long.class).defaultValue(5l).build());
        configMap.put("delay-time-before-retry", Config.builder().type(Long.class).defaultValue(2000l).build());
        configMap.put("delay-time-before-end", Config.builder().type(Long.class).defaultValue(1000l).build());

        if (!FileProcessor.validateFile(chromeDriverPath)) {
            System.out.println("Invalid file in flag '-chromeDriverPath'");
            result = false;
        } else if (!FileProcessor.validateFile(navigationFilePath)) {
            System.out.println("Invalid file in flag '-navigationFilePath'");
            result = false;
        } else if (!FileProcessor.validateFile(dataFilePath)) {
            System.out.println("Invalid file in flag '-dataFilePath'");
            result = false;
        } else if (!FileProcessor.validatePath(outputPath)) {
            System.out.println("Invalid path in flag '-outputPath'");
            result = false;
        } else if (!FileProcessor.validatePath(chromeUserDataDir)) {
            System.out.println("Invalid path in flag '-chromeUserDataDir'");
            result = false;
        } else {
            System.out.println("Reading config files...");
            //System.out.println("");

            JSONObject jsonNavigation;
            JSONObject jsonConfig = null;
            JSONArray jsonPages = null;
            JSONObject jsonData = null;
            JSONParser parser = new JSONParser();
            try {
                jsonNavigation = (JSONObject) parser.parse(new FileReader(navigationFilePath));
                System.out.println("File: '" + navigationFilePath + "' success readed.");
                try {
                    jsonConfig = (JSONObject) jsonNavigation.get("config");
                    System.out.println("Config loaded");
                    jsonPages = (JSONArray) jsonNavigation.get("pages");
                    System.out.println("Pages loaded");
                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println("Error getting 'config' or 'pages'");
                    result = false;
                }
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Error reading the file: '" + navigationFilePath + "'");
                result = false;
            }
            try {
                jsonData = (JSONObject) parser.parse(new FileReader(dataFilePath));
                System.out.println("File: '" + dataFilePath + "' success readed.");
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Error reading the file: '" + dataFilePath + "'");
                result = false;
            }

            setConfigValues(jsonConfig, configMap);
            System.out.println("Config:");
            System.out.println(configMap + "\n");
            String startDate = (String) configMap.get("start-date").getCanonicalValue();
            int loadPageTimeOut = (int) (long) configMap.get("load-page-timeout").getCanonicalValue();
            int maxLoadPageTries = (int) (long) configMap.get("max-load-page-tries").getCanonicalValue();
            int maxActionPageTries = (int) (long) configMap.get("max-action-page-tries").getCanonicalValue();
            int delayTimeBeforeRetry = (int) (long) configMap.get("delay-time-before-retry").getCanonicalValue();
            int delayTimeBeforeEnd = (int) (long) configMap.get("delay-time-before-end").getCanonicalValue();

            List<Page> pages = new ArrayList<>();
            List<String> urlPages = new ArrayList<>();

            if (result && jsonPages != null && jsonData != null) {
                int index = 0;
                for (Object currentPage : jsonPages) {
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
                        //System.out.println("Page:  " + page.getUrl());
                        //System.out.println("Delay: " + page.getDelay());
                        //System.out.println("Elements:");
                        for (Object currentElement : elements) {
                            try {
                                JSONObject jsonCurrentElement = (JSONObject) currentElement;
                                Element element = Element.builder()
                                        .id(replaceData(jsonData, (String) jsonCurrentElement.get("id")))
                                        .name(replaceData(jsonData, (String) jsonCurrentElement.get("name")))
                                        .xpath(replaceData(jsonData, (String) jsonCurrentElement.get("xpath")))
                                        .build();
                                List<Action> actions = new ArrayList<>();
                                for (Object currentAction : (JSONArray) jsonCurrentElement.get("actions")) {
                                    JSONObject jsonCurrentAction = (JSONObject) currentAction;
                                    String type = replaceData(jsonData, (String) jsonCurrentAction.get("type"));
                                    String value = replaceData(jsonData, (String) jsonCurrentAction.get("value"));
                                    long delay_element = (long) jsonCurrentAction.get("delay-before-next");
                                    JSONObject properties = (JSONObject) jsonCurrentAction.get("properties");
                                    properties = properties != null ? properties : new JSONObject();
                                    setConfigValues(properties, configMap);
                                    for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
                                        String key = (String) iterator.next();
                                        properties.put(key, replaceData(jsonData, (String) properties.get(key)));
                                    }
                                    properties.put("-outputPath", outputPath);
                                    String className = actionsPackage;
                                    String[] classNameAux = type.split("-");
                                    for (String name : classNameAux)
                                        className += name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

                                    Constructor cons = Class.forName(className).getConstructors()[0];

                                    Action action = (Action) cons.newInstance();
                                    action.setType(type);
                                    action.setValue(value);
                                    action.setDelay(delay_element);
                                    action.setProperties(properties);
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
                System.out.println("Pages:");
                for (Page page : pages) {
                    System.out.println(page);
                    for (Element element : page.getElements()) {
                        System.out.println("    " + element);
                        for (Action action : element.getActions())
                            System.out.println("        " + action);
                    }
                }
                System.out.println("");

                System.out.println("start-date: " + startDate);
                Date execDate = null;
                Date currentDate = new Date();
                if (startDate != null) {
                    try {
                        execDate = DATETIME_FORMAT.parse(startDate);
                        System.out.println("Parsed using '" + DATETIME_FORMAT.toLocalizedPattern() + "'");
                    } catch (Exception e1) {
                        System.out.println("Could not parse date using '" + DATETIME_FORMAT.toLocalizedPattern() + "'");
                        try {
                            execDate = DATETIME_FORMAT.parse(DATE_FORMAT.format(currentDate) + " " + startDate);
                            System.out.println("Parsed using '" + TIME_FORMAT.toLocalizedPattern() + "'");
                            if (execDate.before(currentDate)) {
                                execDate.setTime(execDate.getTime() + 86400000);
                            }
                        } catch (Exception e2) {
                            System.out.println("Could not parse date using '" + TIME_FORMAT.toLocalizedPattern() + "'");
                        }
                    }
                }

                if (execDate != null) {
                    currentDate = new Date();
                    System.out.println("current-date: " + DATETIME_FORMAT.format(currentDate));
                    System.out.println("start-date:   " + DATETIME_FORMAT.format(execDate));
                    long milliseconds = execDate.getTime() - currentDate.getTime();
                    System.out.println("Waiting " + milliseconds / 1000 + " seconds...\n");
                    try {
                        Thread.sleep(milliseconds);
                    } catch (InterruptedException e) {
                        System.out.println("Error waiting " + milliseconds / 1000 + " seconds\n");
                    }
                } else
                    System.out.println("Start time invalid or not specified\n");

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
