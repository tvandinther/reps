import * as v from 'valibot';
import { exercises } from './state.svelte';

export const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));

export const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.string()
});

export const Exercises = v.record(v.string(), Exercise);

export type Id = v.InferInput<typeof Id>;
export type Exercise = v.InferInput<typeof Exercise>;
export type Exercises = v.InferInput<typeof Exercises>;

export type NewExerciseParameters = {
	name?: string;
	description?: string;
};

export function newExercise({ name, description }: NewExerciseParameters): Exercise {
	return {
		id: crypto.randomUUID(),
		name: name ?? '',
		description: description ?? ''
	};
}

export function persistExercise(exercise: Exercise): void {
	exercises[exercise.id] = exercise;
}

export function deleteExercise(exercise: Exercise): void {
	delete exercises[exercise.id];
}
