import { browser } from '$app/environment';
import * as v from 'valibot';
import {} from './exercise';
import { Exercises } from './schema';

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
			console.warn(
				'Could not fully parse saved game state',
				v.flatten<typeof Exercises>(result.issues)
			);
			console.log(result);

			return Object.assign(initialExercises, result.output);
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

const initialExercises: Exercises = {};

export const exercises: Exercises = $state(
	(() => {
		if (!browser) return initialExercises;

		const saved = load();
		return saved !== null ? saved : initialExercises;
	})()
);
