package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role saveRole(Role role);

    Role updateRole(Role role);

    Optional<Role> getRoleById(Long id);

    Optional<Role> getRoleByNom(String nom);

    List<Role> getAllRoles();

    void deleteRole(Long id);
}