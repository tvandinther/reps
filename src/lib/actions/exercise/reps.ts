import { ExerciseRepsUnit, exerciseRepsUnitMap } from '$lib/schema/exercise/reps';

export function getExerciseRepsUnitFromType(type: ExerciseRepsUnit['type']): ExerciseRepsUnit {
	return exerciseRepsUnitMap[type];
}
