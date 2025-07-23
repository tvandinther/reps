// place files you want to import through the `$lib` alias in this folder.

import { exercises } from './state.svelte';

export function addExercise(name: string) {
	const trimmed = name.trim();
	if (trimmed.length === 0) return;

	const id = crypto.randomUUID();
	exercises[id] = { id: id, name: trimmed };
}

export function search(values: any[], searchString: string): any[];
export function search<T>(values: T[], searchString: string, getter: (x: T) => string): T[];
export function search<T>(
	values: T[],
	searchString: string,
	getter?: (x: T) => string
): T[] | any[] {
	const actualGetter: (x: T) => string = getter || ((x: any) => String(x));
	const results = values.filter((item) => actualGetter(item).includes(searchString));

	return results as T[] | any[];
}
