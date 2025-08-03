import type { ExerciseEvent, ExerciseNote, ExerciseSet, Exercise } from './schema';
import { exercises } from './state.svelte';

export function isExerciseNote(exerciseEvent: ExerciseEvent): exerciseEvent is ExerciseNote {
	return exerciseEvent.type === 'note';
}

export function isExerciseSet(exerciseEvent: ExerciseEvent): exerciseEvent is ExerciseSet {
	return exerciseEvent.type === 'set';
}

export type NewNoteParameters = {
	content?: string;
};
export function newExerciseNote({ content }: NewNoteParameters = {}): ExerciseNote {
	return {
		type: 'note',
		id: crypto.randomUUID(),
		createdAt: new Date().toISOString(),
		content: content ?? '',
	};
}

export type NewSetParameters = {
	resistance?: number;
	repsUnitType: ExerciseSet['repsUnitType'];
};
export function newExerciseSet({ resistance, repsUnitType }: NewSetParameters): ExerciseSet {
	return {
		type: 'set',
		id: crypto.randomUUID(),
		createdAt: new Date().toISOString(),
		resistance: resistance ?? 0,
		reps: 0,
		exertionRating: 1,
		repsUnitType: repsUnitType,
	};
}

export function persistExerciseEvent(exercise: Exercise, exerciseEvent: ExerciseEvent): void {
	exercises[exercise.id].events[exerciseEvent.id] = exerciseEvent;
}

export function deleteExerciseEvent(exercise: Exercise, exerciseEvent: ExerciseEvent): void {
	delete exercise.events[exerciseEvent.id];
}

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
