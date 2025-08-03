import * as v from 'valibot';
import { ExerciseEvent, ExerciseNote, ExerciseSet } from './events';
import { Id } from '$lib/schema';
import { ExerciseRepsUnitType } from './reps';

export const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.fallback(v.string(), ''),
	events: v.fallback(v.record(Id, ExerciseEvent), {}),
	repsUnit: v.fallback(ExerciseRepsUnitType, 'reps'),
});
export type Exercise = v.InferOutput<typeof Exercise>;
export { ExerciseEvent, ExerciseNote, ExerciseSet };
