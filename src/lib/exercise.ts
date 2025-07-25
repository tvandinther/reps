import * as v from 'valibot';
import { exercises } from './state.svelte';

export const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));
export const IsoTimestamp = v.pipe(v.string(), v.isoTimestamp('The timestamp is badly formatted.'));
export const ExerciseNote = v.object({
	type: v.literal('note'),
	id: Id,
	createdAt: IsoTimestamp,
	content: v.fallback(v.string(), ''),
});
export const ExerciseSet = v.object({
	type: v.literal('set'),
	id: Id,
	createdAt: IsoTimestamp,
	resistance: v.number(),
	reps: v.number(),
	repsUnit: v.literal('reps'),
	exertionRating: v.number(),
});
export const ExerciseEvent = v.variant('type', [ExerciseNote, ExerciseSet]);

export const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.fallback(v.string(), ''),
	events: v.fallback(v.record(Id, ExerciseEvent), {}),
});

export const Exercises = v.record(Id, Exercise);

export type Id = v.InferInput<typeof Id>;
export type Exercise = v.InferInput<typeof Exercise>;
export type Exercises = v.InferInput<typeof Exercises>;
export type ExerciseEvent = v.InferInput<typeof ExerciseEvent>;
export type ExerciseNote = v.InferInput<typeof ExerciseNote>;
export type ExerciseSet = v.InferInput<typeof ExerciseSet>;

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
	repsUnit: ExerciseSet['repsUnit'];
};
export function newExerciseSet({ resistance, repsUnit }: NewSetParameters): ExerciseSet {
	return {
		type: 'set',
		id: crypto.randomUUID(),
		createdAt: new Date().toISOString(),
		resistance: resistance ?? 0,
		reps: 0,
		exertionRating: 0,
		repsUnit: repsUnit,
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
	};
}

export function persistExercise(exercise: Exercise): void {
	exercises[exercise.id] = exercise;
}

export function deleteExercise(exercise: Exercise): void {
	delete exercises[exercise.id];
}
