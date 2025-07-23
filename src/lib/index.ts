// place files you want to import through the `$lib` alias in this folder.

import { exercises } from './state.svelte';

export function addExercise(name: string) {
	const trimmed = name.trim();
	if (trimmed.length === 0) return;

	const id = crypto.randomUUID();
	exercises[id] = { id: id, name: trimmed };
}
