import * as v from 'valibot';

export const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));
export const IsoTimestamp = v.pipe(v.string(), v.isoTimestamp('The timestamp is badly formatted.'));
export const ExerciseNote = v.object({
	type: v.literal('note'),
	id: Id,
	createdAt: IsoTimestamp,
	content: v.fallback(v.string(), ''),
});
export const ExerciseRepsUnit = v.object({
	type: v.string(),
	displayName: v.string(),
	shortName: v.string(),
});

export type ExerciseRepsUnit = v.InferInput<typeof ExerciseRepsUnit>;

export const ExerciseRepsUnits: ExerciseRepsUnit[] = [
	{
		type: 'reps',
		displayName: 'Reps',
		shortName: 'reps',
	},
	{
		type: 'seconds',
		displayName: 'Seconds',
		shortName: 's',
	},
] as const;

const exerciseRepsUnitMap: Record<ExerciseRepsUnit['type'], ExerciseRepsUnit> =
	ExerciseRepsUnits.reduce(
		(acc, x) =>
			Object.assign(acc, {
				[x.type]: x,
			}),
		{}
	);

export function getExerciseRepsUnitFromType(type: ExerciseRepsUnit['type']): ExerciseRepsUnit {
	return exerciseRepsUnitMap[type];
}
// export const ExerciseRepsUnitsSchema = v.variant('type');
// export type ExerciseRepsUnits = v.InferInput<typeof ExerciseRepsUnitsSchema>;
export const ExerciseRepsUnitType = v.union(ExerciseRepsUnits.map((x) => v.literal(x.type)));
export const ExerciseSet = v.object({
	type: v.literal('set'),
	id: Id,
	createdAt: IsoTimestamp,
	resistance: v.number(),
	reps: v.number(),
	repsUnitType: ExerciseRepsUnitType,
	exertionRating: v.number(),
});
export const ExerciseEvent = v.variant('type', [ExerciseNote, ExerciseSet]);

export const Exercise = v.object({
	id: Id,
	name: v.string(),
	description: v.fallback(v.string(), ''),
	events: v.fallback(v.record(Id, ExerciseEvent), {}),
	repsUnit: v.fallback(ExerciseRepsUnitType, 'reps'),
});

export const Exercises = v.record(Id, Exercise);
export type Id = v.InferInput<typeof Id>;
export type Exercise = v.InferInput<typeof Exercise>;
export type Exercises = v.InferInput<typeof Exercises>;
export type ExerciseEvent = v.InferInput<typeof ExerciseEvent>;
export type ExerciseNote = v.InferInput<typeof ExerciseNote>;
export type ExerciseSet = v.InferInput<typeof ExerciseSet>;
