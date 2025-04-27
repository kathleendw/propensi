package b02Propensi.siladu.user.service;

import java.util.List;

import b02Propensi.siladu.user.dto.UpdatePasswordDTO;
import b02Propensi.siladu.user.dto.UpdateProfileDTO;
import b02Propensi.siladu.user.dto.UserRegistrationDTO;
import b02Propensi.siladu.user.model.Role;
import b02Propensi.siladu.user.model.UserModel;

public interface UserService {
    UserModel save(UserRegistrationDTO registrationDTO);

    UserModel findByEmail(String email);

    UserModel findById(Long id);

    UserModel updatePremium(UserModel user);

    UserModel updateProfile(UserModel user, UpdateProfileDTO updateProfileDTO);

    UserModel updatePassword(UserModel user, UpdatePasswordDTO updatePasswordDTO);

    void deleteAccount(UserModel user);

    Boolean validatePassword(String password);

    List<UserModel> findAll();

    void setLastSignIn(UserModel user);

    List<UserModel> findMembers();

    List<Integer> findActiveMemberByMonth();

    List<Integer> findActiveMemberByDay();

    Long findAllMember(Role role);

    List<Long> countNewPremiumMembersByMonth(int year);

    Double calculateBasicMemberPercentage();
    Double calculatePremiumMemberPercentage();
    long countTotalPremiumMembers();
    
    
}
