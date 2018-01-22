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
package com.jagrosh.mhghapp.entities;

import java.util.regex.Pattern;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public enum MHGame
{
    MH4U("Monster Hunter 4 Ultimate", "mh4u"),
    MHGEN("Monster Hunter Generations", "mhgen"),
    MHXX("Monster Hunter XX", "mhxx"),
    MHW("Monster Hunter World", "mhw", "XXXXXXXXXX", Pattern.compile("[A-Za-z1-9]{9,12}"))
    ;
    
    private final String name, asset, promptText;
    private final Pattern hallIdPattern;
    
    MHGame(String name, String asset)
    {
        this.name = name;
        this.asset = asset;
        this.promptText = "00-0000-0000-0000";
        this.hallIdPattern = Pattern.compile("\\d{2}-\\d{4}-\\d{4}-\\d{4}");
    }

    MHGame(String name, String asset, String promptText, Pattern hallIdPattern)
    {
        this.name = name;
        this.asset = asset;
        this.promptText = promptText;
        this.hallIdPattern = hallIdPattern;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getAssetId()
    {
        return "game-"+asset;
    }

    public Pattern getHallIdPattern()
    {
        return hallIdPattern;
    }

    public String getPromptText() {
        return promptText;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
