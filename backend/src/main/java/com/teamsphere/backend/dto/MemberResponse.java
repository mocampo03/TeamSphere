package com.teamsphere.backend.dto;

public class MemberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private Boolean active;
    private Long organizationId;

    public MemberResponse() {
    }

    public MemberResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String position,
            Boolean active,
            Long organizationId) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.active = active;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPosition() {
        return position;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}