import * as v from 'valibot';
import { Id, IsoTimestamp } from '$lib/schema';
import { ExerciseRepsUnitType } from '../reps';

export const ExerciseSet = v.object({
	type: v.literal('set'),
	id: Id,
	createdAt: IsoTimestamp,
	resistance: v.number(),
	reps: v.number(),
	repsUnitType: ExerciseRepsUnitType,
	exertionRating: v.number(),
});
export type ExerciseSet = v.InferOutput<typeof ExerciseSet>;
