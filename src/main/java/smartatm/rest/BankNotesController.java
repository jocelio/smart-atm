package smartatm.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smartatm.model.BankNotes;
import smartatm.service.BankNotesRepository;
import smartatm.service.BankNotesService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/banknotes")
@AllArgsConstructor
public class BankNotesController {

    BankNotesRepository bankNotesRepository;

    BankNotesService bankNotesService;
    
    @PostMapping("/supply")
    public List<BankNotes> supply(@RequestBody List<BankNotes> bankNotes){

        List<BankNotes> notes = bankNotesRepository.findAll();

        List<BankNotes> suppliedNotes = bankNotes.stream().map(bn ->
                notes.stream().filter(f -> f.equals(bn))
                        .findFirst().orElse(BankNotes.builder().note(bn.getNote()).build()).supplyNotes(bn.getAmount())
        ).collect(toList());

        return bankNotesRepository.save(suppliedNotes);
    }

    @PostMapping("/options/{value}")
    public List<List<BankNotes>> options(@PathVariable Integer value){

        List<BankNotes> notesList = bankNotesRepository.findAll();
        int[] availableNotes = notesList.stream().mapToInt(n -> n.getNote()).toArray();
        int[] availableNotesAmount = notesList.stream().mapToInt(n -> n.getAmount()).toArray();
        return bankNotesService.calcCombinations(availableNotes, availableNotesAmount, value);

    }

    @PostMapping("/withdraw/")
    public List<BankNotes> withdraw(@RequestBody List<BankNotes> bankNotes){

        List<BankNotes> notes = bankNotesRepository.findAll();

        bankNotes.stream().map(bn ->
                notes.stream().filter(f -> f.equals(bn))
                        .findFirst().orElse(BankNotes.builder().note(bn.getNote()).build()).drecreaseNotes(bn.getAmount())
        ).collect(toList()).forEach(bankNotesRepository::save);

        return bankNotesRepository.findAll();
    }



    @GetMapping
    public List<BankNotes> getAll(){
        return bankNotesRepository.findAll();
    }
}
