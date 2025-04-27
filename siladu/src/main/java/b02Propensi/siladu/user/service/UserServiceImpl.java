package b02Propensi.siladu.user.service;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.time.YearMonth;
import java.util.Calendar;

import b02Propensi.siladu.user.authentication.PasswordConfig;
import b02Propensi.siladu.user.dto.UpdatePasswordDTO;
import b02Propensi.siladu.user.dto.UpdateProfileDTO;
import b02Propensi.siladu.user.dto.UserRegistrationDTO;
import b02Propensi.siladu.user.model.Role;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.repository.UserDb;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserDb userDb;

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserModel findByEmail(String email) {
        Optional<UserModel> user = userDb.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else
            return null;

    }

    @Override
    public UserModel findById(Long id) {
        Optional<UserModel> user = userDb.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NoSuchElementException();
        }

    }

    @Override
    public UserModel updatePremium(UserModel user) {
        user.setPremium(true);
        user.setRole(Role.MEMBER_PREMIUM);
        user.setTanggalUpgradePremium(LocalDate.now()); // Set tanggal upgrade premium ke hari ini
        userDb.save(user);
        return user;
    }

    // untuk bar chart member premium baru per bulan
    @Override
    public List<Long> countNewPremiumMembersByMonth(int year) {
        List<Long> newPremiumMembersByMonth = new ArrayList<>();

        // Loop through each month
        for (int i = 1; i <= 12; i++) {
            LocalDate startDate = LocalDate.of(year, i, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // Count new premium members for the current month
            Long count = userDb.countNewPremiumMembersByMonth(startDate, endDate);
            newPremiumMembersByMonth.add(count != null ? count : 0);
        }

        return newPremiumMembersByMonth;
    }

    // @PostConstruct
    // public void generateDummyDataIfNeeded() {
    //     // long count = userDb.count();
    //     // if (count == 0) {
    //     List<UserModel> dummyUsers = generateDummyPremiumUsers(100); // Misalnya, 100 pengguna
    //     dummyUsers.forEach(userDb::save); // Simpan setiap pengguna ke dalam database
    //     // }
    // }

    // private List<UserModel> generateDummyPremiumUsers(int numberOfUsers) {
    //     List<UserModel> users = new ArrayList<>();
    //     Random random = new Random();

    //     for (int i = 0; i < numberOfUsers; i++) {
    //         UserModel user = new UserModel();
    //         user.setName("User " + i);

    //         // Generate unique email
    //         String email;
    //         do {
    //             email = "user" + i + "_" + random.nextInt(1000) + "@example.com";
    //         } while (userDb.findByEmail(email).isPresent()); // Check if email already exists in the database

    //         user.setEmail(email);

    //         user.setPassword("Password123"); // Change with your password generation logic
    //         user.setPremium(true);
    //         // Generate random role
    //         Role randomRole = random.nextBoolean() ? Role.MEMBER_PREMIUM : Role.MEMBER_BASIC;
    //         user.setRole(randomRole);
    //         user.setIsActive(true);
    //         user.setIsPassUpdated(true);

    //         // Generate random tanggalUpgradePremium in range 2023-2024
    //         int year = random.nextInt(4) + 2022; // Random year between 2023 and 2024
    //         int month = random.nextInt(12) + 1; // Random month between 1 and 12
    //         int day = random.nextInt(28) + 1; // Random day between 1 and 28 (assuming no leap year)

    //         user.setTanggalUpgradePremium(LocalDate.of(year, month, day));

    //         users.add(user);
    //     }

    //     return users;
    // }

    @Override
    public UserModel save(UserRegistrationDTO registrationDTO) {
        UserModel user = new UserModel();

        user.setEmail(registrationDTO.getEmail());
        user.setName(registrationDTO.getName());
        user.setPassword(PasswordConfig.encrypt(registrationDTO.getPassword()));
        user.setIsActive(true);
        user.setIsPassUpdated(true);

        System.out.println("tes service: " + registrationDTO.getRole());

        // if (registrationDTO.getRole().equals("basic")){
        // user.setRole(Role.MEMBER_BASIC);
        //
        // } else if (registrationDTO.getRole().equals("premium")){
        // user.setRole(Role.MEMBER_PREMIUM);
        // user.setPremium(true); //to be changed
        // }

        user.setRole(Role.MEMBER_BASIC);

        return userDb.save(user);

    }

    @Override
    public void deleteAccount(UserModel user) {
        userDb.delete(user);
    }

    @Override
    public UserModel updateProfile(UserModel user, UpdateProfileDTO updateProfileDTO) {
        user.setEmail(updateProfileDTO.getEmail());
        user.setName(updateProfileDTO.getName());
        user.setUmkm(updateProfileDTO.getUmkm());
        user.setTelephone(updateProfileDTO.getTelephone());

        return userDb.save(user);
    }

    @Override
    public UserModel updatePassword(UserModel user, UpdatePasswordDTO updatePasswordDTO) {
        user.setPassword(PasswordConfig.encrypt(updatePasswordDTO.getPassword()));

        return userDb.save(user);
    }

    @Override
    public Boolean validatePassword(String password) {

        if (password.length() < 6) {
            return false;
        }

        boolean containsDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                containsDigit = true;
                break;
            }
        }

        boolean containsUpperCase = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                containsUpperCase = true;
                break;
            }
        }

        boolean containsLowerCase = false;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                containsLowerCase = true;
                break;
            }
        }

        return containsDigit & containsLowerCase & containsUpperCase;
    }

    @Override
    public List<UserModel> findAll() {
        return userDb.findAll();
    }

    @Override
    public List<UserModel> findMembers() {
        List<UserModel> listOfMembers = new ArrayList<>();

        List<UserModel> listOfUsers = findAll();

        for (UserModel user : listOfUsers){
            if (user.getRole().toString().equals("MEMBER_BASIC") | user.getRole().toString().equals("MEMBER_PREMIUM")){
                listOfMembers.add(user);
            }
        }

        return listOfMembers;
    }

    @Override
    public void setLastSignIn(UserModel user){
        user.setLastSignIn(new Date());
        userDb.save(user);

    }

    @Override
    public List<Integer> findActiveMemberByMonth() {
        List<Integer> chartData = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = 1; i <= 12; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.MONTH, i - 1);
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDayOfMonth = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
            Date lastDayOfMonth = calendar.getTime();

            long activeMembers = userDb.countByRoleAndLastSignInBetween(Role.MEMBER_BASIC, firstDayOfMonth, lastDayOfMonth)
                    + userDb.countByRoleAndLastSignInBetween(Role.MEMBER_PREMIUM, firstDayOfMonth, lastDayOfMonth);
            chartData.add((int) activeMembers);
        }

        return chartData;
    }

    @Override
    public List<Integer> findActiveMemberByDay() {
        List<Integer> chartData = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startOfWeek = calendar.getTime();
        System.out.println("start: " + startOfWeek);

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endOfWeek = calendar.getTime();
        System.out.println("end: " + endOfWeek);

        while (startOfWeek.before(endOfWeek) || startOfWeek.equals(endOfWeek)) {
            Date startOfDay = getStartOfDay(startOfWeek);
            Date endOfDay = getEndOfDay(startOfWeek);

            long activeMembers = userDb.countByRoleAndLastSignInBetween(Role.MEMBER_BASIC, startOfDay, endOfDay)
                    + userDb.countByRoleAndLastSignInBetween(Role.MEMBER_PREMIUM, startOfDay, endOfDay);
            chartData.add((int) activeMembers);


            calendar.setTime(startOfWeek);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startOfWeek = calendar.getTime();
        }

        return chartData;
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    @Override
    public Long findAllMember(Role role){
        return userDb.countByRole(role);
    }



    
    public Double calculateBasicMemberPercentage() {
        Long totalMembers = userDb.count(); // Total semua member
        Long totalBasicMembers = userDb.countByRole(Role.MEMBER_BASIC); // Total member basic
        if (totalMembers == 0) {
            return 0.0; // Menghindari pembagian oleh nol
        }
        return (totalBasicMembers.doubleValue() / totalMembers.doubleValue()) * 100; // Hitung persentase member basic
    }

    @Override
    public Double calculatePremiumMemberPercentage() {
        Long totalMembers = userDb.count(); // Total semua member
        Long totalPremiumMembers = userDb.countByRole(Role.MEMBER_PREMIUM); // Total member premium
        if (totalMembers == 0) {
            return 0.0; // Menghindari pembagian oleh nol
        }
        return (totalPremiumMembers.doubleValue() / totalMembers.doubleValue()) * 100; // Hitung persentase member
                                                                                       // premium
    }
    @Override
    public long countTotalPremiumMembers() {
        // Assuming 'Role.PREMIUM' correctly identifies premium members
        // Ensure that you have a role set up for premium users as 'PREMIUM' or adjust the condition accordingly
        return userDb.countByRole(Role.MEMBER_PREMIUM);
    }

}
