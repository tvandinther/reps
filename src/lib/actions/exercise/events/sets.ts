import { ExerciseSet } from '$lib/schema';

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
