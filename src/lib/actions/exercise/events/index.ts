import { Exercise, ExerciseEvent } from '$lib/schema';
import { exercises } from '$lib/state.svelte';

export function persistExerciseEvent(exercise: Exercise, exerciseEvent: ExerciseEvent): void {
	exercises[exercise.id].events[exerciseEvent.id] = exerciseEvent;
}

export function deleteExerciseEvent(exercise: Exercise, exerciseEvent: ExerciseEvent): void {
	delete exercise.events[exerciseEvent.id];
}

export * from './notes';
export * from './sets';
