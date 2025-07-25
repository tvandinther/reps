import { goto } from '$app/navigation';

type UrlOptions = {
	fragment?: string;
	params?: Record<string, string>;
};

function buildUrl(opts: UrlOptions | null, ...path: string[]) {
	let url = '/';
	url += path.join('/');
	if (opts !== null) {
		if (opts.fragment !== undefined) {
			url += '#' + opts.fragment;
		}
		if (opts.params !== undefined) {
			const params = new URLSearchParams(opts.params);
			url += '?' + params.toString();
		}
	}

	return url;
}

const exercises = 'exercises' as const;
const settings = 'settings' as const;
const edit = 'edit' as const;

export const routes = {
	index: '/',
	exercises: {
		index: (name: string = '') => buildUrl({ params: { name: name } }, exercises),
		id: (id: string) => ({
			index: buildUrl(null, exercises, id),
			edit: {
				index: buildUrl(null, exercises, id, edit)
			}
		})
	},
	settings: {
		index: buildUrl(null, settings)
	}
};

export default routes;

export const root = {
	go: () => goto(routes.index),
	exercises: {
		go: (name: string = '') => goto(routes.exercises.index(name)),
		id: (id: string) => ({
			go: () => goto(routes.exercises.id(id).index),
			edit: {
				go: () => goto(routes.exercises.id(id).edit.index)
			}
		})
	},
	settings: {
		go: () => goto(routes.settings.index)
	}
};
