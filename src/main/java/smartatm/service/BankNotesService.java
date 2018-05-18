package smartatm.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BankNotesService{

    public List<Integer[]> calcCombinations(int[] bankNotes, int[] ammounts, int value ) {
        return solutions(bankNotes, ammounts, new int[ammounts.length], value, 0);
    }

    private List<Integer[]> solutions(int[] values, int[] ammounts, int[] variation, int price, int position){
        List<Integer[]> list = new ArrayList<>();
        int value = calcVariationAmount(values, variation);
        if (value < price){
            for (int i = position; i < values.length; i++) {
                if (ammounts[i] > variation[i]){
                    int[] newvariation = variation.clone();
                    newvariation[i]++;
                    List<Integer[]> newList = solutions(values, ammounts, newvariation, price, i);
                    if (newList != null){
                        list.addAll(newList);
                    }
                }
            }
        } else if (value == price) {
            list.add(Arrays.stream(variation).boxed().toArray( Integer[]::new ));
        }
        return list;
    }

    private int calcVariationAmount(int[] values, int[] variation){
        int ret = 0;
        for (int i = 0; i < variation.length; i++) {
            ret += values[i] * variation[i];
        }
        return ret;
    }
}
