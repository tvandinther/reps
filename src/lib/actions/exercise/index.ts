import { exercises } from '../../state.svelte';
import { Exercise } from '$lib/schema';

export type NewExerciseParameters = {
	name?: string;
	description?: string;
};

export function newExercise({ name, description }: NewExerciseParameters = {}): Exercise {
	return {
		id: crypto.randomUUID(),
		name: name ?? '',
		description: description ?? '',
		events: {},
		repsUnit: 'reps',
	};
}

export function persistExercise(exercise: Exercise): void {
	exercises[exercise.id] = exercise;
}

export function deleteExercise(exercise: Exercise): void {
	delete exercises[exercise.id];
}

export * from './events';
export * from './reps';
