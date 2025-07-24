import { exercises } from '$lib/state.svelte';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ params }) => {
	return { exercise: exercises[params.id] };
};
