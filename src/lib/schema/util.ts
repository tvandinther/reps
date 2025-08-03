import * as v from 'valibot';

export const Id = v.pipe(v.string(), v.uuid('The UUID is badly formatted.'));
export type Id = v.InferOutput<typeof Id>;

export const IsoTimestamp = v.pipe(v.string(), v.isoTimestamp('The timestamp is badly formatted.'));
