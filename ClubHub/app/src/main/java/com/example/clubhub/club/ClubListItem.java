package com.example.clubhub.club;

public class ClubListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CLUB = 1;

    public int type;
    public String headerTitle;
    public Club club;

    public ClubListItem(int type, String headerTitle, Club club) {
        this.type = type;
        this.headerTitle = headerTitle;
        this.club = club;
    }

    public int getType() {
        return type;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public Club getClub() {
        return club;
    }
}
