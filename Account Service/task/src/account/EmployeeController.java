package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@Validated
public class EmployeeController {

    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    private String getFormattedPeriod(String period) {
        String[] monthAndYear = period.split("-");
        String month = "";
        switch(monthAndYear[0]) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
        }

        return month + "-" + monthAndYear[1];
    }

    private String getFormattedSalary(long salary) {
        return salary/100 + " dollar(s) " + salary%100 + " cent(s)";
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<Object> checkPayment(@AuthenticationPrincipal UserDetailsImpl details,
            @RequestParam(required = false)  @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}") String period) {

        if(period != null) {
            Optional<Payroll> payroll = payrollRepository.findById(new PayrollId(details.getEmail(), period));

            Payroll value = payroll.orElseThrow();

            UserSalary salary = new UserSalary(details.getName(),
                    details.getLastname(),
                    getFormattedPeriod(value.getPeriod()),
                    getFormattedSalary(value.getSalary()));

            return new ResponseEntity<>(salary, HttpStatus.OK);

        }else {
            List<UserSalary> salaries = new ArrayList<>();
            List<Optional<Payroll>> payrolls = payrollRepository.findByEmployee(details.getEmail());
            payrolls.sort(Collections.reverseOrder(new SortByDate()));

            payrolls.forEach(payroll -> {
                payroll.ifPresent(value -> {
                    salaries.add(new UserSalary(details.getName(),
                            details.getLastname(),
                            getFormattedPeriod(value.getPeriod()),
                            getFormattedSalary(value.getSalary())));
                });
            });
            return new ResponseEntity<>(salaries, HttpStatus.OK);
        }
    }


    @PostMapping("/api/acct/payments")
    public ResponseEntity<Map<String, String>> uploadPayrolls(@RequestBody List<@Valid Payroll> payrolls) {

        validateAndAddPayrolls(payrolls);

        Map<String, String> map = new HashMap<>();
        map.put("status", "Added successfully!");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/api/acct/payments")
    public ResponseEntity<Map<String, String>> changeSalaryOfSpecificUser(@RequestBody @Valid Payroll payroll) {
        UserDetails existingUser = userDetailsServiceImpl.loadUserByUsername(payroll.getEmployee());
        if(existingUser == null) {
            throw new BadRequestException("Employee does not exist");
        }

        payrollRepository.save(payroll);

        Map<String, String> map = new HashMap<>();
        map.put("status", "Updated successfully!");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Transactional
    public void validateAndAddPayrolls(List<Payroll> payrolls) {
        payrolls.forEach(payroll -> {
            UserDetails existingUser = userDetailsServiceImpl.loadUserByUsername(payroll.getEmployee());
            if(existingUser == null) {
                throw new BadRequestException("Employee does not exist");
            }
            Optional<Payroll> existingPayroll = payrollRepository.findById(new PayrollId(payroll.getEmployee(), payroll.getPeriod()));
            if(existingPayroll.isPresent()) {
                throw new BadRequestException("Payroll already present");
            }

            payrollRepository.save(payroll);
        });
    }
}
