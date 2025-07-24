import { browser } from '$app/environment';
import { getContext, setContext } from 'svelte';
import * as v from 'valibot';

const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));

const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.string()
});

const Exercises = v.record(v.string(), Exercise);

export type Id = v.InferInput<typeof Id>;
export type Exercise = v.InferInput<typeof Exercise>;
export type Exercises = v.InferInput<typeof Exercises>;

const STATE_STORAGE_KEY = 'STATE_SAVE';

export function save(exercises: Exercises): void {
	localStorage.setItem(STATE_STORAGE_KEY, JSON.stringify(exercises));
}

export function load(): Exercises | null {
	const saved = localStorage.getItem(STATE_STORAGE_KEY);
	if (saved === null) return null;

	try {
		const savedState = JSON.parse(saved);
		const result = v.safeParse(Exercises, savedState);
		if (!result.success) {
			console.error('Could not parse saved game state', v.flatten<typeof Exercises>(result.issues));

			return null;
		}

		return result.output;
	} catch (e) {
		console.error('Could not parse saved state', e);
		localStorage.removeItem(STATE_STORAGE_KEY);

		return null;
	}
}

export type State = {
	exercises: Exercises;
};

export const exercises: Exercises = $state(
	(() => {
		if (!browser) return {};

		const saved = load();
		return saved !== null ? saved : {};
	})()
);
