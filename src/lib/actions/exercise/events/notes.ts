import { ExerciseNote } from '$lib/schema';

export type NewNoteParameters = {
	content?: string;
};
export function newExerciseNote({ content }: NewNoteParameters = {}): ExerciseNote {
	return {
		type: 'note',
		id: crypto.randomUUID(),
		createdAt: new Date().toISOString(),
		content: content ?? '',
	};
}
