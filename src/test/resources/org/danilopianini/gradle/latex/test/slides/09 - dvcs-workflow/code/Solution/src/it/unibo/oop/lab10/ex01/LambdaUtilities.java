package it.unibo.oop.lab10.ex01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Questa classe dovrà contenere 4 funzioni di utilità su liste e mappe, di
 * cui la prima è fornita a titolo di esempio. Tutte queste hanno come secondo
 * argomento una interfaccia funzionale di libreria (package java.util.function)
 * da trovare, così che potranno essere chiamate con delle lambda, come mostrato
 * dal main in fondo (da "scommentare"). Realizzare i tre metodi statici qui
 * sotto senza fare uso degli stream, ma possibilmente usando il più possibile
 * le funzionalità delle lambda, secondo le indicazioni fornite.
 * 
 * @author Mirko Viroli
 * @author Danilo Pianini
 *
 */
public final class LambdaUtilities {

	private LambdaUtilities() {
	}

	/**
	 * @param list
	 *            the input list
	 * @param op
	 *            the process to run on each element
	 * @param <T>
	 *            element type
	 * @return a new list containing, for each element of list, the element and
	 *         a processed version
	 */
	public static <T> List<T> dup(final List<T> list, final UnaryOperator<T> op) {
		final List<T> l = new ArrayList<>(list.size() * 2);
		list.forEach(t -> {
			l.add(t);
			l.add(op.apply(t));
		});
		return l;
	}

	/**
	 * @param list
	 *            input list
	 * @param pre
	 *            predicate to execute
	 * @param <T>
	 *            element type
	 * @return a list that intervals each element of the input list with an
	 *         element computed passing the value to the predicate
	 */
	public static <T> List<Optional<T>> optFilter(final List<T> list, final Predicate<T> pre) {
		/*
		 * Suggerimento: valutare l'uso di Optional.filter
		 */
		final List<Optional<T>> l = new ArrayList<>();
		list.forEach(t -> {
			l.add(Optional.ofNullable(t).filter(pre));
		});
		return l;
	}

	/**
	 * @param list
	 *            input list
	 * @param op
	 *            a function that, for each element, computes a key
	 * @param <T>
	 *            element type
	 * @param <R>
	 *            key type
	 * @return a map that groups into categories each element of the input list,
	 *         based on the mapping done by the function
	 */
	public static <R, T> Map<R, Set<T>> group(final List<T> list, final Function<T, R> op) {
		/*
		 * Suggerimento: valutare l'uso di Map.merge
		 */
		final Map<R, Set<T>> map = new HashMap<>();
		list.forEach(t -> {
			map.merge(op.apply(t), new HashSet<>(Arrays.asList(t)), (t1, t2) -> {
				t1.addAll(t2);
				return t1;
			});
		});
		return map;
	}

	/**
	 * @param map
	 *            input map
	 * @param def
	 *            the supplier
	 * @param <V>
	 *            element type
	 * @param <K>
	 *            key type
	 * @return a map whose non present values are filled with the value provided
	 *         by the supplier
	 */
	public static <K, V> Map<K, V> fill(final Map<K, Optional<V>> map, final Supplier<V> def) {
		/*
		 * Suggerimento: valutare l'uso di Optional.orElse
		 * 
		 * Si ricordi che si può iterare una mappa col suo metodo forEach
		 */
		final Map<K, V> m = new HashMap<>();
		map.forEach((k, v) -> m.put(k, v.orElse(def.get())));
		return m;
	}

	/**
	 * @param args
	 *            ignored
	 */
	public static void main(final String[] args) {

		final List<Integer> li = IntStream.range(1, 8).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList());

		System.out.println(dup(li, x -> x + 100));
		/*
		 * [1, 101, 2, 102, 3, 103, 4, 104, 5, 105, 6, 106, 7, 107]
		 */

		System.out.println(group(li, x -> x % 2 == 0 ? "pari" : "dispari"));
		/*
		 * {dispari=[1, 3, 5, 7], pari=[2, 4, 6]}
		 */

		final List<Optional<Integer>> opt = optFilter(li, x -> x % 3 == 0);
		System.out.println(opt);
		/*
		 * [Optional.empty, Optional.empty, Optional[3], Optional.empty,
		 * Optional.empty, Optional[6], Optional.empty]
		 */

		final Map<Integer, Optional<Integer>> map = new HashMap<>();
		for (int i = 0; i < opt.size(); i++) {
			map.put(i, opt.get(i));
		}

		System.out.println(fill(map, () -> (int) (-Math.random() * 10)));
		/*
		 * {0=-2, 1=-7, 2=3, 3=-3, 4=-7, 5=6, 6=-3}
		 */

	}
}
