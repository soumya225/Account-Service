package account.controllers;

import account.DBService;
import account.comparators.SortByRole;
import account.models.ChangeRole;
import account.models.Operation;
import account.models.RoleType;
import account.UserDetailsImpl;
import account.UserDetailsServiceImpl;
import account.models.UserInfoReceipt;
import account.exceptions.BadRequestException;
import account.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(
        value = "/api/admin",
        method = {RequestMethod.GET, RequestMethod.DELETE}
)
public class AdminController {
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    DBService dbService;

    @GetMapping("/user")
    public ResponseEntity<List<UserInfoReceipt>> getUsers() {
        List<UserDetailsImpl> userDetailsList = userDetailsServiceImpl.getUsers();

        List<UserInfoReceipt> receipts = new ArrayList<>();

        userDetailsList.forEach(details -> {
            details.getUser().getRoles().sort(new SortByRole());

            List<RoleType> roleTypes = new ArrayList<>();
            details.getUser().getRoles().forEach(role -> roleTypes.add(role.getRoleType()));
            receipts.add(
                    new UserInfoReceipt(
                            details.getId(),
                            details.getName(),
                            details.getLastname(),
                            details.getEmail(),
                            roleTypes
                    ));
        });

        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@AuthenticationPrincipal UserDetails loggedInUser,
                                                          @PathVariable String email) {

        UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByUsername(email);

        if(userDetails == null) {
            throw new NotFoundException("User not found!");
        }

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_ADMINISTRATOR.toString()))) {
            throw new BadRequestException("Can't remove ADMINISTRATOR role!");
        }

        userDetailsServiceImpl.deleteUserByUsername(email);

        Map<String, String> map = new HashMap<>();
        map.put("user", email);
        map.put("status", "Deleted successfully!");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/user/role")
    public ResponseEntity<UserInfoReceipt> setRoleOfUser(@Valid @RequestBody ChangeRole changeRole) {
        UserDetailsImpl details = userDetailsServiceImpl.loadUserByUsername(changeRole.getUser());

        if(details == null) {
            throw new NotFoundException("User not found!");
        }


        RoleType roleFound = null;
        for(RoleType roleType: RoleType.values()){
            if (changeRole.getRole().equals(roleType.getRole())) {
                roleFound = roleType;
                break;
            }
        }

        if(roleFound == null) {
            throw new NotFoundException("Role not found!");
        }



        if(changeRole.getOperation().equals(Operation.GRANT.toString())) {
            if (details.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_ADMINISTRATOR.toString()))) {
                if(roleFound.equals(RoleType.ROLE_USER) || roleFound.equals(RoleType.ROLE_ACCOUNTANT)) {
                    throw new BadRequestException("The user cannot combine administrative and business roles!");
                }
            } else if (details.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_USER.toString())
                            || a.getAuthority().equals(RoleType.ROLE_ACCOUNTANT.toString()))) {
                if(roleFound.equals(RoleType.ROLE_ADMINISTRATOR)) {
                    throw new BadRequestException("The user cannot combine administrative and business roles!");
                }
            }

            dbService.addRoleToUser(details.getUser(), roleFound);
        } else if (changeRole.getOperation().equals(Operation.REMOVE.toString())) {

            if(roleFound.equals(RoleType.ROLE_ADMINISTRATOR)) {
                throw new BadRequestException("Can't remove ADMINISTRATOR role!");
            }

            if(details.getUser().getRoles().size() == 1
                    && details.getUser().getRoles().get(0).getRoleType().equals(roleFound)) {
                throw new BadRequestException("The user must have at least one role!");
            }

            if(details.getUser().getRoles().stream()
                    .noneMatch(a -> a.getRoleType().getRole().equals(changeRole.getRole()))){
                throw new BadRequestException("The user does not have a role!");
            }

            dbService.removeRoleFromUser(details.getUser(), roleFound);
        } else {
            throw new BadRequestException("Operation not found");
        }

        details.getUser().getRoles().sort(new SortByRole());

        List<RoleType> roleTypes = new ArrayList<>();
        details.getUser().getRoles().forEach(role -> roleTypes.add(role.getRoleType()));

        return new ResponseEntity<>(new UserInfoReceipt(
                details.getId(),
                details.getName(),
                details.getLastname(),
                details.getEmail(),
                roleTypes
        ), HttpStatus.OK);
    }
}
