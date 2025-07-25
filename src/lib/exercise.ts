import * as v from 'valibot';
import { exercises } from './state.svelte';

export const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));
export const IsoTimestamp = v.pipe(v.string(), v.isoTimestamp('The timestamp is badly formatted.'));
export const ExerciseNote = v.object({
	type: v.literal('note'),
	id: Id,
	createdAt: IsoTimestamp,
	content: v.fallback(v.string(), '')
});
export const ExerciseSet = v.object({
	type: v.literal('set'),
	id: Id,
	createdAt: IsoTimestamp
});
export const ExerciseEvent = v.variant('type', [ExerciseNote, ExerciseSet]);

export const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.fallback(v.string(), ''),
	events: v.fallback(v.record(Id, ExerciseEvent), {})
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
		content: content ?? ''
	};
}

export function persistExerciseNote(exercise: Exercise, exerciseNote: ExerciseNote): void {
	exercises[exercise.id].events[exerciseNote.id] = exerciseNote;
}

export function deleteExerciseNote(exercise: Exercise, exerciseNote: ExerciseNote): void {
	delete exercise.events[exerciseNote.id];
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
		events: {}
	};
}

export function persistExercise(exercise: Exercise): void {
	exercises[exercise.id] = exercise;
}

export function deleteExercise(exercise: Exercise): void {
	delete exercises[exercise.id];
}
