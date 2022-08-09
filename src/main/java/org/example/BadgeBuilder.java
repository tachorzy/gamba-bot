package org.example;

import java.util.List;

public class BadgeBuilder {
    String badge;
    public String buildBadge(List<String> badgeDetails, String badgeName) {
        String badgeID = badgeDetails.get(0);
        String badgeTag = badgeDetails.get(2);
        String badgeType = badgeDetails.get(3);

        if (badgeType.equals("animated"))
            return badge = "<a:" + badgeName + ":" + badgeID + "> " + badgeTag;
        else if (badgeType.equals("static"))
            return badge = "<:" + badgeName + ":" + badgeID + "> " + badgeTag;
        else
            return badgeID + " " + badgeTag;
    }
}

