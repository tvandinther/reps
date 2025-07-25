import type { PageLoad } from './$types';

export const load: PageLoad = async ({ url }) => {
	return { name: url.searchParams.get('name') ?? '' };
};
