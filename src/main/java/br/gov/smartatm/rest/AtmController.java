package br.gov.smartatm.rest;

import br.gov.smartatm.model.BankNotes;
import br.gov.smartatm.service.BankNotesRepository;
import br.gov.smartatm.service.BankNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/atm")
@CrossOrigin(origins = "http://localhost:8000")
public class AtmController {

    @Autowired
    private BankNotesRepository bankNotesRepository;

    @Autowired
    private BankNotesService bankNotesService;

    @PostMapping("/supply")
    public List<BankNotes> supply(@RequestBody final List<BankNotes> bankNotes){

        List<BankNotes> notes = bankNotesRepository.findAll();

        List<BankNotes> suppliedNotes = bankNotes.stream().map(bn ->
                notes.stream().filter(f -> f.equals(bn))
                        .findFirst().orElse(BankNotes.builder().note(bn.getNote()).build()).supplyNotes(bn.getAmount())
        ).collect(toList());

        List<BankNotes> save = bankNotesRepository.save(suppliedNotes);

        return bankNotesRepository.findAll();
    }

    @GetMapping("/options/{value}")
    public List<List<BankNotes>> options(@PathVariable final Integer value){

        return getWithdrawOptions(value);

    }

    @PostMapping("/withdraw/")
    public List<BankNotes> withdraw(@RequestBody final List<BankNotes> withdrawOption){

        List<BankNotes> availableNotes = bankNotesRepository.findAll();

        bankNotesRepository.save(bankNotesService.calculateNotesUpdate(withdrawOption, availableNotes));

        return bankNotesRepository.findAll();
    }


    @GetMapping("/best-option/{value}")
    public List<BankNotes> bestOption(@PathVariable final Integer value){

        List<BankNotes> availableNotes = bankNotesRepository.findAll();
        List<List<BankNotes>> withdrawOptions = getWithdrawOptions(value);

        List<BankNotes> bestUniformity = new ArrayList<>();
        Integer range = Integer.MAX_VALUE;

        for (List<BankNotes> wo: withdrawOptions){

            List<BankNotes> bankNotesAfterWithdraw = bankNotesService.calculateNotesUpdate(wo, availableNotes);

            BankNotes biggerAmount = bankNotesAfterWithdraw.stream().sorted(comparing(BankNotes::getAmount).reversed()).findFirst().get();
            BankNotes smallerAmount = bankNotesAfterWithdraw.stream().sorted(comparing(BankNotes::getAmount)).findFirst().get();
            final int localRange = biggerAmount.getAmount() - smallerAmount.getAmount();

            if(localRange < range){
                range = localRange;
                bestUniformity = wo;
            }

        }

        return bestUniformity;
    }

    @DeleteMapping
    public void clearNotes(){
         bankNotesRepository.deleteAll();
    }

    @GetMapping
    public List<BankNotes> getAll(){
        return bankNotesRepository.findAll();
    }

    private List<List<BankNotes>> getWithdrawOptions(Integer value){
        final List<BankNotes> notes = bankNotesRepository.findAll().stream().filter(f -> f.getAmount() >= 0).collect(toList());
        return bankNotesService.calcCombinations(toArray(notes, BankNotes::getNote), toArray(notes, BankNotes::getAmount), value);
    }

    private int[] toArray(List<BankNotes> list, ToIntFunction<? super BankNotes> mapper){
        return list.stream().mapToInt(mapper).toArray();
    }

}
