package com.example.clubhub.club;

import com.google.firebase.firestore.DocumentSnapshot;

public class Club {
    private String id, name, description, type, createdById;
    private boolean isPublic;

    public Club(String id, String name, String description, String type, String createdById, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdById = createdById;
        this.isPublic = isPublic;
    }

    public static Club fromFirestore(DocumentSnapshot doc) {
        return new Club(
                doc.getId(),
                doc.getString("name"),
                doc.getString("description"),
                doc.getString("type"),
                doc.getString("createdById"),
                Boolean.TRUE.equals(doc.getBoolean("isPublic"))
        );
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isPublic() { return isPublic; }
    public String getCreatedById() { return createdById; }
}