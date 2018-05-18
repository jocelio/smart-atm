package smartatm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankNotes {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int note;
    private int amount;

    public BankNotes supplyNotes(Integer amount){
        return BankNotes.builder()
                .id(id)
                .note(note)
                .amount(this.amount + amount).build();
    }

    public BankNotes drecreaseNotes(Integer amount){
        return BankNotes.builder()
                .id(id)
                .note(note)
                .amount(this.amount - amount).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankNotes bankNotes = (BankNotes) o;
        return Objects.equals(note, bankNotes.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note);
    }
}
