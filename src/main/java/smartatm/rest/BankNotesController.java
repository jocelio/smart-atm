package smartatm.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smartatm.model.BankNotes;
import smartatm.service.BankNotesRepository;
import smartatm.service.BankNotesService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/banknotes")
@AllArgsConstructor
public class BankNotesController {

    BankNotesRepository bankNotesRepository;

    BankNotesService bankNotesService;
    
    @PostMapping("/supply")
    public List<BankNotes> supply(@RequestBody final List<BankNotes> bankNotes){

        List<BankNotes> notes = bankNotesRepository.findAll();

        List<BankNotes> suppliedNotes = bankNotes.stream().map(bn ->
                notes.stream().filter(f -> f.equals(bn))
                        .findFirst().orElse(BankNotes.builder().note(bn.getNote()).build()).supplyNotes(bn.getAmount())
        ).collect(toList());

        return bankNotesRepository.save(suppliedNotes);
    }

    @PostMapping("/options/{value}")
    public List<List<BankNotes>> options(@PathVariable final Integer value){

        return getWithdrawOptions(value);

    }

    @PostMapping("/withdraw/")
    public List<BankNotes> withdraw(@RequestBody final List<BankNotes> withdrawOption){

        List<BankNotes> availableNotes = bankNotesRepository.findAll();

        bankNotesRepository.save(bankNotesService.calculateNotesUpdate(withdrawOption, availableNotes));

        return bankNotesRepository.findAll();
    }


    @PostMapping("/best-option/{value}")
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

    @GetMapping
    public List<BankNotes> getAll(){
        return bankNotesRepository.findAll();
    }

    private List<List<BankNotes>> getWithdrawOptions(Integer value){
        final List<BankNotes> notes = bankNotesRepository.findAll();
        return bankNotesService.calcCombinations(toArray(notes, BankNotes::getNote), toArray(notes, BankNotes::getAmount), value);
    }

    private int[] toArray(List<BankNotes> list, ToIntFunction<? super BankNotes> mapper){
        return list.stream().mapToInt(mapper).toArray();
    }

}
