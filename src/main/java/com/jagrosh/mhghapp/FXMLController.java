/*
 * Copyright 2017 jagrosh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.mhghapp;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCClient.Status;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.jagrosh.mhghapp.entities.Activity;
import com.jagrosh.mhghapp.entities.MHGame;
import com.jagrosh.mhghapp.entities.Weapon;
import com.jagrosh.mhghapp.logging.TextAreaOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLController implements Initializable
{
    private static final Logger STLOG = LoggerFactory.getLogger("Status");
    private static final Logger LOGGER = LoggerFactory.getLogger("App");

    private static final String DISCORD = "https://discord.gg/MonsterHunter";
    private static final String TWITTER = "https://twitter.com/MHGatheringHall";
    private static final String GITHUB = "https://github.com/MHGatheringHall/App";
    private static final String WEBSITE = "https://mhgh.info";
    private static final String STEAM = "https://steamcommunity.com/groups/MonHunGatheringHall";
    private static final String TWITCH = "https://www.twitch.tv/mhgatheringhall";

    private IPCClient client;
    private HostServices hostServices;
    private Scene scene;
    @FXML private Label statusLbl;
    @FXML private ToggleButton partyBtn1;
    @FXML private ToggleButton partyBtn2;
    @FXML private ToggleButton partyBtn3;
    @FXML private ToggleButton partyBtn4;
    @FXML private Button clearBtn;
    @FXML private Button updateBtn;
    @FXML private TextField hallField;
    @FXML private TextField passField;
    @FXML private TextField questField;
    @FXML private ChoiceBox<MHGame> gameChoice;
    @FXML private ChoiceBox<Weapon> weaponChoice;
    @FXML private ChoiceBox<Activity> statusChoice;
    
    @FXML private ChoiceBox<DiscordBuild> clientChoice;
    @FXML private TextField cssField;
    
    @FXML private Hyperlink discordLink;
    @FXML private Hyperlink twitterLink;
    @FXML private Hyperlink githubLink;
    @FXML private Hyperlink websiteLink;
    @FXML private Hyperlink steamLink;
    @FXML private Hyperlink twitchLink;
    
    @FXML private TextArea console;
    
    @FXML
    private void updatePresence(ActionEvent event)
    {
        if(client==null || client.getStatus()!=Status.CONNECTED)
        {
            setStatus("Not connected! Trying to connect...");
            connect();
        }
        else if(gameChoice.getValue() == null)
            setStatus("Must select a game!");
        //else if(weaponChoice.getValue()==null)
            //setStatus("Must select a weapon!");
        else if(!gameChoice.getValue().getHallIdPattern().matcher(hallField.getText()).matches())
            setStatus("Invalid Hall ID!");
        else if(!passField.getText().matches("\\d{4}") && !passField.getText().equalsIgnoreCase("none") && !passField.getText().isEmpty())
            setStatus("Password 4 digits or empty!");
        else if(questField.getText().length()<2)
            setStatus("Quest description too short!");
        else if(questField.getText().length()>128)
            setStatus("Quest description too long!");
        else if(statusChoice.getValue()==null)
            setStatus("Must set a status!");
        else
        {
            MHGame g = gameChoice.getValue();
            Weapon w = weaponChoice.getValue();
            if(w==Weapon.NONE)
                w = null;
            Activity a = statusChoice.getValue();
            int s = partyBtn4.isSelected() ? 4 : partyBtn3.isSelected() ? 3 : partyBtn2.isSelected() ? 2 : 1;
            RichPresence rp = new RichPresence(a.toString(), questField.getText(), OffsetDateTime.now(), null, 
                    g.getAssetId(), g.getName(), w==null ? null : w.getAssetId(), w==null ? null : w.getName(), 
                    hallField.getText()+":"+(passField.getText().isEmpty() ? "NONE" : passField.getText().toUpperCase()),
                    s, 4, null, null, null, false);
            client.sendRichPresence(rp, new Callback(() -> setStatus("Updated!"), err -> setStatus("Failed to update :(")));
        }
    }
    
    @FXML
    private void clearPresence(ActionEvent event)
    {
        if(client==null || client.getStatus()!=Status.CONNECTED)
        {
            setStatus("Not connected! Trying to connect...");
            connect();
        }
        else
            client.sendRichPresence(null, new Callback(() -> setStatus("Cleared!"), err -> setStatus("Failed to clear :(")));
    }
    
    @FXML
    private void applySettings(ActionEvent event)
    {
        loadCss();
        saveSettings();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        PrintStream con=new PrintStream(new TextAreaOutputStream(console, 1000));
        System.setOut(con);
        System.setErr(con);
        ToggleGroup tg = new ToggleGroup();
        partyBtn1.setToggleGroup(tg);
        partyBtn2.setToggleGroup(tg);
        partyBtn3.setToggleGroup(tg);
        partyBtn4.setToggleGroup(tg);
        partyBtn1.setSelected(true);
        gameChoice.setItems(FXCollections.observableArrayList(MHGame.values()));
        weaponChoice.setItems(FXCollections.observableArrayList(Weapon.values()));
        statusChoice.setItems(FXCollections.observableArrayList(Activity.values()));
        clientChoice.setItems(FXCollections.observableArrayList(DiscordBuild.values()));

        gameChoice.setOnAction(event -> {
            hallField.promptTextProperty().setValue(gameChoice.getValue().getPromptText());

            // Clear the hall ID field
            // We won't clear the password because someone
            // might choose to have the same password between
            // games.
            hallField.clear();

            // Reset weapon choice
            weaponChoice.setValue(Weapon.NONE);

            MHGame game = gameChoice.getValue();

            List<Weapon> weapons = Stream.of(Weapon.values()).filter(weapon ->
            {
                // This weapon is present in all MH Games we support
                if(weapon.getGames().length == 0)
                    return true;

                for(MHGame g : weapon.getGames())
                {
                    // This weapon is present in a MH Game we are using
                    if(g == game)
                        return true;
                }

                return false;
            }).collect(Collectors.toList());

            weaponChoice.setItems(FXCollections.observableArrayList(weapons));
        });
        
        discordLink.setOnAction(e -> visit(discordLink, DISCORD));
        twitterLink.setOnAction(e -> visit(twitchLink, TWITTER));
        githubLink.setOnAction(e -> visit(githubLink, GITHUB));
        websiteLink.setOnAction(e -> visit(websiteLink, WEBSITE));
        steamLink.setOnAction(e -> visit(steamLink, STEAM));
        twitchLink.setOnAction(e -> visit(twitchLink, TWITCH));
        
        tg.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==null)
                oldValue.setSelected(true);
        });

        if(gameChoice.getValue() != null)
        {
            hallField.promptTextProperty().setValue(gameChoice.getValue().getPromptText());
        }
        
        hallField.textProperty().addListener((observable, oldValue, newValue) -> 
        {
            if(gameChoice.getValue() == null)
            {
                // Do not allow modification of preexisting
                hallField.setText(oldValue);
            }

            if(newValue.length() > oldValue.length())
            {
                // If there is no game selected or if we are playing MHW
                if(gameChoice.getValue() == MHGame.MHW)
                {
                    // Cut the new value back down capping it at 12 characters
                    if(newValue.length() > 12)
                        hallField.setText(oldValue);

                    // Prevent whitespace from being added
                    if(newValue.endsWith(" "))
                        hallField.setText(oldValue);
                }
                else
                {
                    if(newValue.length() > 17)
                    {
                        hallField.setText(oldValue);
                    }

                    char lastChar = newValue.charAt(newValue.length()-1);

                    // Prevent whitespace and non-numeric characters being added
                    if(!Character.isDigit(lastChar) && lastChar != '-')
                    {
                        hallField.setText(oldValue);
                        return;
                    }

                    if(newValue.matches("\\d{2}") || newValue.matches("\\d{2}-\\d{4}") ||
                       newValue.matches("\\d{2}-\\d{4}-\\d{4}"))
                    {
                        hallField.setText(newValue+"-");
                    }
                }
            }
            // Well, after some very frustrating debugging I discovered that JavaFX apparently
            // doesn't allow developers to make changes or alterations to textboxes when deleting
            // characters or content outside of a one character difference BELOW the start and end.
            // If you try to do this, it will give you the error:
            //
            // java.lang.IllegalArgumentException: The start must be <= the end
            //
            // Which might I just say, is really really really poor design, and shame on whoever
            // thought this was a good idea.

            /*// Deleting a '-' character
            else if(oldValue.length() > newValue.length() && oldValue.endsWith("-"))
            {
                hallField.setText(newValue.substring(0, newValue.length()-1));
            }*/
        });
        
        passField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.length() > 4)
                passField.setText(newValue.substring(0,4));
        });
        
        loadSettings();
        
        client = new IPCClient(192660688282320896L);
        client.setListener(new Listener());
        connect();
    }
    
    public void initObjects(HostServices host, Scene scene)
    {
        this.hostServices = host;
        this.scene = scene;
        loadCss();
    }
    
    public void setStatus(String text)
    {
        if(Platform.isFxApplicationThread())
        {
            STLOG.info(text);
            statusLbl.setText(text);
            statusLbl.setOpacity(1.0);
            FadeTransition ft = new FadeTransition(Duration.seconds(0.8));
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            Transition t = new FadeTransition(Duration.seconds(4.2));
            SequentialTransition st = new SequentialTransition(statusLbl, t, ft);
            st.playFromStart();
        }
        else Platform.runLater(() -> setStatus(text));
    }
    
    public void close()
    {
        saveSettings();
        client.close();
        System.exit(0);
    }
    
    private void saveSettings()
    {
        try
        {
            Properties props = new Properties();
            MHGame g = gameChoice.getValue();
            if(g==null)
                g = MHGame.values()[0];
            Weapon w = weaponChoice.getValue();
            if(w==null)
                w = Weapon.NONE;
            Activity a = statusChoice.getValue();
            if(a==null)
                a = Activity.values()[0];
            DiscordBuild c = clientChoice.getValue();
            if(c==null)
                c = DiscordBuild.ANY;
            props.setProperty("game", g.name());
            props.setProperty("weapon", w.name());
            props.setProperty("activity", a.name());
            props.setProperty("quest", questField.getText());
            props.setProperty("hall", hallField.getText());
            props.setProperty("pass", passField.getText());
            props.setProperty("client", c.name());
            props.setProperty("css", cssField.getText());
            props.store(new FileWriter("settings.properties"), "Settings for the MHGHApp");
        }
        catch(IOException ex)
        {
            LOGGER.error("Failed to save settings!");
        }
    }
    
    private void loadSettings()
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileReader("settings.properties"));
            MHGame g = MHGame.valueOf(props.getProperty("game"));
            Weapon w = Weapon.valueOf(props.getProperty("weapon"));
            Activity a = Activity.valueOf(props.getProperty("activity"));
            gameChoice.getSelectionModel().select(g);
            weaponChoice.getSelectionModel().select(w);
            statusChoice.getSelectionModel().select(a);
            questField.setText(props.getProperty("quest"));
            hallField.setText(props.getProperty("hall"));
            passField.setText(props.getProperty("pass"));
            
            DiscordBuild c = DiscordBuild.valueOf(props.getProperty("client"));
            clientChoice.getSelectionModel().select(c);
            cssField.setText(props.getProperty("css"));
            LOGGER.info("Successfully loaded settings.");
        }
        catch(IllegalArgumentException | IOException ex)
        {
            LOGGER.error("Failed to load existing settings.");
        }
    }
    
    private void loadCss()
    {
        if(Platform.isFxApplicationThread())
        {
            if(scene.getStylesheets().size()==2)
                scene.getStylesheets().remove(1);
            String loc = cssField.getText();
            if(loc.isEmpty())
                return;
            if(!(loc.startsWith("http://") || loc.startsWith("https://")))
            {
                try
                {
                    File f = new File(cssField.getText());
                    if(f.exists())
                        loc = "file:///" + f.getAbsolutePath().replace("\\", "/");
                    else
                    {
                        LOGGER.error("Could not find stylesheet: "+cssField.getText());
                        return;
                    }
                }
                catch(SecurityException ex)
                {
                    LOGGER.error("Unable to access stylesheet: "+cssField.getText());
                    return;
                }
            }
            try
            {
                scene.getStylesheets().add(loc);
            }
            catch(Exception e)
            {
                LOGGER.error("CSS is not in proper format or invalid.");
            }
            
        } else Platform.runLater(this::loadCss);
    }
    
    private void connect()
    {
        try
        {
            DiscordBuild c = clientChoice.getValue();
            if(c==null)
                c = DiscordBuild.ANY;
            client.connect(c, DiscordBuild.ANY);
            client.subscribe(IPCClient.Event.ACTIVITY_JOIN_REQUEST);
            client.subscribe(IPCClient.Event.ACTIVITY_JOIN);
        }
        catch (NoDiscordClientException ex)
        {
            setStatus("No Discord client found!");
        }
    }

    private void visit(Hyperlink link, String url) {
        hostServices.showDocument(url);
        // Set it so that we haven't visited the link yet
        // By default, FXML permanently highlights Hyperlinks
        // after they are clicked once.
        link.setVisited(false);
    }
    
    private class Listener implements IPCListener
    {
        @Override
        public void onReady(IPCClient client)
        {
            setStatus("Connected to Discord ("+client.getDiscordBuild().name().toLowerCase()+")");
        }

        @Override
        public void onDisconnect(IPCClient client, Throwable t)
        {
            setStatus("Disconnected: "+t);
        }

        @Override
        public void onClose(IPCClient client, JSONObject json)
        {
            setStatus("Connection closed.");
        }

        @Override
        public void onActivityJoinRequest(IPCClient client, String secret, User user)
        {
            setStatus(user.getName()+"#"+user.getDiscriminator()+" wants to join!");
        }

        @Override
        public void onPacketSent(IPCClient client, Packet packet)
        {
            LOGGER.trace(">> "+packet);
        }

        @Override
        public void onPacketReceived(IPCClient client, Packet packet)
        {
            LOGGER.trace("<< "+packet);
        }
        
        
    }
}
