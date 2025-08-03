import * as v from 'valibot';
import { Exercise } from './exercise';
import { Id } from './util';

export const Exercises = v.record(Id, Exercise);
export type Exercises = v.InferOutput<typeof Exercises>;

export * from './exercise';
export * from './util';
