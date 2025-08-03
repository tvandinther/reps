import * as v from 'valibot';
import { ExerciseNote } from './notes';
import { ExerciseSet } from './sets';

export const ExerciseEvent = v.variant('type', [ExerciseNote, ExerciseSet]);
export type ExerciseEvent = v.InferOutput<typeof ExerciseEvent>;
export { ExerciseNote, ExerciseSet };
