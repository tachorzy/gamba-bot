package org.example;

import java.util.List;

public class BadgeBuilder {
    String badge;

    //returns badge in discords emote tag format
    public String buildBadge(List<String> badgeDetails, String badgeName) {

        String badgeID = badgeDetails.get(0);
        String badgeTag = badgeDetails.get(2);
        String badgeType = badgeDetails.get(3);

        //since discord has two types of emotes  a stands for animated gifs  and without a is just the static emotes
        if (badgeType.equals("animated"))
            return badge = "<a:" + badgeName + ":" + badgeID + "> " + badgeTag;
        else if (badgeType.equals("static"))
            return badge = "<:" + badgeName + ":" + badgeID + "> " + badgeTag;
        else
            return badgeID + " " + badgeTag;
    }
}

