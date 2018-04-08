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

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public enum Weapon
{
    NONE("", null),
    GREATSWORD("Great Sword",     "great"),
    LONGSWORD("Long Sword",       "long"),
    SWORD("Sword and Shield",     "sns"),
    DUALBLADES("Dual Blades",     "dual"),
    HAMMER("Hammer",              "hammer"),
    HUNTINGHORN("Hunting Horn",   "horn"),
    LANCE("Lance",                "lance"),
    GUNLANCE("Gunlance",          "glance"),
    SWITCHAXE("Switch Axe",       "switch"),
    CHARGEBLADE("Charge Blade",   "charge"),
    INSECTGLAIVE("Insect Glaive", "insect"),
    LIGHTBOWGUN("Light Bowgun",   "lbg"),
    HEAVYBOWGUN("Heavy Bowgun",   "hbg"),
    BOW("Bow",                    "bow"),
    PROWLER("Prowler",            "prowler", MHGame.MHGEN, MHGame.MHXX)
    ;
    
    
    private final String asset, name;
    private final MHGame[] games;
    
    private Weapon(String name, String asset, MHGame... games)
    {
        this.name = name;
        this.asset = asset;
        this.games = games; // Empty games array means all games support this weapon
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getAssetId()
    {
        return "weapon-"+asset;
    }

    public MHGame[] getGames()
    {
        return games;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
