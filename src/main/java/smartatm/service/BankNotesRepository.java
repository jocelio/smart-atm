package smartatm.service;

import smartatm.model.BankNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankNotesRepository extends JpaRepository<BankNotes, Integer> {
}
