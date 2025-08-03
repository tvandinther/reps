import * as v from 'valibot';

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

export const exerciseRepsUnitMap: Record<ExerciseRepsUnit['type'], ExerciseRepsUnit> =
	ExerciseRepsUnits.reduce(
		(acc, x) =>
			Object.assign(acc, {
				[x.type]: x,
			}),
		{}
	);

// export const ExerciseRepsUnitsSchema = v.variant('type');
// export type ExerciseRepsUnits = v.InferInput<typeof ExerciseRepsUnitsSchema>;
export const ExerciseRepsUnitType = v.union(ExerciseRepsUnits.map((x) => v.literal(x.type)));
