import { Id, IsoTimestamp } from '$lib/schema/util';
import * as v from 'valibot';

export const ExerciseNote = v.object({
	type: v.literal('note'),
	id: Id,
	createdAt: IsoTimestamp,
	content: v.fallback(v.string(), ''),
});
export type ExerciseNote = v.InferOutput<typeof ExerciseNote>;
