package br.gov.smartatm.service;

import org.springframework.stereotype.Service;
import br.gov.smartatm.model.BankNotes;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BankNotesService {

    public static final int MAX_NOTES = 3;

    public List<BankNotes> calculateNotesUpdate(List<BankNotes> withdrawOption, List<BankNotes> availableNotes){
        return withdrawOption.stream().map(bn ->
                availableNotes.stream().filter(f -> f.equals(bn))
                        .findFirst().orElse(BankNotes.builder().note(bn.getNote()).build()).drecreaseNotes(bn.getAmount())
        ).collect(toList());
    }

    public List<List<BankNotes>> calcCombinations(final int[] bankNotes, final int[] ammounts, final int value) {
        List<List<BankNotes>> result = resolution(bankNotes, ammounts, new int[ammounts.length], value, 0);
        return result.stream()
                    .filter(l -> l.stream().filter(f -> f.getAmount() != 0).count() <= MAX_NOTES )
                    .collect(toList());
    }

    private List<List<BankNotes>> resolution(final int[] values, final int[] amounts, final int[] variation, final int price, final int position){
        List<List<BankNotes>> list = new ArrayList<>();
        int value = calcVariationAmount(values, variation);
        if (value < price){
            for (int i = position; i < values.length; i++) {
                if (amounts[i] > variation[i]){
                    int[] newVariation = variation.clone();
                    newVariation[i]++;
                    List<List<BankNotes>> newList = resolution(values, amounts, newVariation, price, i);
                    if (newList != null){
                        list.addAll(newList);
                    }
                }
            }
        } else if (value == price) {
            List<BankNotes> notes = new ArrayList<>();
            for (int i = 0; i < variation.length; i++) {
                notes.add(BankNotes.builder().amount(variation[i]).note(values[i]).build());
            }
            list.add(notes);
        }
        return list;
    }

    private int calcVariationAmount(final int[] values, final int[] variation){
        int ret = 0;
        for (int i = 0; i < variation.length; i++) {
            ret += values[i] * variation[i];
        }
        return ret;
    }
}
