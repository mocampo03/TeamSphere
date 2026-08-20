package com.teamsphere.backend.service;

import com.teamsphere.backend.entity.Organization;
import com.teamsphere.backend.exception.ResourceNotFoundException;
import com.teamsphere.backend.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import com.teamsphere.backend.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization updateOrganization(Long id, Organization organizationDetails) {
        Organization organization = getOrganizationById(id);

        organization.setName(organizationDetails.getName());
        organization.setDescription(organizationDetails.getDescription());
        organization.setEmail(organizationDetails.getEmail());
        organization.setPhone(organizationDetails.getPhone());
        organization.setActive(organizationDetails.getActive());

        return organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        Organization organization = getOrganizationById(id);
        organizationRepository.delete(organization);
    }
}