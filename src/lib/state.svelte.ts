export type Id = ReturnType<Crypto['randomUUID']>;
export type Exercise = {
	id: Id;
	name: string;
};
export type Exercises = Record<Id, Exercise>;

export const exercises = $state<Exercises>({});
