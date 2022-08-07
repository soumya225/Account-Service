package account.comparators;

import account.models.Payroll;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

public class SortByDate implements Comparator<Optional<Payroll>> {

    public int compare(Optional<Payroll> a, Optional<Payroll> b)
    {
        if(a.isPresent() && b.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy", Locale.ENGLISH);
            YearMonth periodA = YearMonth.parse(a.get().getPeriod(), formatter);
            YearMonth periodB = YearMonth.parse(b.get().getPeriod(), formatter);

            return periodA.compareTo(periodB);
        }
        return 0;
    }
}