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
public enum Activity
{
    ONQUEST("On a Quest"),
    PREPARE("Getting Ready"),
    FINDTEAM("Finding Teammates"),
    TURNS("Taking Turns"),
    AFK("AFK")
    ;
    
    private final String value;
    
    private Activity(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
