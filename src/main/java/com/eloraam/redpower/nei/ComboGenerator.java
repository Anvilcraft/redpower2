package com.eloraam.redpower.nei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ComboGenerator {
    public static List<LinkedList<Integer>> generate(int sum) {
        if (sum < 2) {
            return null;
        } else {
            List<LinkedList<Integer>> combos = new ArrayList();
            if (sum == 2) {
                combos.add(new LinkedList(Arrays.asList(1, 1)));
                return combos;
            } else {
                for (int base = 1; base <= sum / 2; ++base) {
                label37:
                    for (LinkedList<Integer> combo : generate(sum - base)) {
                        for (Integer i : combo) {
                            if (i < base) {
                                continue label37;
                            }
                        }

                        combo.addFirst(base);
                        combos.add(combo);
                    }

                    combos.add(new LinkedList(Arrays.asList(base, sum - base)));
                }

                return combos;
            }
        }
    }

    public static void print(List<LinkedList<Integer>> combos) {
        System.out.println(
            "Combinations summing to: " + sum((LinkedList<Integer>) combos.get(0))
        );

        for (LinkedList<Integer> combo : combos) {
            StringBuilder line = new StringBuilder();
            boolean comma = false;

            for (Integer i : combo) {
                if (!comma) {
                    comma = true;
                } else {
                    line.append(',');
                }

                line.append(i);
            }

            System.out.println(line);
        }
    }

    public static List<LinkedList<Integer>>
    removeNotContaining(List<LinkedList<Integer>> combos, int required) {
        Iterator<LinkedList<Integer>> iterator = combos.iterator();

    label23:
        while (iterator.hasNext()) {
            LinkedList<Integer> combo = (LinkedList) iterator.next();

            for (Integer i : combo) {
                if (i == required) {
                    continue label23;
                }
            }

            iterator.remove();
        }

        return combos;
    }

    private static int sum(LinkedList<Integer> combo) {
        int sum = 0;

        for (Integer i : combo) {
            sum += i;
        }

        return sum;
    }
}
