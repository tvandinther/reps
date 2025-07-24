// place files you want to import through the `$lib` alias in this folder.

import { type Exercises, type Id } from './state.svelte';

export type AddExerciseParameters = {
	name: string;
	description: string;
};

export function addExercise(exercises: Exercises, parameters: AddExerciseParameters): Id | null {
	const trimmed = parameters.name.trim();
	if (trimmed.length === 0) return null;

	const id = crypto.randomUUID();
	exercises[id] = { id: id, name: trimmed, description: parameters.description.trim() };
	return id;
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
